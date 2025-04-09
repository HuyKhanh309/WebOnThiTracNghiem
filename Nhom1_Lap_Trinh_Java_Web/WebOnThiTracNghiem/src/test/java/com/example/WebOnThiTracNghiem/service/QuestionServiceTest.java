package com.example.WebOnThiTracNghiem.service;

import com.example.WebOnThiTracNghiem.model.Question;
import com.example.WebOnThiTracNghiem.repository.QuestionRepository;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class QuestionServiceTest {

    @Autowired
    private QuestionService questionService;

    @MockBean
    private QuestionRepository questionRepository;

    @RepeatedTest(10)
    public void updateQuestionById_questionExists_shouldUpdateQuestion(RepetitionInfo repetitionInfo) {
        // Arrange
        Long id = 3L;
        Question existingQuestion = new Question();
        existingQuestion.setIdQuestion(id);
        existingQuestion.setNdQuestion("Old Question");
        Question updateQuestion = new Question();
        updateQuestion.setNdQuestion("New Question");

        // Mock behavior
        when(questionRepository.findById(id)).thenReturn(Optional.of(existingQuestion));

        try {
            // Act
            questionService.updateQuestionById(id, updateQuestion);

            // Assert
            verify(questionRepository).save(existingQuestion);
            assertEquals("New Question", existingQuestion.getNdQuestion());

            // Print result of each test
            System.out.printf("✅ Test %s (Repetition %d/10) PASSED%n",
                    getTestInfo(), repetitionInfo.getCurrentRepetition());
        } catch (AssertionError e) {
            System.out.printf("❌ Test %s (Repetition %d/10) FAILED: %s%n",
                    getTestInfo(), repetitionInfo.getCurrentRepetition(), e.getMessage());
            throw e;
        }
    }

    @RepeatedTest(10)
    public void updateQuestionById_questionDoesNotExist_shouldThrowException(RepetitionInfo repetitionInfo) {
        Long id = 3L;
        Question updateQuestion = new Question();
        updateQuestion.setNdQuestion("New Question");

        when(questionRepository.findById(id)).thenReturn(Optional.empty());

        try {
            IllegalStateException exception = assertThrows(IllegalStateException.class, () -> questionService.updateQuestionById(id, updateQuestion));

            assertEquals("Question with ID " + id + " does not exist.", exception.getMessage());

            System.out.printf("✅ Test %s (Repetition %d/10) PASSED%n",
                    getTestInfo(), repetitionInfo.getCurrentRepetition());
        } catch (AssertionError e) {
            System.out.printf("❌ Test %s (Repetition %d/10) FAILED: %s%n",
                    getTestInfo(), repetitionInfo.getCurrentRepetition(), e.getMessage());
            throw e;
        }
    }

    private String getTestInfo() {
        return Thread.currentThread().getStackTrace()[2].getMethodName();
    }
}
