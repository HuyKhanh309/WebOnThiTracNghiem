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

        // Debug
        System.out.println(">> hasErrors = true (mô phỏng validate không thành công)");

        // When
        String result = examController.addExam(exam, bindingResult, model);
        System.out.println(">> result returned: " + result);

        // Then
        assertEquals("redirect:/admin/exams/add", result);

        // Xác nhận không gọi service
        verify(examService, never()).addExam(any());

        // Xác nhận có add error vào model
        verify(model).addAttribute(eq("errorMessage"), contains("không hợp lệ"));
        System.out.println(">> model.addAttribute(errorMessage, ...) đã được gọi");
    }

    @Test
    void testAddExam_WhenNoErrors_ShouldAddExamAndRedirectToList() {
        // Given
        Exam exam = new Exam();
        when(bindingResult.hasErrors()).thenReturn(false);

        // Debug
        System.out.println(">> hasErrors = false (mô phỏng validate thành công)");

        // When
        String result = examController.addExam(exam, bindingResult, model);
        System.out.println(">> result returned: " + result);

        // Then
        assertEquals("redirect:/admin/exams", result);
        verify(examService, times(1)).addExam(exam);
        System.out.println(">> examService.addExam was called with exam object");
    }

}
