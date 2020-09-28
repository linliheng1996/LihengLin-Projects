package com.trojanmd.controller;

import com.trojanmd.domain.Appointment;
import com.trojanmd.domain.Doctor;
import com.trojanmd.domain.User;
import com.trojanmd.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/doctor")
public class DoctorController {

    @Autowired
    private DoctorServiceImpl doctorService;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private DiseaseService diseaseService;

    @Autowired
    private AppointmentService appointmentService;

    @RequestMapping("/home")
    public String doctor(Principal principal,
                         HttpSession session){
        Doctor doctor = doctorService.findByEmail(principal.getName());
        session.setAttribute("loggedInUser", doctor);
        return "doctor/home";
    }

    @GetMapping("/my-patient")
    public String userChatList(Model model,
                               Principal principal){
        Doctor doctor = doctorService.findByEmail(principal.getName());
        List<Appointment> appointments = appointmentService.findAllByDoctorId(doctor.getId());
        Set<User> userSet = new HashSet<>();
        for(Appointment app : appointments){
            User user = app.getUser();
            if(user != null) userSet.add(user);
        }
        model.addAttribute("users", userSet);
        return "doctor/my-patient";
    }

    @GetMapping("/profile")
    public String profile(Model model, Principal principal){
        Doctor doctor = doctorService.findByEmail(principal.getName());
        model.addAttribute("doctor", doctor);
        model.addAttribute("diseases", diseaseService.listDiseases());
        return "doctor/profile";
    }

//    @GetMapping("/my-appointment")
//    public String myAppointment(Model model, Principal principal){
//        Long id = doctorService.findByEmail(principal.getName()).getId();
//        List<Appointment> appointments = appointmentService.findAllByDoctorId(id);
//        model.addAttribute("appointments", appointments);
//        return "/doctor/my-appointment";
//    }

    @GetMapping("/my-appointment")
    public String myAppointment(@PageableDefault(size = 15, sort = {"date"}) Pageable pageable,
                                Model model, Principal principal){
        Long id = doctorService.findByEmail(principal.getName()).getId();
        Page<Appointment> appointments = appointmentService.findAllByDoctorId(id, pageable);
        model.addAttribute("page", appointments);
        return "doctor/my-appointment";
    }

    @PostMapping("/profile")
    public String editProfile(@RequestParam String ids,
                              Doctor doctor,
                              RedirectAttributes redirectAttributes){
//        System.out.println("ids: " + ids);
        if(ids!=null && ids.length()!=0) doctor.setDiseases(diseaseService.listDiseases(ids));
        doctorService.update(doctor.getId(), doctor);
//        doctorService.save(doctor);
        redirectAttributes.addFlashAttribute("successMessage","Profile updated successfully!");
        return "redirect:/doctor/profile";
    }
}
