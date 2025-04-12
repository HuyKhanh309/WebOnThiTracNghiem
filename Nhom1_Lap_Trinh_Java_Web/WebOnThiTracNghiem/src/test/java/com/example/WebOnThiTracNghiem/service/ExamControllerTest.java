package com.example.WebOnThiTracNghiem.service;

import com.example.WebOnThiTracNghiem.controller.ExamController;
import com.example.WebOnThiTracNghiem.model.Exam;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ExamControllerTest {

    @InjectMocks
    private ExamController examController;

    @Mock
    private ExamService examService;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private Model model;

    @Test
    void testAddExam_WhenHasErrors_ShouldRedirectToAddForm() {
        // Given
        Exam exam = new Exam();
        when(bindingResult.hasErrors()).thenReturn(true);

        // When
        String result = examController.addExam(exam, bindingResult, model);

        // Then
        assertEquals("redirect:/admin/exams/add", result);
        verify(examService, never()).addExam(any());
    }

    @Test
    void testAddExam_WhenNoErrors_ShouldAddExamAndRedirectToList() {
        // Given
        Exam exam = new Exam();
        when(bindingResult.hasErrors()).thenReturn(false);

        // When
        String result = examController.addExam(exam, bindingResult, model);

        // Then
        assertEquals("redirect:/admin/exams", result);
        verify(examService, times(1)).addExam(exam);
    }

}
