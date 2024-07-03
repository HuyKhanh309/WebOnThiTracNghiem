package com.example.WebOnThiTracNghiem.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "AccountExam")
public class AccountExam {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "idAccount")
    private Account account;

    @ManyToOne
    @JoinColumn(name = "idExam")
    private Exam exam;

    private float result;
}
