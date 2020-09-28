package com.trojanmd.controller;

import com.trojanmd.domain.Appointment;
import com.trojanmd.domain.Doctor;
import com.trojanmd.domain.User;
import com.trojanmd.service.AppointmentService;
import com.trojanmd.service.DoctorService;
import com.trojanmd.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private AppointmentService appointmentService;

    @RequestMapping("/home")
    public String home(Principal principal,
                       HttpSession session){
        User user = userService.findByEmail(principal.getName());
        user.setPassword(null);
        session.setAttribute("loggedInUser", user);
        return "user/home";
    }

    @PostMapping("/user-info")
    public String userInfo(User user,
                           HttpSession session,
                           RedirectAttributes redirectAttributes){
        user = userService.update(user.getId(), user);
        user.setPassword(null);
        session.setAttribute("loggedInUser", user);
        redirectAttributes.addFlashAttribute("successMessage","Profile updated successfully!");
        return "redirect:/user/user-info";
    }

    @GetMapping("/user-info")
    public String updateUserInfo(Model model, Principal principal){
        User user = userService.findByEmail(principal.getName());
        user.setPassword(null);
        model.addAttribute("user", user);
        return "user/user-info";
    }

    @GetMapping("/visit")
    public String visit(){
        return "/user/visit";
    }

    @GetMapping("/my-appointment")
    public String appointment(Principal principal,
                              Model model){
        User user = userService.findByEmail(principal.getName());
        List<Appointment> appointments = appointmentService.findAllByUserId(user.getId());
        model.addAttribute("appointments",appointments);
        return "user/my-appointment";
    }

    @GetMapping("/my-doctor")
    public String doctorChatList(Model model,
                                 Principal principal){
        User user = userService.findByEmail(principal.getName());
        List<Appointment> appointments = appointmentService.findAllByUserId(user.getId());
        Set<Doctor> doctorSet = new HashSet<Doctor>();
        for(Appointment app : appointments){
            Doctor doctor = app.getDoctor();
            doctorSet.add(doctor);
        }
        model.addAttribute("doctors",doctorSet);
        return "user/my-doctor";
    }

    @GetMapping("/lab-test")
    public String labTest(){
        return "user/lab-test";
    }

    @GetMapping("/prescription")
    public String prescription(){
        return "user/prescription";
    }

    @GetMapping("/doctor-profile/{id}")
    public String displayDoctor(@PathVariable Long id,
                                Model model){
        Doctor doctor = doctorService.get(id);
        doctor.setPassword(null);
        model.addAttribute("doctor", doctor);
        return "user/doctor-display";
    }

}
