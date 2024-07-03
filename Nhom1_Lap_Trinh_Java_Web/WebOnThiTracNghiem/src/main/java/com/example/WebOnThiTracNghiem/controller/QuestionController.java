package com.example.WebOnThiTracNghiem.controller;

import com.example.WebOnThiTracNghiem.model.Exam;
import com.example.WebOnThiTracNghiem.model.ExamQuestion;
import com.example.WebOnThiTracNghiem.model.Question;
import com.example.WebOnThiTracNghiem.service.ExamQuestionService;
import com.example.WebOnThiTracNghiem.service.ExamService;
import com.example.WebOnThiTracNghiem.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    @Autowired
    private final ExamService examService;
    @Autowired
    private final ExamQuestionService examQuestionService;
    @GetMapping("")
    public String showQuestionList(Model model) {
        List<Question> questions = questionService.getAllQuestions();

        // Fetch the exam notes using ExamQuestionService
        List<String> examNotes = questions.stream()
                .map(question -> examQuestionService.getExamNotesByQuestionId(question.getIdQuestion()))
                .collect(Collectors.toList());

        model.addAttribute("questions", questions);
        model.addAttribute("examNotes", examNotes); // Add examNotes to the model

        return "Questions/Questions-List";
    }

    @GetMapping("/add")
    public String showAddQuestionForm(Model model) {
        List<Exam> exams = examService.getAllExams();
        model.addAttribute("exams", exams);
        model.addAttribute("question", new Question());
        model.addAttribute("examQuestion", new ExamQuestion());
        return "Questions/Add-Question";
    }

    @PostMapping("/add")
    public String addQuestion(@Valid Question question, @RequestParam("examQuestion") Long examQuestion, BindingResult result, Model model) {
        if (result.hasErrors()) {
            // Handle validation errors if needed
            return "Questions/Add-Question";
        }
        questionService.saveQuestion(question);
        ExamQuestion add = new ExamQuestion();

        Exam examAdd = examService.getExamById(examQuestion)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Id:" + examQuestion));


        add.setExam(examAdd);
        add.setQuestion(question);

        /*examAdd.setQuantity(examAdd.getQuantity() + 1);
        examService.updateExamById(examQuestion, examAdd);*/
        examQuestionService.addQuestionExam(add);
        return "redirect:/admin/questions";
    }

    @GetMapping("/edit/{id}")
    public String showUpdateQuestionForm(@PathVariable Long id, Model model) {
        Question question = questionService.getQuestionById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid question Id:" + id));
        List<Exam> exams = examService.getAllExams();
        model.addAttribute("exams", exams);
        model.addAttribute("question", question);
        model.addAttribute("examQuestion", new ExamQuestion());
        return "Questions/Update-Question";
    }

    @PostMapping("/edit/{id}")
    public String updateQuestion(@PathVariable("id") Long id, @javax.validation.Valid Question question, BindingResult result, Model model) {
        if (result.hasErrors()) {
            question.setIdQuestion(id);
            return "/Questions/Update-Question";
        }
        questionService.updateQuestionById(id,question);
        model.addAttribute("exams", questionService.getAllQuestions());
        return "redirect:/admin/questions";
    }



    @GetMapping("/delete/{id}")
    public String deleteQuestion(@PathVariable Long id) {
       Optional<Question> question = questionService.getQuestionById(id);
       examQuestionService.deleteExamQuestionByQuestion(question);
        questionService.deleteQuestionById(id);

        return "redirect:/admin/questions";
    }
}
