package com.example.WebOnThiTracNghiem.controller;

import com.example.WebOnThiTracNghiem.model.*;
import com.example.WebOnThiTracNghiem.repository.AccountExamRepository;
import com.example.WebOnThiTracNghiem.repository.ExamRepository;
import com.example.WebOnThiTracNghiem.repository.IAccountRepository;
import com.example.WebOnThiTracNghiem.service.ExamQuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class QuizController {
    private final ExamQuestionService examQuestionService;

    private final AccountExamRepository accountExamRepository;

    private final ExamRepository examRepository;

    private final IAccountRepository accountRepository;

    List<Question> questions;
    @GetMapping("/exam/quiz_Exam/{id}")
    public String getQuizExam(@PathVariable("id") Long examId, Model model, Principal principal) {

        // Lấy thông tin người dùng đang đăng nhập
        Account account = accountRepository.findByUsername(principal.getName());

        Optional<Exam> exam = examRepository.findById(examId);
        if(exam.isEmpty()) {
            model.addAttribute("errorMessage", String.format("Invalid exam Id: %d", examId));
            return "Quiz/Error"; // Chuyển hướng đến trang báo lỗi nếu đề thi không tồn tại
        }
        
        // Trừ tiền từ tài khoản người dùng
        Double price = exam.get().getPrice();
        if (account.getBalance() < price) {
            model.addAttribute("errorMessage", "Không đủ tiền trong tài khoản để bắt đầu bài kiểm tra.");
            return "Quiz/Error"; // Chuyển hướng đến trang báo lỗi nếu không đủ tiền
        }

        account.setBalance(account.getBalance() - price);
        accountRepository.save(account); // Lưu lại thông tin tài khoản

        // Lấy các câu hỏi của bài kiểm tra
        List<ExamQuestion> examQuestions = examQuestionService.getQuestionsByExamId(examId);
        questions = examQuestions.stream().map(ExamQuestion::getQuestion).collect(Collectors.toList());
        Collections.shuffle(examQuestions);
        model.addAttribute("questions", examQuestions);
        model.addAttribute("examId", examId);
        return "Quiz/ExamNoScore";
    }

    @PostMapping("/exam/tryquiz_Exam/{id}")
    public String submittryExam(@PathVariable("id") Long id, @RequestParam Map<String, String> allParams, Model model, Principal principal) {
        if (questions == null || questions.isEmpty()) {
            throw new IllegalStateException("Questions not initialized properly");
        }
        AccountExam accountExam = new AccountExam();

        Map<Long, String> correctAnswers = questions.stream()
                .collect(Collectors.toMap(Question::getIdQuestion, Question::getAnswer));

        int correctCount = 0;
        for (Map.Entry<String, String> entry : allParams.entrySet()) {
            if (entry.getKey().startsWith("question")) {
                Long questionId = Long.parseLong(entry.getKey().replace("question", ""));
                String userAnswer = entry.getValue().toString() ;

                if (correctAnswers.containsKey(questionId)) {
                    String correctAnswer = correctAnswers.get(questionId);
                    if (correctAnswer.equals(userAnswer)) {
                        correctCount++;
                    }
                }
            }
        }

        Exam exam = examRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid exam Id:" + id));
        accountExam.setExam(exam);

        Account account = accountRepository.findByUsername(principal.getName());
        accountExam.setAccount(account);

        float result= Math.round(((float)correctCount/exam.getQuantity())*10 * 2) / 2.0f;;
        accountExam.setResult(result);
        // Lưu AccountExam object vào database
        accountExamRepository.save(accountExam);

        // Trả về kết quả hoặc trang thông báo
        model.addAttribute("message", "Đã nộp bài thành công! Số câu trả lời đúng: " + correctCount+"/"+exam.getQuantity());
        model.addAttribute("point", "Điểm số của bạn: " + result);
        return "/Quiz/Result";
    }
    //
    @PostMapping("/exam/quiz_Exam/{id}")
    public String submitExam(@PathVariable("id") Long id, @RequestParam Map<String, String> allParams, Model model, Principal principal) {
        if (questions == null || questions.isEmpty()) {
            throw new IllegalStateException("Questions not initialized properly");
        }
        AccountExam accountExam = new AccountExam();

        Map<Long, String> correctAnswers = questions.stream()
                .collect(Collectors.toMap(Question::getIdQuestion, Question::getAnswer));

        int correctCount = 0;
        Map<Long, String> userAnswers = new HashMap<>();
        for (Map.Entry<String, String> entry : allParams.entrySet()) {
            if (entry.getKey().startsWith("question")) {
                Long questionId = Long.parseLong(entry.getKey().replace("question", ""));
                String userAnswer = entry.getValue();

                userAnswers.put(questionId, userAnswer);

                if (correctAnswers.containsKey(questionId)) {
                    String correctAnswer = correctAnswers.get(questionId);
                    if (correctAnswer.equals(userAnswer)) {
                        correctCount++;
                    }
                }
            }
        }

        Exam exam = examRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid exam Id:" + id));
        accountExam.setExam(exam);

        Account account = accountRepository.findByUsername(principal.getName());
        accountExam.setAccount(account);

        float result = Math.round(((float) correctCount / exam.getQuantity()) * 10 * 2) / 2.0f;
        accountExam.setResult(result);

        // Lưu AccountExam object vào database
        accountExamRepository.save(accountExam);

        // Get ExamQuestion objects to pass to the view
        List<ExamQuestion> examQuestions = examQuestionService.getQuestionsByExamId(id);

        // Add data to model
        model.addAttribute("message", "Đã nộp bài thành công! Số câu trả lời đúng: " + correctCount);
        model.addAttribute("correctAnswers", correctAnswers);
        model.addAttribute("userAnswers", userAnswers);
        model.addAttribute("point", "Điểm số của bạn: " + result);
        model.addAttribute("examQuestions", examQuestions);

        return "Quiz/ResultNoScore"; // Ensure the path is correct
    }






    @GetMapping("/exam/try_Exam/{id}")
    public String tryQuizExam(@PathVariable("id") Long examId, Model model, Principal principal) {
        // Lấy thông tin người dùng đang đăng nhập
        Account account = accountRepository.findByUsername(principal.getName());

        // Tìm bài kiểm tra
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid exam Id:" + examId));

        // Trừ tiền từ tài khoản người dùng
        Double price = exam.getPrice();
        if (account.getBalance() < price) {
            model.addAttribute("errorMessage", "Không đủ tiền trong tài khoản để bắt đầu bài kiểm tra.");
            return "Quiz/Error"; // Chuyển hướng đến trang báo lỗi nếu không đủ tiền
        }

        account.setBalance(account.getBalance() - price);
        accountRepository.save(account); // Lưu lại thông tin tài khoản

        // Lấy các câu hỏi ngẫu nhiên từ bài kiểm tra
        List<ExamQuestion> examQuestions = examQuestionService.getRandomQuestionsByExamId(examId, exam.getQuantity());
        questions = examQuestions.stream().map(ExamQuestion::getQuestion).collect(Collectors.toList());
        Collections.shuffle(examQuestions);

        model.addAttribute("questions", examQuestions);
        model.addAttribute("examId", examId);
        return "Quiz/Exam";
    }

    //
    @GetMapping("/exam/Exam/{id}")
    public String QuizExam(@PathVariable("id") Long examId, Model model, Principal principal) {
        // Lấy thông tin người dùng đang đăng nhập
        Account account = accountRepository.findByUsername(principal.getName());

        // Tìm bài kiểm tra
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid exam Id:" + examId));

        // Trừ tiền từ tài khoản người dùng
        Double price = exam.getPrice();
        if (account.getBalance() < price) {
            model.addAttribute("errorMessage", "Không đủ tiền trong tài khoản để bắt đầu bài kiểm tra.");
            return "Quiz/Error"; // Chuyển hướng đến trang báo lỗi nếu không đủ tiền
        }

        account.setBalance(account.getBalance() - price);
        accountRepository.save(account); // Lưu lại thông tin tài khoản

        // Lấy các câu hỏi ngẫu nhiên từ bài kiểm tra
        List<ExamQuestion> examQuestions = examQuestionService.getRandomQuestionsByExamId(examId, exam.getQuantity());
        questions = examQuestions.stream().map(ExamQuestion::getQuestion).collect(Collectors.toList());
        Collections.shuffle(examQuestions);
        model.addAttribute("questions", examQuestions);
        model.addAttribute("examId", examId);
        return "Quiz/ExamNoScore";
    }
}
