package com.example.WebOnThiTracNghiem.controller;

import com.example.WebOnThiTracNghiem.model.*;
import com.example.WebOnThiTracNghiem.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class SubjectController {
    @Autowired
    private final SubjectService subjectService;
    @Autowired
    private final AccountExamService accountExamService;
    @Autowired
    private final ExamService examService;
    @Autowired
    private final ExamQuestionService examQuestionService;
    @Autowired
    private final QuestionService questionService;

    @GetMapping("/admin/subjects/add")
    public String showAddForm(Model model) {
        model.addAttribute("subject", new Subject());
        return "Subjects/Add-Subject";
    }

    @PostMapping("/admin/subjects/add")
    public String addSubject(@Valid Subject subject, BindingResult result) {
        if (result.hasErrors()) {
            return "Subjects/Add-Subject";
        }
        subjectService.addSubject(subject);
        return "redirect:/admin/subjects"; // Điều hướng về trang danh sách sau khi thêm
    }

    @GetMapping("/admin/subjects")
    public String listSubjects(Model model) {
        List<Subject> subjects = subjectService.getAllSubjects();
        model.addAttribute("subjects", subjects);
        return "Subjects/Subjects-List";
    }
    /*   @GetMapping("/admin/subjects/questions/{id}")
       public String getQuestionsBySubjectId(@PathVariable("id") Long id, Model model) {
           Optional<Subject> subject = subjectService.getSubjectById(id);
           if (subject.isPresent()) {
               model.addAttribute("subject", subject.get());
               model.addAttribute("questions", subject.get().getQuestions());
           } else {
               // Handle the case where the subject is not found, e.g., redirect to an error page
           }
           return "Questions/Question-List";
       }*/
    @GetMapping("/admin/subjects/delete/{id}")
    public String deleteSubject(@PathVariable("id") Long id, Model model) {
        Subject subject = subjectService.getSubjectById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid category Id:" + id));
        /*examService.deleteExamBySubject(subject);*/
        List<Exam> exams = examService.getExamsBySubjectId(subject);
        List<ExamQuestion> examQuestions = examQuestionService.getExamQuestionByExamList(exams);
        List<Long> idQuestions = examQuestions.stream()
                .map(ExamQuestion::getQuestion)
                .map(Question::getIdQuestion)
                .collect(Collectors.toList());

        // Now you have a list of idQuestion, you can fetch corresponding Question objects
        List<Question> questions = questionService.getQuestionByListId(idQuestions);

        List<AccountExam> accountExams = accountExamService.getAccountExamByExam(exams);
        if (!accountExams.isEmpty()) {
            accountExamService.deleteAccountExamByList(accountExams);
        }
        if (!examQuestions.isEmpty()) {
            examQuestionService.deleteExamQuestionByExamQuestionList(examQuestions);
        }
        if (!questions.isEmpty()) {
            questionService.deleteQuestionByQuestion(questions);
        }
        if (!exams.isEmpty()) {
            examService.deleteExamBySubject(subject);
        }

        subjectService.deleteSubjectById(id);
        model.addAttribute("subjects", subjectService.getAllSubjects());
        return "redirect:/admin/subjects";
    }
    @GetMapping("/admin/subjects/edit/{id}")
    public String showUpdateForm(@PathVariable("id") Long id, Model model) {
        Subject subject = subjectService.getSubjectById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid subject Id:" + id));
        model.addAttribute("subject", subject);
        return "/Subjects/Update-Subject";
    }
    @PostMapping("/admin/subjects/edit/{id}")
    public String updateSubject(@PathVariable("id") Long id, @Valid Subject subject, BindingResult result, Model model) {
        if (result.hasErrors()) {
            subject.setIdSubject(id);
            return "/Subjects/Update-Subject";
        }
        subjectService.updateSubjectById(id,subject);
        model.addAttribute("subjects", subjectService.getAllSubjects());
        return "redirect:/admin/subjects";
    }

    @GetMapping("/admin/subjects/exams/{id}")
    public String listExamsBySubject(@PathVariable("id") Long idSubject, Model model) {
        List<Exam> exams = examService.getExamsByIdSubject(idSubject);
        model.addAttribute("exams", exams);
        return "Exams/Exams-List-By-Subject";
    }
    /*@GetMapping("/examList/{id}")
    public String viewExam(@PathVariable Long id, Model model) {
        Optional<Exam> exam = examService.getExamById(id);
        model.addAttribute("exam", exam);
        return "Exams/View-Exam";
    }*/




    @GetMapping("/admin/subjects/exams/add/{id}")
    public String showAddExamForm(@PathVariable("id") Long idSubject, Model model) {
        Subject subject = subjectService.getSubjectById(idSubject)
                .orElseThrow(() -> new IllegalArgumentException("Invalid exam Id:" + idSubject));

        model.addAttribute("exam", new Exam());
        model.addAttribute("subject", subject);
        return "Exams/Add-Exam-By-Subject";
    }
    @PostMapping("/admin/subjects/exams/add/{id}")
    public String addExam(@jakarta.validation.Valid Exam exam, @PathVariable("id") Long idSubject, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "/admin/subjects/exams/add/{id}";
        }



        /*model.addAttribute("message", "Sinh viên đã được thêm thành công!");
        model.addAttribute("product", product);*/
        Subject subjectAdd = subjectService.getSubjectByIdSubNum(idSubject);
        exam.setSubject(subjectAdd);
        examService.addExam(exam);
        return "redirect:/admin/subjects/exams/add/{id}";
    }
}
