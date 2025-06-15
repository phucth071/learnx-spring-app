package com.learnx.service;

import com.learnx.dto.OutcomeDTO;
import com.learnx.entity.Course;
import com.learnx.entity.Outcome;
import com.learnx.exception.ResourceNotFoundException;
import com.learnx.repository.OutcomeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OutcomeService {
    private final OutcomeRepository outcomeRepository;

    @Transactional
    public Outcome create(Outcome outcome) {
        return outcomeRepository.save(outcome);
    }

    @Transactional
    public Set<Outcome> createOutcomesForCourse(List<OutcomeDTO> outcomeDTOs, Course course) {
        if (outcomeDTOs == null || outcomeDTOs.isEmpty()) {
            return new HashSet<>();
        }

        Set<Outcome> outcomes = outcomeDTOs.stream()
                .map(dto -> {
                    Outcome outcome = outcomeRepository.findByCode(dto.getCode())
                            .orElse(Outcome.builder()
                                    .code(dto.getCode())
                                    .description(dto.getDescription())
                                    .courses(new HashSet<>())
                                    .build());

                    outcome.getCourses().add(course);
                    return outcome;
                })
                .collect(Collectors.toSet());

        return new HashSet<>(outcomeRepository.saveAll(outcomes));
    }

    @Transactional
    public Outcome update(Long id, Outcome outcome) {
        if (!outcomeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Outcome not found with id: " + id);
        }
        outcome.setId(id);
        return outcomeRepository.save(outcome);
    }

    @Transactional
    public void delete(Long id) {
        outcomeRepository.deleteById(id);
    }

    public Optional<Outcome> getById(Long id) {
        return outcomeRepository.findById(id);
    }

    public Outcome getByCode(String code) {
        return outcomeRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Outcome not found with code: " + code));
    }

    public List<Outcome> getByCourseId(Long courseId) {
        return outcomeRepository.findByCourseId(courseId);
    }

    public List<Outcome> getByCourseCode(String courseCode) {
        return outcomeRepository.findByCourseCode(courseCode);
    }

    public List<Outcome> getAll() {
        return outcomeRepository.findAll();
    }
}
