Rpackage com.hcmute.utezbe.service;

import com.hcmute.utezbe.entity.QuizAnswer;
import com.hcmute.utezbe.entity.QuizSubmission;
import com.hcmute.utezbe.repository.QuizAnswerRepository;
import com.hcmute.utezbe.repository.QuizSubmissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class QuizSubmissionService {

    private final QuizSubmissionRepository quizSubmissionRepository;
    private final QuizAnswerRepository quizAnswerRepository;

    public Optional<QuizSubmission> getQuizSubmissionById(Long Id) {
        return quizSubmissionRepository.findById(Id);
    }

    public List<QuizSubmission> getAllQuizSubmissions() {
        return quizSubmissionRepository.findAll();
    }

    public QuizSubmission saveQuizSubmission(QuizSubmission quizSubmission) {
        return quizSubmissionRepository.save(quizSubmission);
    }

    public QuizSubmission deleteQuizSubmission(Long Id) {
        Optional<QuizSubmission> quizSubmission = quizSubmissionRepository.findById(Id);
        quizSubmission.ifPresent(qS -> {
            List<QuizAnswer> quizAnswers = quizAnswerRepository.findAllByQuizSubmissionId(qS.getId());
            quizAnswerRepository.deleteAll(quizAnswers);
            quizSubmissionRepository.delete(qS);
        });
        return quizSubmission.orElse(null);
    }

}
