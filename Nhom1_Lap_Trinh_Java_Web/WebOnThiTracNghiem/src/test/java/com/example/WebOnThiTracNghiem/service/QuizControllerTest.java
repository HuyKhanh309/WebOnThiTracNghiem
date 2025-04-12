package com.example.WebOnThiTracNghiem.service;

import com.example.WebOnThiTracNghiem.controller.QuizController;
import com.example.WebOnThiTracNghiem.model.Account;
import com.example.WebOnThiTracNghiem.model.Exam;
import com.example.WebOnThiTracNghiem.model.ExamQuestion;
import com.example.WebOnThiTracNghiem.model.Question;
import com.example.WebOnThiTracNghiem.repository.AccountExamRepository;
import com.example.WebOnThiTracNghiem.repository.AccountRepository;
import com.example.WebOnThiTracNghiem.repository.ExamRepository;
import com.example.WebOnThiTracNghiem.repository.IAccountRepository;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest
@RequiredArgsConstructor
public class QuizControllerTest {

    @Autowired
    private QuizController quizController;

    @MockBean
    private IAccountRepository accountRepository;

    @MockBean
    private ExamRepository examRepository;

    @MockBean
    private ExamQuestionService examQuestionService;

    private final Long examId = 0L;

    @Test
    void testGetQuizExam_ExamNotFound_ShouldReturnErrorPage() {
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("user");

        when(accountRepository.findByUsername("user")).thenReturn(new Account());
        when(examRepository.findById(examId)).thenReturn(Optional.empty());

        Model model = spy(new ConcurrentModel());

        String result = quizController.getQuizExam(examId, model, principal);

        assertEquals("Quiz/Error", result);
        verify(model).addAttribute(eq("errorMessage"), contains("Invalid exam Id"));
    }

    @Test
    void testGetQuizExam_InsufficientBalance_ShouldReturnErrorPage() {
        Principal principal = mock(Principal.class);
        Account account = new Account();
        account.setBalance(10.0); // QUAN TRỌNG nhất
        when(principal.getName()).thenReturn("admin");
        when(accountRepository.findByUsername("admin")).thenReturn(account);

        Exam exam = new Exam();
        exam.setPrice(30.0);
        when(examRepository.findById(examId)).thenReturn(Optional.of(exam));

        Model model = spy(new ConcurrentModel());

        String result = quizController.getQuizExam(examId, model, principal);

        assertEquals("Quiz/Error", result);
        verify(model).addAttribute(eq("errorMessage"), contains("Không đủ tiền"));
    }

    @Test
    void testGetQuizExam_Success_ShouldReturnExamPage() {
        Principal principal = mock(Principal.class);
        Account account = new Account();
        account.setBalance(100.0);

        when(principal.getName()).thenReturn("admin");
        when(accountRepository.findByUsername("admin")).thenReturn(account);

        Exam exam = new Exam();
        exam.setPrice(30.0);
        when(examRepository.findById(examId)).thenReturn(Optional.of(exam));

        Question question = new Question();
        ExamQuestion examQuestion = new ExamQuestion();
        examQuestion.setQuestion(question);
        List<ExamQuestion> examQuestions = List.of(examQuestion);

        when(examQuestionService.getQuestionsByExamId(examId)).thenReturn(examQuestions);

        Model model = spy(new ConcurrentModel());

        String result = quizController.getQuizExam(examId, model, principal);

        assertEquals("Quiz/ExamNoScore", result);
        verify(accountRepository).save(account); // đã đổi từ accountService sang accountRepository
        verify(model).addAttribute(eq("questions"), any());
        verify(model).addAttribute("examId", examId);
        assertEquals(70.0, account.getBalance()); // kiểm tra đã trừ tiền
    }
}
