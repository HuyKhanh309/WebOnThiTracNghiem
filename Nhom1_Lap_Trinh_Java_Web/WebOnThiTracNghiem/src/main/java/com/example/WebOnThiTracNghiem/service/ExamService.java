package com.example.WebOnThiTracNghiem.service;

import com.example.WebOnThiTracNghiem.model.Exam;
import com.example.WebOnThiTracNghiem.model.Subject;
import com.example.WebOnThiTracNghiem.repository.ExamRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ExamService {
    private final ExamRepository examRepository;

    public List<Exam> getAllExams() {
        return examRepository.findAll();
    }

    public Exam findById(Long id) {
        Optional<Exam> exam = examRepository.findById(id);
        return exam.orElseThrow(() -> new IllegalArgumentException("Exam not found"));
    }
    public Exam loadExamById(Long id) {
        return examRepository.findByIdExam(id);
    }
    public void addExam(Exam exam) {
        examRepository.save(exam);
    }
    public Optional<Exam> getExamById(Long id) {
        return examRepository.findById(id);
    }
    public void deleteExamById(Long id) {
        if (!examRepository.existsById(id)) {
            throw new IllegalStateException("Subject with ID " + id + " does not exist.");
        }
        examRepository.deleteById(id);
    }
    public void updateExamById(@NotNull Long id, @NotNull Exam updatedExam) {
        Exam examToUpdate = examRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Exam with ID " + id + " does not exist."));

        // Update the fields of the existing subject entity
        examToUpdate.setNote(updatedExam.getNote());
        examToUpdate.setSubject(updatedExam.getSubject());
        examToUpdate.setPrice(updatedExam.getPrice());
        examToUpdate.setQuantity(updatedExam.getQuantity());

        // Save the updated subject entity
        examRepository.save(examToUpdate);
    }
    public void deleteExamBySubject(Subject subject) {

        examRepository.deleteBySubject(subject);
    }
    public List<Exam> getExamsBySubjectId(Subject idSubject) {
        return examRepository.findBySubject(idSubject);
    }
    public List<Exam> getExamsByIdSubject(Long idSubject) {
        return examRepository.findExamsBySubject_IdSubject(idSubject);
    }


}
