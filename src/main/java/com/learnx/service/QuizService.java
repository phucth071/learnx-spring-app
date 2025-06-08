package com.learnx.service;

import com.learnx.auth.AuthService;
import com.learnx.entity.Quiz;
import com.learnx.entity.User;
import com.learnx.entity.enumClass.Role;
import com.learnx.exception.AccessDeniedException;
import com.learnx.exception.ResourceNotFoundException;
import com.learnx.repository.QuizRepository;
import com.learnx.request.CreateQuizRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class QuizService {

    final private QuizRepository quizRepository;

    public List<Quiz> findAll() {
        return quizRepository.findAll();
    }

    public Optional<Quiz> findById(Long id) {
        return quizRepository.findById(id);
    }

    public List<Quiz> findAllByModuleId(Long id) {
        return quizRepository.findAllByModuleId(id);
    }

    public Quiz saveQuiz(Quiz quiz) {
        if (AuthService.isUserNotHaveRole(Role.TEACHER) && AuthService.isUserNotHaveRole(Role.ADMIN)) {
            throw new AccessDeniedException("You do not have permission to do this action!");
        }
        return quizRepository.save(quiz);
    }

    @Transactional
    public int deleteQuiz(Long id) {
        if (AuthService.isUserNotHaveRole(Role.TEACHER) && AuthService.isUserNotHaveRole(Role.ADMIN)) {
            throw new AccessDeniedException("You do not have permission to do this action!");
        }
        Optional<Quiz> quizOtp = quizRepository.findById(id);
        if (quizOtp.isPresent()) {
            Quiz quiz = quizOtp.get();
            quizRepository.delete(quiz);
            return 1;
        }
        return 0;
    }

    public Quiz updateQuiz(Long id, CreateQuizRequest request) throws ParseException {
        if (AuthService.isUserNotHaveRole(Role.TEACHER) && AuthService.isUserNotHaveRole(Role.ADMIN)) {
            throw new AccessDeniedException("You do not have permission to do this action!");
        }
        Optional<Quiz> quizOtp = quizRepository.findById(id);
        if (quizOtp.isPresent()) {
            Quiz quiz = getQuiz(request, quizOtp);
            if (request.getStartDate() != null) {
                quiz.setStartDate(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(request.getStartDate()));
            }
            if (request.getEndDate() != null) {
                quiz.setEndDate(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(request.getEndDate()));
            }
            if (request.getTimeLimit() != 0) {
                quiz.setTimeLimit(request.getTimeLimit());
            }
            if (request.getAttemptAllowed() != 0) {
                quiz.setAttemptAllowed(request.getAttemptAllowed());
            }
            if (request.getDescription() != null) {
                quiz.setDescription(request.getDescription());
            }
            if (request.isShuffled() != quiz.isShuffled()) {
                quiz.setShuffled(request.isShuffled());
            }

            return quizRepository.save(quiz);
        }
        return null;
    }

    public List<Quiz> getAllQuizzesByStudentIdAndEndDateMonthYear(int month, int year) {
        User user = AuthService.getCurrentUser();
        return quizRepository.findAllByEmailAndEndDateMonthYear(user.getEmail(), month, year);
    }

    public List<Quiz> getAllQuizzesByTeacherIdAndEndDateMonthYear(int month, int year) {
        User user = AuthService.getCurrentUser();
        return quizRepository.findAllByTeacherIdAndEndDateMonthYear(user.getId(), month, year);
    }

    private static Quiz getQuiz(CreateQuizRequest request, Optional<Quiz> quizOtp) throws ParseException {
        Quiz quiz = quizOtp.get();
        if (request.getTitle() != null) quiz.setTitle(request.getTitle());
        if (request.getStartDate() != null) quiz.setStartDate(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(request.getStartDate()));
        if (request.getEndDate() != null) quiz.setEndDate(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(request.getEndDate()));
        if (request.getTimeLimit() != 0) quiz.setTimeLimit(request.getTimeLimit());
        if (request.getAttemptAllowed() != 0) quiz.setAttemptAllowed(request.getAttemptAllowed());
        if (request.getDescription() != null) quiz.setDescription(request.getDescription());
        if (request.isShuffled() != quiz.isShuffled()) quiz.setShuffled(request.isShuffled());
        return quiz;
    }

}
