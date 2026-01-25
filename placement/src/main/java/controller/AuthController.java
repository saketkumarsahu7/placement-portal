package com.campus.placement.controller;

import com.campus.placement.entity.User;
import com.campus.placement.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    // --- GLOBAL LOGIN FALLBACK ---
    // If someone tries to go to /login directly, send them to the home page
    @GetMapping("/login")
    public String showGenericLogin() {
        return "redirect:/";
    }

    // ==========================================
    // 1. STRICT STUDENT ZONE
    // ==========================================
    @GetMapping("/student/login")
    public String showStudentLogin() {
        return "student_login";
    }

    @PostMapping("/student/login-process")
    public String processStudentLogin(@RequestParam String email, @RequestParam String password, HttpSession session) {
        User user = userRepository.findByEmail(email);

        // Security Check: Must exist + Password Match + MUST BE STUDENT
        if (user != null && user.getPassword().equals(password) && "STUDENT".equals(user.getRole())) {
            session.setAttribute("user", user);
            return "redirect:/student/dashboard";
        }
        return "redirect:/student/login?error";
    }

    @GetMapping("/student/register")
    public String showStudentRegister(Model model) {
        model.addAttribute("user", new User());
        return "student_register";
    }

    @PostMapping("/student/register")
    public String registerStudent(@ModelAttribute User user) {
        // Prevent duplicate email crash
        if (userRepository.findByEmail(user.getEmail()) != null) {
            return "redirect:/student/login?error";
        }
        user.setRole("STUDENT");
        userRepository.save(user);
        return "redirect:/student/login?success";
    }

    // ==========================================
    // 2. STRICT COMPANY ZONE
    // ==========================================
    @GetMapping("/company/login")
    public String showCompanyLogin() {
        return "company_login";
    }

    @PostMapping("/company/login-process")
    public String processCompanyLogin(@RequestParam String email, @RequestParam String password, HttpSession session) {
        User user = userRepository.findByEmail(email);

        // Security Check: Must exist + Password Match + MUST BE COMPANY
        if (user != null && user.getPassword().equals(password) && "COMPANY".equals(user.getRole())) {
            session.setAttribute("user", user);
            return "redirect:/company/dashboard";
        }
        return "redirect:/company/login?error";
    }

    @GetMapping("/company/register")
    public String showCompanyRegister(Model model) {
        model.addAttribute("user", new User());
        return "company_register";
    }

    @PostMapping("/company/register")
    public String registerCompany(@ModelAttribute User user) {
        // Prevent duplicate email crash
        if (userRepository.findByEmail(user.getEmail()) != null) {
            return "redirect:/company/login?error";
        }
        user.setRole("COMPANY");
        userRepository.save(user);
        return "redirect:/company/login?success";
    }

    // ==========================================
    // 3. STRICT ADMIN ZONE
    // ==========================================
    @GetMapping("/admin/login")
    public String showAdminLogin() {
        return "admin_login";
    }

    @PostMapping("/admin/login-process")
    public String processAdminLogin(@RequestParam String email, @RequestParam String password, HttpSession session) {
        User user = userRepository.findByEmail(email);

        // Security Check: Must exist + Password Match + MUST BE ADMIN
        if (user != null && user.getPassword().equals(password) && "ADMIN".equals(user.getRole())) {
            session.setAttribute("user", user);
            return "redirect:/admin";
        }
        return "redirect:/admin/login?error";
    }
}