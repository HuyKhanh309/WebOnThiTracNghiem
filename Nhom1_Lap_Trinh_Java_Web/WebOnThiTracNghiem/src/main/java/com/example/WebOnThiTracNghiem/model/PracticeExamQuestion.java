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
@Table(name = "PracticeExamQuestion")

public class PracticeExamQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPracticeQuestion;

    @ManyToOne
    @JoinColumn(name = "idQuestion")
    private Question question;
    @ManyToOne
    @JoinColumn(name = "idPractice")
    private Exam exam;
    private Double point;
    private Boolean isActive;
}
