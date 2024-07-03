package com.example.WebOnThiTracNghiem.repository;
import com.example.WebOnThiTracNghiem.model.Exam;
import com.example.WebOnThiTracNghiem.model.ExamQuestion;
import com.example.WebOnThiTracNghiem.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExamQuestionRepository extends JpaRepository<ExamQuestion, Long> {
    List<ExamQuestion> findByExamIdExam(Long examId);
    List<ExamQuestion> findByQuestionAndExam(Question question, Exam exam);
    void deleteByQuestionAndExam(Question question, Exam exam);
    void deleteByQuestion(Optional<Question> question);
    List<ExamQuestion> findByQuestion(Question question);
    void deleteByExam(Exam exam);
    List<ExamQuestion> findExamQuestionByExam(Exam exam);
    List<ExamQuestion> findExamQuestionByExamIn(List<Exam> exam);

/*    List<ExamQuestion> findByExamId(Long examId);*/
    List<ExamQuestion> findByExam_IdExam(Long examId);

    List<ExamQuestion> findByQuestion_IdQuestion(Long questionId);
}
