package com.example.WebOnThiTracNghiem.repository;
import com.example.WebOnThiTracNghiem.model.AccountExam;
import com.example.WebOnThiTracNghiem.model.Exam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountExamRepository extends JpaRepository<AccountExam, Long>  {
    List<AccountExam> findByExamIn(List<Exam> exams);

    List<AccountExam> findByExam(Exam exam);
}
