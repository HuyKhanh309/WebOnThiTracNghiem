package com.example.WebOnThiTracNghiem.repository;
import com.example.WebOnThiTracNghiem.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface IAccountRepository extends JpaRepository<Account, Long>  {
    Account findByUsername(String username);
}
