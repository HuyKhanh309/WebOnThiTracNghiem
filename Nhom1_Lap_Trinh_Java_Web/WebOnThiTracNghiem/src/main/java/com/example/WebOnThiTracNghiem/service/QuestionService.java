package com.example.WebOnThiTracNghiem.service;

import com.example.WebOnThiTracNghiem.model.Exam;
import com.example.WebOnThiTracNghiem.model.ExamQuestion;
import com.example.WebOnThiTracNghiem.model.Question;
import com.example.WebOnThiTracNghiem.repository.QuestionRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class QuestionService {
    private final QuestionRepository questionRepository;

    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }


    public Optional<Question> getQuestionById(Long id) {
        return questionRepository.findById(id);
    }
    public List<Question> getQuestionByListId(List<Long> id) {
        return questionRepository.findByIdQuestionIn(id);
    }



    public Question saveQuestion(Question question) {
        return questionRepository.save(question);
    }

    public void deleteQuestionById(Long id) {
        questionRepository.deleteById(id);
    }
    public void deleteQuestionByQuestion(List<Question> questions) {
        questionRepository.deleteAll(questions);
    }

    public void updateQuestionById(@NotNull Long id, @NotNull Question updateQuestion) {
        Question questionToUpdate = questionRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Question with ID " + id + " does not exist."));

        // Update the fields of the existing subject entity
        questionToUpdate.setNdQuestion(updateQuestion.getNdQuestion());
        questionToUpdate.setAnswer1(updateQuestion.getAnswer1());
        questionToUpdate.setAnswer2(updateQuestion.getAnswer2());
        questionToUpdate.setAnswer3(updateQuestion.getAnswer3());
        questionToUpdate.setAnswer4(updateQuestion.getAnswer4());
        questionToUpdate.setAnswer(updateQuestion.getAnswer());


        // Save the updated subject entity
        questionRepository.save(questionToUpdate);
    }

}
