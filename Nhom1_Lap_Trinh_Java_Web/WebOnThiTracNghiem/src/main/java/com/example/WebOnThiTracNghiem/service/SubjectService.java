package com.example.WebOnThiTracNghiem.service;

import com.example.WebOnThiTracNghiem.model.Subject;
import com.example.WebOnThiTracNghiem.repository.SubjectRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class SubjectService {
    private final SubjectRepository subjectRepository;

    public List<Subject> getAllSubjects() {
        return subjectRepository.findAll();
    }

    public void addSubject(Subject subject) {
        subjectRepository.save(subject);
    }
    public Optional<Subject> getSubjectById(Long id) {
        return subjectRepository.findById(id);
    }
    public void deleteSubjectById(Long id) {
        if (!subjectRepository.existsById(id)) {
            throw new IllegalStateException("Subject with ID " + id + " does not exist.");
        }
        subjectRepository.deleteById(id);
    }
    public void updateSubjectById(@NotNull Long id, @NotNull Subject updatedSubject) {
        Subject subjectToUpdate = subjectRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Subject with ID " + id + " does not exist."));

        // Update the fields of the existing subject entity
        subjectToUpdate.setName(updatedSubject.getName());

        // Save the updated subject entity
        subjectRepository.save(subjectToUpdate);
    }

    public Subject getSubjectByIdSubNum(Long idSubject) {
        return subjectRepository.findSubjectByIdSubject(idSubject);
    }
}
