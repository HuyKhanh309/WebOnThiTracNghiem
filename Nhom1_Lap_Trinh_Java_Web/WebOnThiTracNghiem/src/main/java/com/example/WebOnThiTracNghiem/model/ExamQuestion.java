package com.example.WebOnThiTracNghiem.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ExamQuestion")

public class ExamQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idExamQuestion;

    @ManyToOne
    @JoinColumn(name = "idQuestion")
    private Question question;
    @ManyToOne
    @JoinColumn(name = "idExam")
    private Exam exam;
    private Double point;
    private Boolean isActive;
}
