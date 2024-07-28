package com.hcmute.utezbe.service;

import com.hcmute.utezbe.auth.AuthService;
import com.hcmute.utezbe.entity.Quiz;
import com.hcmute.utezbe.entity.embeddedId.QuizId;
import com.hcmute.utezbe.entity.enumClass.Role;
import com.hcmute.utezbe.exception.AccessDeniedException;
import com.hcmute.utezbe.repository.QuizRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
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

    public Optional<Quiz> findById(Long id, Long moduleId) {
        return quizRepository.findById(id, moduleId);
    }

    public List<Quiz> findAllByModuleId(Long moduleId) {
        return quizRepository.findAllByModuleId(moduleId);
    }

    public Quiz saveQuiz(Quiz quiz) {
        if (!AuthService.isUserHaveRole(Role.TEACHER) && !AuthService.isUserHaveRole(Role.ADMIN)) {
            throw new AccessDeniedException("You do not have permission to do this action!");
        }
        Long maxId = quizRepository.findMaxIdByModuleId(quiz.getModule().getId());
        if (maxId == null) {
            maxId = 1L;
        } else {
            maxId++;
        }
        quiz.setId(new QuizId(maxId, quiz.getModule().getId()));
        return quizRepository.save(quiz);
    }

    public Quiz saveQuiz(Long id, Map<String, Object> fields) {
        if (!AuthService.isUserHaveRole(Role.TEACHER) && !AuthService.isUserHaveRole(Role.ADMIN)) {
            throw new AccessDeniedException("You do not have permission to do this action!");
        }
        Optional<Quiz> quizOtp = quizRepository.findById(id);
        if (quizOtp.isPresent()) {
            Quiz quiz = quizOtp.get();
            fields.forEach((k, v) -> {
                Field field = ReflectionUtils.findField(Quiz.class, k);
                if (field != null) {
                    field.setAccessible(true);
                    if (field.getType().equals(Role.class)) {
                        Role role = Role.valueOf((String) v);
                        ReflectionUtils.setField(field, quiz, role);
                    } else {
                        ReflectionUtils.setField(field, quiz, v);
                    }
                }
            });
            quizRepository.save(quiz);
            return quiz;
        }
        return null;
    }

}
