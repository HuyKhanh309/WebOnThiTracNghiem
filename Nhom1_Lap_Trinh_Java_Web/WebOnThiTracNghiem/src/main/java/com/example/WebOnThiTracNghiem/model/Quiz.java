package com.example.WebOnThiTracNghiem.model;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Setter
@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public class Quiz {
    private Long idQuestion;
    private String question;
    private Map.Entry<String, String> answer1;
    private Map.Entry<String, String> answer2;
    private Map.Entry<String, String> answer3;
    private Map.Entry<String, String> answer4;
    private String answer;

}
