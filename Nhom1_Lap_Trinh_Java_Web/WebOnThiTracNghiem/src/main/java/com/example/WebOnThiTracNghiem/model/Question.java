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
@Table(name = "Question")

public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idQuestion;
    private String ndQuestion;
    private String answer1;
    private String answer2;
    private String answer3;
    private String answer4;
    private String answer;
    private String note;
    private Boolean isActive;

}
