package com.example.WebOnThiTracNghiem.service;

import com.example.WebOnThiTracNghiem.controller.UserController;
import com.example.WebOnThiTracNghiem.model.Account;
import com.example.WebOnThiTracNghiem.service.AccountService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Mock
    private AccountService userService;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private Model model;

    @InjectMocks
    private UserController userController;

    @Test
    public void register_withErrors_shouldReturnRegisterView() {
        // Arrange
        Account user = new Account();
        user.setUsername(""); // Empty to simulate error

        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getAllErrors()).thenReturn(List.of(
                new ObjectError("user", "Username is required")));

        // Act
        String viewName = userController.register(user, bindingResult, model);

        // Debug output
        System.out.println("▶️ Test: register_withErrors_shouldReturnRegisterView");
        System.out.println("   Username: '" + user.getUsername() + "'");
        System.out.println("   Has errors: " + bindingResult.hasErrors());
        System.out.println("   Returned view: " + viewName);

        // Assert
        assertEquals("/Users/Register", viewName);
        verify(model).addAttribute(eq("errors"), any(String[].class));
        System.out.println("✅ Passed\n");
    }

    @Test
    public void register_withoutErrors_shouldRedirectToLogin() {
        // Arrange
        Account user = new Account();
        user.setUsername("test");

        when(bindingResult.hasErrors()).thenReturn(false);

        // Act
        String viewName = userController.register(user, bindingResult, model);

        // Debug output
        System.out.println("▶️ Test: register_withoutErrors_shouldRedirectToLogin");
        System.out.println("   Username: '" + user.getUsername() + "'");
        System.out.println("   Has errors: " + bindingResult.hasErrors());
        System.out.println("   Returned view: " + viewName);

        // Assert
        assertEquals("redirect:/login", viewName);
        verify(userService).save(user);
        verify(userService).setDefaultRole(user.getUsername());
        System.out.println("✅ Passed\n");
    }
}
