package com.example.WebOnThiTracNghiem.controller;

import com.example.WebOnThiTracNghiem.model.*;
import com.example.WebOnThiTracNghiem.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class ExamController {
    @Autowired
    private final ExamService examService;
    @Autowired
    private final AccountExamService accountExamService;
    @Autowired
    private final QuestionService questionService;
    @Autowired
    private final SubjectService subjectService;
    @Autowired
    private final ExamQuestionService examQuestionService;
    @Autowired
    private final AccountService accountService;
    @GetMapping("/admin/exams/add")
    public String showAddForm(Model model) {
        model.addAttribute("exam", new Exam());
        model.addAttribute("subjects", subjectService.getAllSubjects());
        return "Exams/Add-Exam";
    }
    @PostMapping("/admin/exams/add")
    public String addExam(@Valid @ModelAttribute Exam exam, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "redirect:/admin/exams/add";
        }
        examService.addExam(exam);
        return "redirect:/admin/exams";
    }

    @GetMapping("/admin/exams")
    public String listExams(Model model) {
        List<Exam> exams = examService.getAllExams();
        model.addAttribute("exams", exams);
        return "Exams/Exams-List";
    }

    @GetMapping("/admin/exams/delete/{id}")
    public String deleteSubject(@PathVariable("id") Long id, Model model) {
        Exam exam = examService.getExamById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid category Id:" + id));

        List<ExamQuestion> examQuestionsList = examQuestionService.getExamQuestionByExam(exam);

        /*List<Long> idQuestions = examQuestionsList.stream()
                .map(ExamQuestion::getQuestion)
                .map(Question::getIdQuestion)
                .collect(Collectors.toList());
*/
        List<Long> idQuestions = examQuestionsList.stream()
                .filter(examQuestion -> examQuestion != null && examQuestion.getQuestion() != null)
                .map(examQuestion -> Optional.ofNullable(examQuestion.getQuestion()).map(Question::getIdQuestion))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());

        // Now you have a list of idQuestion, you can fetch corresponding Question objects
        List<Question> questions = questionService.getQuestionByListId(idQuestions);

        List<AccountExam> accountExams = accountExamService.getAccountExamByOneExam(exam);
        if (!accountExams.isEmpty()) {
            accountExamService.deleteAccountExamByList(accountExams);
        }

        if (!examQuestionsList.isEmpty()) {
            examQuestionService.deleteExamQuestionByExam(exam);
        }

        if (!questions.isEmpty()) {
            questionService.deleteQuestionByQuestion(questions);
        }

        examService.deleteExamById(id);
        model.addAttribute("exams", examService.getAllExams());
        return "redirect:/admin/exams";
    }

//Hien thi cau hoi theo exam
    @GetMapping("/admin/exams/questions/{id}")
    public String listQuestionsForExam(@PathVariable("id") Long examId, Model model) {
        List<ExamQuestion> examQuestions = examQuestionService.getExamQuestionsByExamId(examId);

        List<Question> questions = examQuestions.stream()
                .map(ExamQuestion::getQuestion)
                .collect(Collectors.toList());


        model.addAttribute("questions", questions);
        return "Questions/Show-Question-By-Exam";
    }

    @GetMapping("/admin/exams/questions/add/{id}")
    public String showAddQuestionFormForExam(@PathVariable("id") Long examId, Model model) {
        // Load the exam based on examId
        Exam exam = examService.getExamById(examId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid exam Id:" + examId));

        // Prepare a new Question object
        Question question = new Question();

        // Add necessary attributes to the model
        model.addAttribute("exam", exam);
        model.addAttribute("question", question);

        // Return the view name for adding a question to an exam
        return "Questions/Add-Question-By-Exam";
    }
    @PostMapping("/admin/exams/questions/add/{id}")
    public String addQuestion2(@Valid Question question, @PathVariable Long id, BindingResult result, Model model) {
        if (result.hasErrors()) {
            // Handle validation errors if needed
            return "Questions/Add-Question-By-Exam"; // Return to the form with errors
        }
        questionService.saveQuestion(question);
        // Retrieve the exam based on examId
        Exam examAdd = examService.getExamById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Id:" + id));

        // Create a new ExamQuestion object
        ExamQuestion examQuestion = new ExamQuestion();
        examQuestion.setExam(examAdd);
        examQuestion.setQuestion(question);

        /*examAdd.setQuantity(examAdd.getQuantity() + 1);
        examService.updateExamById(id, examAdd);*/
        // Save the ExamQuestion
        examQuestionService.addQuestionExam(examQuestion);

        // Redirect to the appropriate URL after adding the question
        return "redirect:/admin/exams/questions/add/{id}"; // Redirect to the questions list page
    }



    @GetMapping("/admin/exams/edit/{id}")
    public String showUpdateForm(@PathVariable("id") Long id, Model model) {
        Exam exam = examService.getExamById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid exam Id:" + id));
        model.addAttribute("exam", exam);
        model.addAttribute("subjects", subjectService.getAllSubjects());
        return "/Exams/Update-Exam";
    }
    @PostMapping("/admin/exams/edit/{id}")
    public String updateSubject(@PathVariable("id") Long id, @javax.validation.Valid Exam exam, BindingResult result, Model model) {
        if (result.hasErrors()) {
            exam.setIdExam(id);
            return "/Exams/Update-Exam";
        }
        examService.updateExamById(id,exam);
        model.addAttribute("exams", examService.getAllExams());
        return "redirect:/admin/exams";
    }

    @GetMapping("/exam/{id}")
    public String showTestForm(@PathVariable Long id,Model model) {
        Exam exam=examService.loadExamById(id);
        model.addAttribute("exam",exam);
        return "Exams/Test";
    }
    @GetMapping("/quiz/{id}")
    public String startExam(@PathVariable("id") Long examId, Model model) {
        Exam exam = examService.findById(examId);
        model.addAttribute("exam", exam);
        return "Quiz/Exam";
    }



    @GetMapping("/quiz/{id}/start")
    public String startExamProcess(@PathVariable("id") Long examId, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        Exam exam = examService.findById(examId);
        Double price = exam.getPrice();

        try {
            // Trừ tiền từ tài khoản người dùng
            accountService.deductBalance(username, price);
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", "Không đủ tiền trong tài khoản để bắt đầu bài kiểm tra.");
            return "error"; // Chuyển hướng đến trang báo lỗi nếu không đủ tiền
        }

        // Chuyển hướng sang trang làm bài kiểm tra
        return "/Quiz/Exam" + examId;
    }


}
