package com.example.WebOnThiTracNghiem.repository;
import com.example.WebOnThiTracNghiem.model.Exam;
import com.example.WebOnThiTracNghiem.model.ExamQuestion;
import com.example.WebOnThiTracNghiem.model.Question;
import com.example.WebOnThiTracNghiem.model.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    List<Question> findByIdQuestionIn(List<Long> ids);
    void deleteByIdQuestionIn(List<Question> questions);

}
