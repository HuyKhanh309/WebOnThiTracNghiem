package com.example.WebOnThiTracNghiem.service;

import com.example.WebOnThiTracNghiem.model.Account;
import com.example.WebOnThiTracNghiem.repository.AccountRepository;
import com.example.WebOnThiTracNghiem.repository.IAccountRepository;
import com.example.WebOnThiTracNghiem.repository.IAppRoleRepository;
import com.example.WebOnThiTracNghiem.sercurity.Role;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Service
@Slf4j
@Transactional
public class AccountService implements UserDetailsService {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private IAppRoleRepository roleRepository;
    public void save(@NotNull Account account) {
        account.setPassword(new BCryptPasswordEncoder().encode(account.getPassword()));
        accountRepository.save(account);
    }
    public void saveWithoutEncoding(Account account) {
        accountRepository.save(account);
    }
    public void setDefaultRole(String username){
        accountRepository.findByUsername(username).ifPresentOrElse(u -> {
                    u.getRoles().add(roleRepository.findRoleByIdRole(Role.USER.value));
                    accountRepository.save(u);
                },
                () -> { throw new UsernameNotFoundException("User not found"); }
        );
    }
    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        var user = accountRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(user.getAuthorities())
                .accountExpired(!user.isAccountNonExpired())
                .accountLocked(!user.isAccountNonLocked())
                .credentialsExpired(!user.isCredentialsNonExpired())
                .disabled(!user.isEnabled())
                .build();
    }
    public Account findByUsername(String username) throws UsernameNotFoundException {
        return accountRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
    public void deductBalance(String username, Double amount) {
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (account.getBalance() >= amount) {
            account.setBalance(account.getBalance() - amount);
            accountRepository.save(account);
        } else {
            throw new IllegalArgumentException("Insufficient balance");
        }
    }
}
