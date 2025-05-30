package com.learnx.service;

import com.learnx.auth.AuthService;
import com.learnx.entity.*;
import com.learnx.entity.enumClass.QuizSessionStatus;
import com.learnx.exception.ResourceNotFoundException;
import com.learnx.repository.QuizSessionRepository;
import com.learnx.repository.QuizSubmissionDetailRepository;
import com.learnx.repository.QuizSubmissionRepository;
import com.learnx.repository.UserRepository;
import com.learnx.request.CreateQuizSubmissionRequest;
import com.learnx.response.QuizSubmissionWithStudentInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuizSubmissionService {

    private final QuizSubmissionRepository quizSubmissionRepository;
    private final QuizSubmissionDetailRepository quizSubmissionDetailRepository;
    private final QuizSessionRepository quizSessionRepository;

    private final QuizSubmissionAnswerService quizSubmissionAnswerService;
    private final QuizService quizService;
    private final UserService userService;
    private final UserRepository userRepository;
    private final QuestionService questionService;

    public Optional<QuizSubmission> getQuizSubmissionById(Long Id) {
        Optional<QuizSubmission> quizSubmission = quizSubmissionRepository.findById(Id);
        if (quizSubmission.isEmpty()) {
            throw new ResourceNotFoundException("QuizSubmission with id " + Id + " not found!");
        }
        return quizSubmission;
    }

    public List<QuizSubmission> getQuizSubmissionsByStudentId(Long studentId) {
        return quizSubmissionRepository.findAllByStudentId(studentId);
    }

    public List<QuizSubmission> getAllQuizSubmissions() {
        return quizSubmissionRepository.findAll();
    }

    public List<QuizSubmission> getQuizSubmissionByQuizIdAndStudentId(Long quizId, Long studentId) {
        return quizSubmissionRepository.findAllByQuizIdAndStudentId(quizId, studentId);
    }

    public List<QuizSubmissionWithStudentInfoResponse> getQuizSubmissionsByQuizId(Long quizId) {
        // Fetch submissions with student information eagerly loaded
        List<QuizSubmission> allSubmissions = quizSubmissionRepository.findAllByQuizId(quizId);

        // Group submissions by student ID and find the one with highest score for each student
        Map<Long, QuizSubmission> highestSubmissionsByStudent = new HashMap<>();

        for (QuizSubmission submission : allSubmissions) {
            Long studentId = submission.getStudent().getId();
            if (!highestSubmissionsByStudent.containsKey(studentId) ||
                    submission.getScore() > highestSubmissionsByStudent.get(studentId).getScore()) {
                highestSubmissionsByStudent.put(studentId, submission);
            }
        }

        // Create a new list with the highest submissions
        List<QuizSubmission> result = new ArrayList<>(highestSubmissionsByStudent.values());
        List<QuizSubmissionWithStudentInfoResponse> responseList = new ArrayList<>();
        // Explicitly load student data for each submission
        result.forEach(submission -> {
            QuizSubmissionWithStudentInfoResponse qs = QuizSubmissionWithStudentInfoResponse.builder()
                    .quizSubmission(submission)
                    .email(submission.getStudent().getEmail())
                    .build();
            responseList.add(qs);
        });

        return responseList;
    }

    public QuizSubmission deleteQuizSubmission(Long Id) {
        Optional<QuizSubmission> quizSubmission = quizSubmissionRepository.findById(Id);
        quizSubmission.ifPresent(qS -> {
            List<QuizSubmissionDetail> quizSubmissionDetails = quizSubmissionDetailRepository.findAllByQuizSubmissionId(qS.getId());
            quizSubmissionDetailRepository.deleteAll(quizSubmissionDetails);
            quizSubmissionRepository.delete(qS);
        });
        return quizSubmission.orElse(null);
    }

    @Transactional
    public QuizSubmission createQuizSubmission(CreateQuizSubmissionRequest requestDTO) {
        Quiz quiz = quizService.findById(requestDTO.getQuizId())
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found with id: " + requestDTO.getQuizId()));

        User student = AuthService.getCurrentUser();
        Long studentId = student.getId();

        // Check if student is enrolled in the quiz's module
        if (quiz.getModule().getCourse().getCourseRegistrations().stream()
                .noneMatch(registration -> registration.getEmail().equals(student.getEmail()))) {
            throw new ResourceNotFoundException("Student with id " + studentId + " is not enrolled in the course for this quiz.");
        }

        List<QuizSubmission> previousSubmissions = quizSubmissionRepository.findAllByQuizIdAndStudentId(quiz.getId(), studentId);
        int attemptsMade = previousSubmissions.size();
        int attemptsAllowed = quiz.getAttemptAllowed();

        if (attemptsMade > attemptsAllowed) {
            throw new ResourceNotFoundException("You have already used all " + attemptsAllowed + " attempts for this quiz.");
        }

        // Get the active session for this quiz
        Optional<QuizSession> activeSessionOpt = quizSessionRepository.findByStudentIdAndQuizIdAndStatus(
                studentId, quiz.getId(), QuizSessionStatus.ACTIVE);

        QuizSession activeSession = quizSessionRepository.findByStudentIdAndQuizIdAndStatus(
                        studentId, quiz.getId(), QuizSessionStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("No active quiz session found. Please start the quiz first."));

        // Get the submission associated with the active session
        QuizSubmission quizSubmission = activeSession.getSubmission();
        if (quizSubmission == null) {
            throw new ResourceNotFoundException("No submission found for this session. Please restart the quiz.");
        }

        // Process each question submission
        List<QuizSubmissionDetail> details = new ArrayList<>();
        int totalCorrects = 0;

        for (Map.Entry<Long, List<String>> entry : requestDTO.getAnswers().entrySet()) {
            Long questionId = entry.getKey();
            List<String> answerIds = entry.getValue();

            Question question = questionService.findById(questionId);

            // Create submission detail
            QuizSubmissionDetail detail = QuizSubmissionDetail.builder()
                    .quizSubmission(quizSubmission)
                    .question(question)
                    .build();


            detail = quizSubmissionDetailRepository.save(detail);

            // Create submission answers
            QuizSubmissionDetail finalDetail = detail;
            boolean isCorrect = checkIfAnswerIsCorrect(question, answerIds);

            List<QuizSubmissionAnswer> answers = answerIds.stream()
                    .map(answerId -> QuizSubmissionAnswer.builder()
                            .answerId(answerId)
                            .quizSubmissionDetail(finalDetail)
                            .isCorrect(isCorrect)
                            .build())
                    .collect(Collectors.toList());

            quizSubmissionAnswerService.saveAll(answers);
            detail.setQuizSubmissionAnswers(answers);
            details.add(detail);

            // Here you would implement logic to check if the answer is correct
            // This depends on your Question and Answer structure
            // For now, I'll leave this as a placeholder

            if (isCorrect) {
                totalCorrects++;
            }
        }

        // Calculate score
        double score = calculateScore(totalCorrects, quiz.getQuizQuestions().size());
        quizSubmission.setScore(score);
        quizSubmission.setTotalCorrects(totalCorrects);
        quizSubmission.setAnswers(details);
        quizSubmission.setTotalTimeTakenInSeconds(requestDTO.getTotalTimeTakenInSeconds());

        // Save the quiz submission
        QuizSubmission savedSubmission = quizSubmissionRepository.save(quizSubmission);

        // Complete the active session
        activeSession.setStatus(QuizSessionStatus.COMPLETED);
        activeSession.setTotalTimeTakenInSeconds(requestDTO.getTotalTimeTakenInSeconds());
        quizSessionRepository.save(activeSession);

        return savedSubmission;
    }

    private boolean checkIfAnswerIsCorrect(Question question, List<String> submittedAnswerIds) {
        // If no answers submitted, it's incorrect
        if (submittedAnswerIds == null || submittedAnswerIds.isEmpty()) {
            return false;
        }

        // Get correct answers from question
        List<QuestionAnswer> correctAnswers = question.getAnswers();

        if (correctAnswers.isEmpty()) {
            return false; // No correct answers defined for this question
        }

        switch (question.getQuestionType()) {
            case SINGLE_CHOICE:
                // For single choice, there should be exactly one submitted answer
                if (submittedAnswerIds.size() != 1) {
                    return false;
                }

                // Check if the submitted answer matches one of the correct answers
                String submittedId = submittedAnswerIds.get(0);
                return correctAnswers.stream()
                        .anyMatch(answer -> answer.getAnswerId().equals(submittedId));

            case MULTIPLE_CHOICE:
                // For multiple choice, check if submitted answers match exactly with correct answers
                Set<String> submittedIds = new HashSet<>(submittedAnswerIds);
                Set<String> correctAnswerIds = correctAnswers.stream()
                        .map(QuestionAnswer::getAnswerId)
                        .collect(Collectors.toSet());
                System.out.println("MCQ CORRECT?:::" + submittedIds.equals(correctAnswerIds));
                // All correct options must be selected and no incorrect ones
                return submittedIds.equals(correctAnswerIds);

            case TRUE_FALSE:
                // For true/false, there should be exactly one submitted answer
                if (submittedAnswerIds.size() != 1) {
                    return false;
                }

                String selectedAnswer = submittedAnswerIds.get(0);
                return correctAnswers.stream()
                        .anyMatch(answer -> answer.getAnswerId().equals(selectedAnswer));

            case FILL_IN_THE_BLANK:
                // For fill-in-the-blank, compare the text (case-insensitive)
                if (submittedAnswerIds.size() != 1) {
                    return false;
                }

                String submittedText = submittedAnswerIds.get(0).trim();

                // Check if the submitted text matches any of the correct answers
                return correctAnswers.stream()
                        .anyMatch(answer -> answer.getAnswerContent() != null &&
                                answer.getAnswerContent().trim().equalsIgnoreCase(submittedText));

            default:
                return false;
        }
    }

    public QuizSubmission getQuizSubmissionByQuizSessionId(Long quizSessionId) {
        QuizSession quizSession = quizSessionRepository.findById(quizSessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz session not found with id: " + quizSessionId));

        QuizSubmission submission = quizSession.getSubmission();
        if (submission == null) {
            throw new ResourceNotFoundException("No submission found for this session.");
        }

        return submission;
    }

    private double calculateScore(int totalCorrects, int totalQuestions) {
        if (totalQuestions == 0) return 0;
        return (double) totalCorrects / totalQuestions * 100;
    }
}
