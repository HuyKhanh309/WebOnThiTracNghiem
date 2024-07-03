package com.example.WebOnThiTracNghiem.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Subject")
public class Subject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idSubject;
    private String name;
    private Boolean isActive;
}
