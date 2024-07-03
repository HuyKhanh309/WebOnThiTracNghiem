package com.example.WebOnThiTracNghiem.repository;
import com.example.WebOnThiTracNghiem.model.Exam;
import com.example.WebOnThiTracNghiem.model.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Long> {
    Exam findByIdExam(Long id);
    void deleteBySubject(Subject subject);
    List<Exam> findBySubject(Subject Subject);
    List<Exam> findExamsBySubject_IdSubject(Long idSubject);
    // Phương thức tìm kiếm theo tên môn học
    List<Exam> findBySubjectName(String subjectName);

    // Phương thức tìm kiếm theo tên bài thi
    List<Exam> findByNoteContaining(String note);
}
