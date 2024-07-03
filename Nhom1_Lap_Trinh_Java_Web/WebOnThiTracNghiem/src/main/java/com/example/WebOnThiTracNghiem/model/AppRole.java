package com.example.WebOnThiTracNghiem.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;
import org.springframework.security.core.GrantedAuthority;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Setter
@Getter
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "AppRole")

public class AppRole implements GrantedAuthority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRole;

    private String roleName;
    @ManyToMany(mappedBy = "roles", cascade = CascadeType.ALL)
    @ToString.Exclude
    private Set<Account> accounts = new HashSet<>();
    @Override
    public String getAuthority() {
        return roleName;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return
                false;
        AppRole role = (AppRole) o;
        return getIdRole() != null && Objects.equals(getIdRole(), role.getIdRole());
    }
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
