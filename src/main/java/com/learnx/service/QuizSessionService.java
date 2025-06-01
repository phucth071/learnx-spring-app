package com.learnx.service;

import com.learnx.auth.AuthService;
import com.learnx.entity.*;
import com.learnx.entity.enumClass.QuizSessionStatus;
import com.learnx.exception.ResourceNotFoundException;
import com.learnx.repository.QuizRepository;
import com.learnx.repository.QuizSessionRepository;
import com.learnx.repository.QuizSubmissionRepository;
import com.learnx.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class QuizSessionService {

    private final QuizSessionRepository quizSessionRepository;
    private final QuizRepository quizRepository;
    private final UserRepository userRepository;
    private final QuizSubmissionRepository quizSubmissionRepository;

    @Transactional
    public QuizSession startQuizSession(Long quizId) {
        Long studentId = AuthService.getCurrentUser().getId();
        Optional<QuizSession> existingSession = quizSessionRepository.findByStudentIdAndQuizIdAndStatus(
                studentId, quizId, QuizSessionStatus.ACTIVE);

        if (existingSession.isPresent()) {
            return existingSession.get();
        }

        // Get quiz and student
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found with id: " + quizId));

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + studentId));

        // Check if student is enrolled in the quiz's module
        if (quiz.getModule().getCourse().getCourseRegistrations().stream()
                .noneMatch(registration -> registration.getEmail().equals(student.getEmail()))) {
            throw new ResourceNotFoundException("Student with id " + studentId + " is not enrolled in the course for this quiz.");
        }

        List<QuizSubmission> previousSubmissions = quizSubmissionRepository.findAllByQuizIdAndStudentId(quiz.getId(), studentId);
        int attemptsMade = previousSubmissions.size();
        int attemptsAllowed = quiz.getAttemptAllowed();

        if (attemptsMade >= attemptsAllowed) {
            throw new ResourceNotFoundException("You have already used all " + attemptsAllowed + " attempts for this quiz.");
        }

        // Create new QuizSubmission
        QuizSubmission submission = new QuizSubmission();
        submission.setQuiz(quiz);
        submission.setStudent(student);
        submission.setTotalTimeTakenInSeconds(0);
        submission.setScore(0.00);
        QuizSubmission savedSubmission = quizSubmissionRepository.save(submission);

        // Create new session
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endTime = now.plusMinutes(quiz.getTimeLimit());

        QuizSession session = QuizSession.builder()
                .student(student)
                .quiz(quiz)
                .startTime(now)
                .endTime(endTime)
                .status(QuizSessionStatus.ACTIVE)
                .submission(savedSubmission)
                .build();

        return quizSessionRepository.save(session);
    }

    public List<QuizSession> getSessionCompletedByStudentIdAndQuizId(Long studentId, Long quizId) {
        List<QuizSession> sessions = quizSessionRepository.findAllByStudentIdAndQuizIdAndStatusIn(
                studentId, quizId, List.of(QuizSessionStatus.COMPLETED, QuizSessionStatus.EXPIRED));
        return sessions;
    }


    @Transactional
    public QuizSession completeQuizSession(Long sessionId) {
        QuizSession session = quizSessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz session not found with id: " + sessionId));

        // Calculate time taken
        LocalDateTime now = LocalDateTime.now();
        Duration timeSpent = Duration.between(session.getStartTime(), now);

        session.setTotalTimeTakenInSeconds((int) timeSpent.toSeconds());
        session.setStatus(QuizSessionStatus.COMPLETED);

        // The submission is already linked to the session when it was created
        // Update submission completion time if needed
        if (session.getSubmission() != null) {
            QuizSubmission submission = session.getSubmission();
            submission.setTotalTimeTakenInSeconds(Integer.parseInt(String.valueOf(timeSpent.toSeconds())));
            quizSubmissionRepository.save(submission);
        }

        return quizSessionRepository.save(session);
    }

    @Transactional(readOnly = true)
    public Optional<QuizSession> getActiveSession(Long quizId) {
        Long studentId = AuthService.getCurrentUser().getId();
        // Fetch the active quiz session for the current student and quiz
        return quizSessionRepository.findByStudentIdAndQuizIdAndStatus(
                studentId, quizId, QuizSessionStatus.ACTIVE);
    }

    @Transactional
    public QuizSession getSession(Long quizId) {
        Long studentId = AuthService.getCurrentUser().getId();

        // Expire the session if it end time has passed
        expireInactiveSessions();

        // Fetch the quiz session by quiz ID and student ID
        return quizSessionRepository.findByStudentIdAndQuizIdAndStatus(studentId, quizId, QuizSessionStatus.ACTIVE)
                .orElse(startQuizSession(quizId));
    }

    @Transactional
    public void expireInactiveSessions() {
        // Find sessions that have passed their end time but are still active
        LocalDateTime now = LocalDateTime.now();
        quizSessionRepository.findByStatusAndEndTimeBefore(QuizSessionStatus.ACTIVE, now)
                .forEach(session -> {
                    session.setStatus(QuizSessionStatus.EXPIRED);
                    quizSessionRepository.save(session);
                });
    }
}