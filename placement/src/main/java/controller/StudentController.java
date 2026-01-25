package com.campus.placement.controller;

import com.campus.placement.entity.Application;
import com.campus.placement.entity.Job;
import com.campus.placement.entity.User;
import com.campus.placement.repository.ApplicationRepository;
import com.campus.placement.repository.JobRepository;
import com.campus.placement.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class StudentController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    // 1. Dashboard (Requires Login)
    @GetMapping("/student/dashboard")
    public String showProfile(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/student/login";

        // Refresh user data from DB
        User dbUser = userRepository.findById(user.getId()).orElse(null);
        model.addAttribute("user", dbUser);

        // Load Jobs
        List<Job> jobs = jobRepository.findAll();
        model.addAttribute("jobs", jobs);

        // Load Application History
        List<Application> myApps = applicationRepository.findByStudentId(user.getId());
        model.addAttribute("myApplications", myApps);

        // List of Job IDs for "Applied" button logic
        model.addAttribute("appliedJobIds", myApps.stream().map(app -> app.getJob().getId()).collect(Collectors.toList()));

        return "student_dashboard";
    }

    // 2. Show Application Form
    @GetMapping("/student/apply/{jobId}")
    public String showApplicationForm(@PathVariable Long jobId, HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/student/login";

        // Check if already applied
        if (applicationRepository.existsByStudentIdAndJobId(user.getId(), jobId)) {
            return "redirect:/student/dashboard?error=already_applied";
        }

        Job job = jobRepository.findById(jobId).orElse(null);
        if (job == null) return "redirect:/student/dashboard?error=job_not_found";

        model.addAttribute("job", job);
        model.addAttribute("user", user);
        return "application_form";
    }

    // 3. SUBMIT FORM (Corrected: No File Upload, Uses ResumeLink & CGPA)
    @PostMapping("/student/apply-submit")
    public String submitApplication(@RequestParam Long jobId,
                                    @RequestParam String contactNumber,
                                    @RequestParam String gender,
                                    @RequestParam String branch,
                                    @RequestParam String cgpa, // Matches Entity
                                    @RequestParam String yearOfPassing,
                                    @RequestParam String collegeName,
                                    @RequestParam String resumeLink, // Matches Entity
                                    HttpSession session) {

        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/student/login";

        // Double check duplicate
        if (applicationRepository.existsByStudentIdAndJobId(user.getId(), jobId)) {
            return "redirect:/student/dashboard?error=already_applied";
        }

        Job job = jobRepository.findById(jobId).orElse(null);

        Application app = new Application();
        app.setStudent(user);
        app.setJob(job);
        app.setApplicationDate(LocalDate.now());
        app.setStatus("Applied");

        // --- Save the fields that actually exist in Application.java ---
        app.setFullName(user.getFullName());
        app.setEmail(user.getEmail());
        app.setPositionApplied(job.getJobTitle());

        app.setContactNumber(contactNumber);
        app.setGender(gender);
        app.setBranch(branch);
        app.setCgpa(cgpa);                // <--- Correctly saves CGPA
        app.setYearOfPassing(yearOfPassing);
        app.setCollegeName(collegeName);
        app.setResumeLink(resumeLink);    // <--- Correctly saves Link (Not File)

        applicationRepository.save(app);

        return "redirect:/student/dashboard?applied=true";
    }

    // 4. Update Profile (For Dashboard Sidebar)
    @PostMapping("/student/update")
    public String updateProfile(@RequestParam String department, @RequestParam String cgpa, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            User dbUser = userRepository.findById(user.getId()).orElse(null);
            if (dbUser != null) {
                dbUser.setDepartment(department);
                dbUser.setCgpa(cgpa);
                userRepository.save(dbUser);
            }
        }
        return "redirect:/student/dashboard?success";
    }
}