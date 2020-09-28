package com.trojanmd.controller;

import com.trojanmd.domain.Doctor;
import com.trojanmd.domain.User;
import com.trojanmd.service.DoctorService;
import com.trojanmd.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;

@Controller
@RequestMapping
public class LoginAndSignupController {

    @Autowired
    private UserService userService;

    @Autowired
    private DoctorService doctorService;

    // User:

    @GetMapping("/user-login")
    public String login(@RequestParam(value = "error", required = false) String error,
                        Model model,
                        Principal principal) {
        if(error != null){
            model.addAttribute("error", "Invalid log in credentials");
        }
        if(principal != null) return "index";
        return "user/user-login";
    }

    @GetMapping("/user-signup")
    public String signup(Principal principal){
//        if(principal != null) return "index";
        return "user/user-signup";
    }

    @PostMapping("/user-signup")
    public String addNewUser(@RequestParam String email,
                             @RequestParam String password,
                             @RequestParam String firstName,
                             @RequestParam String lastName){
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String encodedPwd = bCryptPasswordEncoder.encode(password);

        User user = new User();
        user.setFname(firstName);
        user.setLname(lastName);
        user.setEmail(email);
        user.setPassword(encodedPwd);
        user.setActive(true);
        user.setRoles("ROLE_USER");
        userService.save(user);
        return "user/user-login";
    }

    @RequestMapping("/user/logout")
    public String userLogout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return "redirect:/";
    }

    // Doctor:

    @GetMapping("/doctor-login")
    public String doctorLogin(@RequestParam(value = "error", required = false) String error,
                        Model model, Principal principal) {
        if(error != null){
            model.addAttribute("error", "Invalid log in credentials");
        }
//        if(principal != null) return "index";
        return "doctor/doctor-login";
    }

    @GetMapping("/doctor-signup")
    public String doctorSignup(Principal principal){
//        if(principal != null) return "index";
        return "doctor/doctor-signup";
    }

    @PostMapping("/doctor-signup")
    public String addNewDoctor(@RequestParam String email,
                               @RequestParam String password,
                               @RequestParam String firstName,
                               @RequestParam String lastName){
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String encodedPwd = bCryptPasswordEncoder.encode(password);

        Doctor doctor = new Doctor();
        doctor.setFname(firstName);
        doctor.setLname(lastName);
        doctor.setEmail(email);
        doctor.setPassword(encodedPwd);
        doctor.setProfileImg("https://ui-avatars.com/api/?name="+firstName+"+"+lastName+"&background=0D8ABC&color=fff");
        doctor.setActive(true);
        doctor.setRoles("ROLE_DOCTOR");
        doctorService.save(doctor);
        return "doctor/doctor-login";

    }

    @RequestMapping("/doctor/logout")
    public String doctorLogout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return "redirect:/";
    }

}
