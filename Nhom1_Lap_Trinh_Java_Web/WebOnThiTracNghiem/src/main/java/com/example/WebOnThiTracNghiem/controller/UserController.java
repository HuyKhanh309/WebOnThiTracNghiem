package com.example.WebOnThiTracNghiem.controller;

import com.example.WebOnThiTracNghiem.model.Account;

import com.example.WebOnThiTracNghiem.service.AccountService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


@Controller
@RequiredArgsConstructor
public class UserController {
    private final AccountService userService;
    @GetMapping("/login")
    public String login() {
        return "/Users/Login";
    }
    @GetMapping("/register")
    public String register(@NotNull Model model) {
        model.addAttribute("user", new Account());
        return "/Users/Register";
    }
    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("user") Account user,
                           @NotNull BindingResult bindingResult,
                           Model model) {
        //Test 1
        if(user.getUsername().isBlank()){
            var errors = "Tài khoản không hợp lệ";
            model.addAttribute("errors", errors);
            return "/Users/Register";
        }

        if (bindingResult.hasErrors()) {
            var errors = bindingResult.getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toArray(String[]::new);
            model.addAttribute("errors", errors);
            return "/Users/Register";
        }
        user.setBalance((double) 0);
        user.setIsActive(true);
        userService.save(user);
        userService.setDefaultRole(user.getUsername());
        return "redirect:/login";
    }

}
