package com.example.WebOnThiTracNghiem.repository;
import com.example.WebOnThiTracNghiem.model.PracticeExam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface PracticeExamRepository extends JpaRepository<PracticeExam, Long> {
}
