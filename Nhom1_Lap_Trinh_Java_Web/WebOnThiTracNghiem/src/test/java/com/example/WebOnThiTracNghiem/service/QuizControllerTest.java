package com.example.WebOnThiTracNghiem.service;
import com.example.WebOnThiTracNghiem.controller.QuizController;
import com.example.WebOnThiTracNghiem.model.Account;
import com.example.WebOnThiTracNghiem.model.Exam;
import com.example.WebOnThiTracNghiem.model.ExamQuestion;
import com.example.WebOnThiTracNghiem.model.Question;
import com.example.WebOnThiTracNghiem.repository.AccountRepository;
import com.example.WebOnThiTracNghiem.repository.ExamRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class QuizControllerTest {

    @InjectMocks
    private QuizController quizController;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private ExamRepository examRepository;

    @Mock
    private ExamQuestionService examQuestionService;

    @Mock
    private Model model;

    @Mock
    private Principal principal;

    private final Long examId = 0L;

    @Test
    void testGetQuizExam_ExamNotFound_ShouldReturnErrorPage() {
        when(principal.getName()).thenReturn("user");
        when(accountRepository.findByUsername("user")).thenReturn(Optional.of(new Account()));
        when(examRepository.findById(examId)).thenReturn(Optional.empty());

        String result = quizController.getQuizExam(examId, model, principal);

        assertEquals("Quiz/Error", result);
        verify(model).addAttribute(eq("errorMessage"), contains("Invalid exam Id"));
    }

    @Test
    void testGetQuizExam_InsufficientBalance_ShouldReturnErrorPage() {
        Account account = new Account();
        account.setBalance(10.0);
        when(principal.getName()).thenReturn("user");
        when(accountRepository.findByUsername("user")).thenReturn(Optional.of(account));

        Exam exam = new Exam();
        exam.setPrice(50.0);
        when(examRepository.findById(examId)).thenReturn(Optional.of(exam));

        String result = quizController.getQuizExam(examId, model, principal);

        assertEquals("Quiz/Error", result);
        verify(model).addAttribute(eq("errorMessage"), contains("Không đủ tiền"));
    }

    @Test
    void testGetQuizExam_Success_ShouldReturnExamPage() {
        Account account = new Account();
        account.setBalance(100.0);
        when(principal.getName()).thenReturn("user");
        when(accountRepository.findByUsername("user")).thenReturn(Optional.of(account));

        Exam exam = new Exam();
        exam.setPrice(30.0);
        when(examRepository.findById(examId)).thenReturn(Optional.of(exam));

        Question question = new Question();
        ExamQuestion examQuestion = new ExamQuestion();
        examQuestion.setQuestion(question);
        List<ExamQuestion> examQuestions = List.of(examQuestion);

        when(examQuestionService.getQuestionsByExamId(examId)).thenReturn(examQuestions);

        String result = quizController.getQuizExam(examId, model, principal);

        assertEquals("Quiz/ExamNoScore", result);
        verify(accountRepository).save(account);
        verify(model).addAttribute(eq("questions"), any());
        verify(model).addAttribute("examId", examId);
        assertEquals(70.0, account.getBalance()); // kiểm tra đã trừ tiền
    }
}
