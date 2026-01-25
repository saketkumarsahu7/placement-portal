package com.campus.placement.repository;

import com.campus.placement.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

    List<Application> findByJobId(Long jobId);

    List<Application> findByStudentId(Long studentId);

    // NEW: Check if a specific application already exists
    boolean existsByStudentIdAndJobId(Long studentId, Long jobId);
}