package com.example.WebOnThiTracNghiem.repository;
import com.example.WebOnThiTracNghiem.model.PracticeExamQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface PracticeExamQuestionRepository extends JpaRepository<PracticeExamQuestion, Long> {
}
