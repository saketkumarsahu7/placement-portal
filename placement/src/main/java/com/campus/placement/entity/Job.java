package com.campus.placement.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "jobs")
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String jobTitle;       // e.g. "Software Engineer"
    private String jobDescription; // e.g. "Knowledge of Java, Spring..."
    private String location;       // e.g. "Bangalore"
    private String packageLPA;     // e.g. "12 LPA"
    private Double requiredCgpa;   // e.g. 7.5
    private LocalDate deadline;    // e.g. 2026-05-20

    // Connect Job to the Company who posted it
    @ManyToOne
    @JoinColumn(name = "company_id")
    private User company;

    // Standard Getters and Setters (Generate using Alt+Insert)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }

    public String getJobDescription() { return jobDescription; }
    public void setJobDescription(String jobDescription) { this.jobDescription = jobDescription; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getPackageLPA() { return packageLPA; }
    public void setPackageLPA(String packageLPA) { this.packageLPA = packageLPA; }

    public Double getRequiredCgpa() { return requiredCgpa; }
    public void setRequiredCgpa(Double requiredCgpa) { this.requiredCgpa = requiredCgpa; }

    public LocalDate getDeadline() { return deadline; }
    public void setDeadline(LocalDate deadline) { this.deadline = deadline; }

    public User getCompany() { return company; }
    public void setCompany(User company) { this.company = company; }
}