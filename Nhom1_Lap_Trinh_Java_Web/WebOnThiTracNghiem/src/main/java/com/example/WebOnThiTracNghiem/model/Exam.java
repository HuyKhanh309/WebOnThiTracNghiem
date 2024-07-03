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
@Table(name = "Exam")
public class Exam {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idExam;

    @ManyToOne
    @JoinColumn(name = "idSubject")
    private Subject subject;

    private Integer quantity;
    private Double price;
    private String note;
    private Boolean isActive;
}
