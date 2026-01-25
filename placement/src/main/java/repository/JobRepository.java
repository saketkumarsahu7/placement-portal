package com.campus.placement.repository;

import com.campus.placement.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface JobRepository extends JpaRepository<Job, Long> {
    // Fetch all jobs posted by a specific company
    List<Job> findByCompanyId(Long companyId);
}