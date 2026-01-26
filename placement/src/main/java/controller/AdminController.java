package com.campus.placement.controller;

import org.springframework.web.bind.annotation.ResponseBody;
import com.campus.placement.entity.Application;
import com.campus.placement.entity.User;
import com.campus.placement.repository.ApplicationRepository;
import com.campus.placement.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    // 1. Admin Dashboard (With Analytics)
    @GetMapping("/admin")
    public String adminDashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"ADMIN".equals(user.getRole())) {
            return "redirect:/admin/login";
        }

        List<User> allUsers = userRepository.findAll();
        List<Application> allApps = applicationRepository.findAll();

        model.addAttribute("users", allUsers);
        model.addAttribute("applications", allApps);
        model.addAttribute("adminName", user.getFullName());

        // --- ANALYTICS LOGIC ---

        // 1. Status Counts
        long hired = allApps.stream().filter(a -> "Hired".equals(a.getStatus())).count();
        long shortlisted = allApps.stream().filter(a -> "Shortlisted".equals(a.getStatus())).count();
        long rejected = allApps.stream().filter(a -> "Rejected".equals(a.getStatus())).count();
        long applied = allApps.stream().filter(a -> "Applied".equals(a.getStatus())).count();

        model.addAttribute("statHired", hired);
        model.addAttribute("statShort", shortlisted);
        model.addAttribute("statReject", rejected);
        model.addAttribute("statApplied", applied);

        // 2. Branch Counts
        long cse = allApps.stream().filter(a -> "CSE".equals(a.getBranch())).count();
        long ece = allApps.stream().filter(a -> "ECE".equals(a.getBranch())).count();
        long mech = allApps.stream().filter(a -> "MECH".equals(a.getBranch())).count();
        long civil = allApps.stream().filter(a -> "CIVIL".equals(a.getBranch())).count();

        model.addAttribute("cseCount", cse);
        model.addAttribute("eceCount", ece);
        model.addAttribute("mechCount", mech);
        model.addAttribute("civilCount", civil);

        return "admin_dashboard";
    }

    // 2. Delete User
    @GetMapping("/admin/delete/{id}")
    public String deleteUser(@PathVariable Long id, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"ADMIN".equals(user.getRole())) return "redirect:/admin/login";

        userRepository.deleteById(id);
        return "redirect:/admin?success=UserDeleted";
    }

    // 3. Delete Application
    @GetMapping("/admin/application/delete/{appId}")
    public String deleteApplication(@PathVariable Long appId, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"ADMIN".equals(user.getRole())) return "redirect:/admin/login";

        applicationRepository.deleteById(appId);
        return "redirect:/admin?success=AppRemoved";
    }
    // --- TEMPORARY SECRET LINK TO MAKE ADMIN ---
    @GetMapping("/make-me-admin")
    @ResponseBody
    public String makeMeAdmin() {
        // 1. Get all users
        List<User> users = userRepository.findAll();

        // 2. Find your email and upgrade it
        for (User u : users) {
            if (u.getEmail().equals("saketkumarsahu000@gmail.com")) { // <--- CHANGE THIS TO YOUR EMAIL
                u.setRole("ADMIN");
                userRepository.save(u);
                return "SUCCESS! " + u.getFullName() + " is now an ADMIN. Go to /admin/login";
            }
        }
        return "User not found! Did you register first?";
    }
}