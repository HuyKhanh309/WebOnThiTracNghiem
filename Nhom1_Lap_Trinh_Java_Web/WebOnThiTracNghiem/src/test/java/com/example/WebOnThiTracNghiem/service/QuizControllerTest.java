package com.example.WebOnThiTracNghiem.service;

import com.example.WebOnThiTracNghiem.controller.QuizController;
import com.example.WebOnThiTracNghiem.model.Account;
import com.example.WebOnThiTracNghiem.model.Exam;
import com.example.WebOnThiTracNghiem.model.ExamQuestion;
import com.example.WebOnThiTracNghiem.model.Question;
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

    private final Long examId = 1L;

    @Test
    void testGetQuizExam_ExamNotFound_ShouldReturnErrorPage() {
        System.out.println(">>> TEST: Exam không tồn tại");

        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("admin");

        Account mockAccount = new Account();
        when(accountRepository.findByUsername("admin")).thenReturn(mockAccount);
        when(examRepository.findById(examId)).thenReturn(Optional.empty());

        Model model = spy(new ConcurrentModel());

        String result = quizController.getQuizExam(examId, model, principal);
        System.out.println(">>> Kết quả trả về: " + result);

        assertEquals("Quiz/Error", result);
        verify(model).addAttribute(eq("errorMessage"), contains("Invalid exam Id"));
    }

    @Test
    void testGetQuizExam_InsufficientBalance_ShouldReturnErrorPage() {
        System.out.println(">>> TEST: Không đủ tiền");

        Principal principal = mock(Principal.class);
        Account account = new Account();
        account.setBalance(10.0);
        System.out.println(">>> Account balance ban đầu: " + account.getBalance());

        when(principal.getName()).thenReturn("admin");
        when(accountRepository.findByUsername("admin")).thenReturn(account);

        Exam exam = new Exam();
        exam.setPrice(30.0);
        System.out.println(">>> Giá bài thi: " + exam.getPrice());

        when(examRepository.findById(examId)).thenReturn(Optional.of(exam));

        Model model = spy(new ConcurrentModel());

        String result = quizController.getQuizExam(examId, model, principal);
        System.out.println(">>> Kết quả trả về: " + result);

        assertEquals("Quiz/Error", result);
        verify(model).addAttribute(eq("errorMessage"), contains("Không đủ tiền"));
    }

    @Test
    void testGetQuizExam_Success_ShouldReturnExamPage() {
        System.out.println(">>> TEST: Đủ tiền, thi thành công");

        Principal principal = mock(Principal.class);
        Account account = new Account();
        account.setBalance(100.0);
        System.out.println(">>> Account balance ban đầu: " + account.getBalance());

        when(principal.getName()).thenReturn("admin");
        when(accountRepository.findByUsername("admin")).thenReturn(account);

        Exam exam = new Exam();
        exam.setPrice(30.0);
        System.out.println(">>> Giá bài thi: " + exam.getPrice());

        when(examRepository.findById(examId)).thenReturn(Optional.of(exam));

        Question question = new Question();
        ExamQuestion examQuestion = new ExamQuestion();
        examQuestion.setQuestion(question);
        List<ExamQuestion> examQuestions = List.of(examQuestion);

        when(examQuestionService.getQuestionsByExamId(examId)).thenReturn(examQuestions);

        Model model = spy(new ConcurrentModel());

        String result = quizController.getQuizExam(examId, model, principal);
        System.out.println(">>> Kết quả trả về: " + result);
        System.out.println(">>> Account balance sau khi trừ: " + account.getBalance());

        assertEquals("Quiz/ExamNoScore", result);
        verify(accountRepository).save(account);
        verify(model).addAttribute(eq("questions"), any());
        verify(model).addAttribute("examId", examId);
        assertEquals(70.0, account.getBalance());
    }

}
