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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class CompanyController {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    // 1. COMPANY DASHBOARD
    @GetMapping("/company/dashboard")
    public String dashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");

        // Security Check: Must be logged in and be a COMPANY
        if (user == null || !"COMPANY".equals(user.getRole())) {
            return "redirect:/company/login";
        }

        List<Job> myJobs = jobRepository.findByCompanyId(user.getId());
        model.addAttribute("jobs", myJobs);
        model.addAttribute("user", user);
        return "company_dashboard";
    }

    // 2. POST NEW JOB (Form)
    @GetMapping("/company/post-job")
    public String showPostJobForm(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/company/login";

        model.addAttribute("job", new Job());
        return "post_job";
    }

    // 3. SAVE NEW JOB (Action)
    @PostMapping("/company/post-job")
    public String saveJob(@ModelAttribute Job job, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/company/login";

        job.setCompany(user); // Link job to the logged-in company
        jobRepository.save(job);

        return "redirect:/company/dashboard?success";
    }

    // 4. VIEW APPLICANTS (Fixes the Whitelabel Error)
    @GetMapping("/company/job/{jobId}/applicants")
    public String viewApplicants(@PathVariable Long jobId, Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");

        // A. Security Check
        if (user == null || !"COMPANY".equals(user.getRole())) {
            return "redirect:/company/login";
        }

        // B. Find Job & Verify Ownership
        Job job = jobRepository.findById(jobId).orElse(null);
        if (job == null || !job.getCompany().getId().equals(user.getId())) {
            // If job doesn't exist OR doesn't belong to this company
            return "redirect:/company/dashboard?error=Unauthorized";
        }

        // C. Fetch Applications
        List<Application> applications = applicationRepository.findByJobId(jobId);

        // D. Send Data to HTML
        model.addAttribute("job", job);
        model.addAttribute("applications", applications);

        return "view_applicants"; // Must match template name: view_applicants.html
    }

    // 5. UPDATE STATUS (Without Email Service)
    @PostMapping("/company/application/update-status")
    public String updateStatus(@RequestParam Long appId,
                               @RequestParam String newStatus,
                               HttpSession session) {

        User user = (User) session.getAttribute("user");
        if (user == null || !"COMPANY".equals(user.getRole())) {
            return "redirect:/company/login";
        }

        Application app = applicationRepository.findById(appId).orElse(null);

        // Security: Ensure the company owns the job this app belongs to
        if (app != null && app.getJob().getCompany().getId().equals(user.getId())) {

            app.setStatus(newStatus); // "Shortlisted", "Hired", "Rejected"
            applicationRepository.save(app);

            return "redirect:/company/job/" + app.getJob().getId() + "/applicants?success=StatusUpdated";
        }

        return "redirect:/company/dashboard?error=Unauthorized";
    }
}