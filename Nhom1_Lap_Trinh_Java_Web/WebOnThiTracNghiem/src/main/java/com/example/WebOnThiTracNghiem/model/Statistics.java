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
@Table(name = "Statistics")
public class Statistics {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idStatistics;

    private String month;
    private String subject;
    private Integer quantityPracticeExam;
    private Integer quantityExam;
    private Integer quantityAcess;
}
