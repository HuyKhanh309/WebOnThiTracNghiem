package com.example.WebOnThiTracNghiem.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.Hibernate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Setter
@Getter
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "Account")
public class Account  implements UserDetails{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idAccount;

    @Column(name = "username", length = 50)
    @NotBlank(message = "Username is required")
    private String username;
    @Column(name = "password", length = 250)
    @NotBlank(message = "Password is required")
    private String password;
    private Boolean isActive;
    private Double balance;

    @ManyToMany(fetch=FetchType.EAGER)
    @JoinTable(name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<AppRole> roles = new HashSet<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<AppRole> userRoles = this.getRoles();
        return userRoles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getRoleName()))
                .toList();
    }

    @Override
    public String getPassword() {
        return password;
    }
    @Override
    public String getUsername() {
        return username;
    }
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    @Override
    public boolean isEnabled() {
        return true;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return
                false;
        Account account = (Account) o;
        return getIdAccount() != null && Objects.equals(getIdAccount(), account.getIdAccount());
    }
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
