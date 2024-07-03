package com.example.WebOnThiTracNghiem.repository;
import com.example.WebOnThiTracNghiem.model.Statistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface StatisticsRepository extends JpaRepository<Statistics, Long> {
}
