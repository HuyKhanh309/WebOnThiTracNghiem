package com.example.WebOnThiTracNghiem.service;

import com.example.WebOnThiTracNghiem.model.AccountExam;
import com.example.WebOnThiTracNghiem.model.Exam;
import com.example.WebOnThiTracNghiem.repository.AccountExamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AccountExamService {

    private final AccountExamRepository accountExamRepository;

    public List<AccountExam> getAccountExamByExam(List<Exam> exam) {
        return accountExamRepository.findByExamIn(exam);
    }
    public List<AccountExam> getAccountExamByOneExam(Exam exam) {
        return accountExamRepository.findByExam(exam);
    }
    public void deleteAccountExamByList(List<AccountExam> accountExams){
        accountExamRepository.deleteAll(accountExams);
    }

}
