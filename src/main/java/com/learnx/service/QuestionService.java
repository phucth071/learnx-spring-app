package com.learnx.service;

import com.learnx.entity.*;
import com.learnx.entity.embeddedId.QuestionOptionId;
import com.learnx.entity.enumClass.QuestionType;
import com.learnx.exception.ResourceNotFoundException;
import com.learnx.repository.QuestionAnswerRepository;
import com.learnx.repository.QuestionRepository;
import com.learnx.repository.QuizRepository;
import com.learnx.request.CreateFITBRequest;
import com.learnx.request.CreateMCQRequest;
import com.learnx.request.CreateSCQRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final QuestionOptionService questionOptionService;
    private final QuizRepository quizRepository;
    private final QuestionAnswerRepository questionAnswerRepository;
    private final QuizQuestionService quizQuestionService;

    public Question findById(Long id) {
        Optional<Question> question = questionRepository.findById(id);
        if (question.isEmpty()) {
            throw new ResourceNotFoundException("Question with id " + id + " not found!");
        }
        return question.get();
    }

    public List<Question> getQuestionsByQuizId(Long quizId) {
        List<Question> questions = new ArrayList<>();
        List<QuizQuestion> quizQuestions = quizQuestionService.findAllByQuiz_Id(quizId);
        for (QuizQuestion quizQuestion : quizQuestions) {
            questions.add(quizQuestion.getQuestion());
        }
        return questions;
    }

    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }

    public List<Question> getQuestionsByType(String type) {
        QuestionType questionType = QuestionType.valueOf(type);
        return questionRepository.findAllByQuestionType(questionType);
    }

    @PreAuthorize("hasAnyAuthority('TEACHER', 'ADMIN')")
    @Transactional
    public Question saveMCQ(CreateMCQRequest req) {
        Quiz quiz = quizRepository.findById(req.getQuizId()).orElse(null);
        if (quiz == null) {
            throw new ResourceNotFoundException("Quiz with id " + req.getQuizId() + " not found!");
        }

        Question question = Question.builder()
                .content(req.getContent())
                .questionType(QuestionType.MULTIPLE_CHOICE)
                .score(req.getScore() == null ? 0.0 : req.getScore())
                .build();

        question = questionRepository.save(question);
        question.setOptions(new ArrayList<>());
        question.setAnswers(new ArrayList<>());
        int seq = 0;
        for (String o : req.getOptions()) {
            QuestionOption questionOption = QuestionOption.builder()
                    .content(o)
                    .question(question)
                    .seq(seq++)
                    .build();
            QuestionOptionId questionOptionId = QuestionOptionId.builder()
                    .questionId(question.getId())
                    .optionId(Math.abs(UUID.randomUUID().getMostSignificantBits()))
                    .build();
            questionOption.setId(questionOptionId);
            question.getOptions().add(questionOption);
            questionOptionService.save(questionOption);
        }

        for (Integer a : req.getAnswers()) {
            QuestionAnswer questionAnswer = QuestionAnswer.builder()
                    .question(question)
                    .answerId(question.getOptions().get(a).getId().getOptionId())
                    .build();
            question.getAnswers().add(questionAnswer);
            questionAnswerRepository.save(questionAnswer);
        }

        quizQuestionService.addNewQuestionToQuiz(req.getQuizId(), question);
        questionRepository.save(question);

        return question;
    }

    @PreAuthorize("hasAnyAuthority('TEACHER', 'ADMIN')")
    @Transactional
    public Question updateMCQ(Long id, CreateMCQRequest req) {
        Question question = questionRepository.findById(id).orElse(null);
        if (question == null) {
            throw new ResourceNotFoundException("Question with id " + id + " not found!");
        }

        if (req.getContent() != null) {
            question.setContent(req.getContent());
        }
        if (req.getScore() != null) {
            question.setScore(req.getScore());
        }

        if (req.getOptions() != null) {
            List<QuestionOption> options = new ArrayList<>();
            int seq = 0;
            for (String o : req.getOptions()) {
                QuestionOption questionOption = QuestionOption.builder()
                        .content(o)
                        .question(question)
                        .seq(seq++)
                        .build();
                QuestionOptionId questionOptionId = QuestionOptionId.builder()
                        .questionId(question.getId())
                        .optionId(Math.abs(UUID.randomUUID().getMostSignificantBits()))
                        .build();
                questionOption.setId(questionOptionId);
                options.add(questionOption);
                questionOptionService.save(questionOption);
            }
            question.setOptions(options);
        }

        if (req.getAnswers() != null) {
            List<QuestionAnswer> answers = new ArrayList<>();
            questionAnswerRepository.deleteAll(question.getAnswers());
            for (Integer a : req.getAnswers()) {
                QuestionAnswer questionAnswer = QuestionAnswer.builder()
                        .question(question)
                        .answerId(question.getOptions().get(a).getId().getOptionId())
                        .build();
                answers.add(questionAnswer);
                questionAnswerRepository.save(questionAnswer);
            }
            question.setAnswers(answers);
        }
        questionRepository.save(question);
        return question;
    }

    @PreAuthorize("hasAnyAuthority('TEACHER', 'ADMIN')")
    @Transactional
    public Question saveSCQ(CreateSCQRequest req) {
        Quiz quiz = quizRepository.findById(req.getQuizId()).orElse(null);
        if (quiz == null) {
            throw new ResourceNotFoundException("Quiz with id " + req.getQuizId() + " not found!");
        }

        Question question = Question.builder()
                .content(req.getContent())
                .questionType(QuestionType.SINGLE_CHOICE)
                .score(req.getScore() == null ? 0.0 : req.getScore())
                .build();

        question = questionRepository.save(question);
        question.setOptions(new ArrayList<>());
        question.setAnswers(new ArrayList<>());
        int seq = 0;
        for (String o : req.getOptions()) {
            QuestionOption questionOption = QuestionOption.builder()
                    .content(o)
                    .question(question)
                    .seq(seq++)
                    .build();
            QuestionOptionId questionOptionId = QuestionOptionId.builder()
                    .questionId(question.getId())
                    .optionId(Math.abs(UUID.randomUUID().getMostSignificantBits()))
                    .build();
            questionOption.setId(questionOptionId);
            question.getOptions().add(questionOption);
            questionOptionService.save(questionOption);
        }

        QuestionAnswer questionAnswer = QuestionAnswer.builder()
                .question(question)
                .answerId(question.getOptions().get(req.getAnswer()).getId().getOptionId())
                .build();
        questionAnswerRepository.save(questionAnswer);

        question.setAnswers(new ArrayList<>(List.of(questionAnswer)));

        quizQuestionService.addNewQuestionToQuiz(req.getQuizId(), question);
        questionRepository.save(question);

        return question;
    }

    @PreAuthorize("hasAnyAuthority('TEACHER', 'ADMIN')")
    @Transactional
    public Question updateSCQ(Long id, CreateSCQRequest req) {
        Question question = questionRepository.findById(id).orElse(null);
        if (question == null) {
            throw new ResourceNotFoundException("Question with id " + id + " not found!");
        }

        if (req.getContent() != null) {
            question.setContent(req.getContent());
        }
        if (req.getScore() != null) {
            question.setScore(req.getScore());
        }

        if (req.getOptions() != null) {
            int seq = 0;
            List<QuestionOption> options = new ArrayList<>();
            for (String o : req.getOptions()) {
                QuestionOption questionOption = QuestionOption.builder()
                        .content(o)
                        .question(question)
                        .seq(seq++)
                        .build();
                QuestionOptionId questionOptionId = QuestionOptionId.builder()
                        .questionId(question.getId())
                        .optionId(Math.abs(UUID.randomUUID().getMostSignificantBits()))
                        .build();
                questionOption.setId(questionOptionId);
                options.add(questionOption);
                questionOptionService.save(questionOption);
            }
            question.setOptions(options);
        }

        if (req.getAnswer() != null) {
            questionAnswerRepository.deleteAll(question.getAnswers());
            QuestionAnswer questionAnswer = QuestionAnswer.builder()
                    .question(question)
                    .answerId(question.getOptions().get(req.getAnswer()).getId().getOptionId())
                    .build();
            questionAnswerRepository.save(questionAnswer);
            question.setAnswers(new ArrayList<>(List.of(questionAnswer)));
        }
        questionRepository.save(question);

        return question;
    }

    @PreAuthorize("hasAnyAuthority('TEACHER', 'ADMIN')")
    @Transactional
    public Question saveTFQ(CreateSCQRequest req) {
        Quiz quiz = quizRepository.findById(req.getQuizId()).orElse(null);
        if (quiz == null) {
            throw new ResourceNotFoundException("Quiz with id " + req.getQuizId() + " not found!");
        }

        Question question = Question.builder()
                .content(req.getContent())
                .questionType(QuestionType.TRUE_FALSE)
                .score(req.getScore() == null ? 0.0 : req.getScore())
                .build();

        question = questionRepository.save(question);
        question.setOptions(new ArrayList<>());
        question.setAnswers(new ArrayList<>());
        int seq = 0;
        for (String o : req.getOptions()) {
            QuestionOption questionOption = QuestionOption.builder()
                    .content(o)
                    .question(question)
                    .seq(seq++)
                    .build();
            QuestionOptionId questionOptionId = QuestionOptionId.builder()
                    .questionId(question.getId())
                    .optionId(Math.abs(UUID.randomUUID().getMostSignificantBits()))
                    .build();
            questionOption.setId(questionOptionId);
            question.getOptions().add(questionOption);
            questionOptionService.save(questionOption);
        }

        QuestionAnswer questionAnswer = QuestionAnswer.builder()
                .question(question)
                .answerId(question.getOptions().get(req.getAnswer()).getId().getOptionId())
                .build();
        questionAnswerRepository.save(questionAnswer);

        question.setAnswers(new ArrayList<>(List.of(questionAnswer)));

        quizQuestionService.addNewQuestionToQuiz(req.getQuizId(), question);
        questionRepository.save(question);

        return question;
    }

    @PreAuthorize("hasAnyAuthority('TEACHER', 'ADMIN')")
    @Transactional
    public Question updateTFQ(Long id, CreateSCQRequest req) {
        Question question = questionRepository.findById(id).orElse(null);
        if (question == null) {
            throw new ResourceNotFoundException("Question with id " + id + " not found!");
        }

        if (req.getContent() != null) {
            question.setContent(req.getContent());
        }
        if (req.getScore() != null) {
            question.setScore(req.getScore());
        }

        if (req.getOptions() != null) {
            int seq = 0;
            List<QuestionOption> options = new ArrayList<>();
            for (String o : req.getOptions()) {
                QuestionOption questionOption = QuestionOption.builder()
                        .content(o)
                        .question(question)
                        .seq(seq++)
                        .build();
                QuestionOptionId questionOptionId = QuestionOptionId.builder()
                        .questionId(question.getId())
                        .optionId(Math.abs(UUID.randomUUID().getMostSignificantBits()))
                        .build();
                questionOption.setId(questionOptionId);
                options.add(questionOption);
                questionOptionService.save(questionOption);
            }
            question.setOptions(options);
        }

        if (req.getAnswer() != null) {
            questionAnswerRepository.deleteAll(question.getAnswers());
            QuestionAnswer questionAnswer = QuestionAnswer.builder()
                    .question(question)
                    .answerId(question.getOptions().get(req.getAnswer()).getId().getOptionId())
                    .build();
            questionAnswerRepository.save(questionAnswer);
            question.setAnswers(new ArrayList<>(List.of(questionAnswer)));
        }
        questionRepository.save(question);

        return question;
    }

    @PreAuthorize("hasAnyAuthority('TEACHER', 'ADMIN')")
    @Transactional
    public Question saveFITB(CreateFITBRequest req) {
        Quiz quiz = quizRepository.findById(req.getQuizId()).orElse(null);
        if (quiz == null) {
            throw new ResourceNotFoundException("Quiz with id " + req.getQuizId() + " not found!");
        }

        Question question = Question.builder()
                .content(req.getContent())
                .questionType(QuestionType.FILL_IN_THE_BLANK)
                .score(req.getScore() == null ? 0.0 : req.getScore())
                .build();

        question = questionRepository.save(question);
        question.setAnswers(new ArrayList<>());

        QuestionAnswer questionAnswer = QuestionAnswer.builder()
                .question(question)
                .answerContent(req.getAnswerContent())
                .build();
        questionAnswerRepository.save(questionAnswer);

        question.setAnswers(new ArrayList<>(List.of(questionAnswer)));

        quizQuestionService.addNewQuestionToQuiz(req.getQuizId(), question);
        questionRepository.save(question);

        return question;
    }

    @PreAuthorize("hasAnyAuthority('TEACHER', 'ADMIN')")
    @Transactional
    public Question updateFITB(Long id, CreateFITBRequest req) {
        Question question = questionRepository.findById(id).orElse(null);
        if (question == null) {
            throw new ResourceNotFoundException("Question with id " + id + " not found!");
        }

        if (req.getContent() != null) {
            question.setContent(req.getContent());
        }
        if (req.getScore() != null) {
            question.setScore(req.getScore());
        }

        if (req.getAnswerContent() != null) {
            questionAnswerRepository.deleteAll(question.getAnswers());
            QuestionAnswer questionAnswer = QuestionAnswer.builder()
                    .question(question)
                    .answerContent(req.getAnswerContent())
                    .build();
            questionAnswerRepository.save(questionAnswer);
            question.setAnswers(new ArrayList<>(List.of(questionAnswer)));
        }
        questionRepository.save(question);
        return question;
    }

    @PreAuthorize("hasAnyAuthority('TEACHER', 'ADMIN')")
    public Question deleteQuestion(Long id) {
        Optional<Question> quizQuestion = questionRepository.findById(id);
        quizQuestion.ifPresent(questionRepository::delete);
        return quizQuestion.orElse(null);
    }

}
