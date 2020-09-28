package com.trojanmd.controller;

import com.trojanmd.domain.Appointment;
import com.trojanmd.domain.BodyPart;
import com.trojanmd.domain.Doctor;
import com.trojanmd.domain.User;
import com.trojanmd.service.AppointmentService;
import com.trojanmd.service.BodyPartService;
import com.trojanmd.service.DoctorService;
import com.trojanmd.service.UserService;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private UserService userService;

    @Autowired
    private BodyPartService bodyPartService;

    @GetMapping("/user/before-appointment")
    public String beforeAppointment(Model model){
        List<BodyPart> bodyParts = bodyPartService.findAll();
        model.addAttribute("bodyParts", bodyParts);
        return "user/before-appointment";
    }

    @GetMapping("/user/make-appointment")
    public String appointment(Model model){
        List<Doctor> doctorList = doctorService.findAllDoctors();
        model.addAttribute("doctors", doctorList);
        return "user/make-appointment";
    }

    @ResponseBody
    @PostMapping("/user/make-appointment/date")
    public List<Appointment> app(@RequestBody JSONObject jsonObject) throws ParseException {
//        System.out.println(jsonObject.toString());
        String curDate = jsonObject.getString("curDate");
        Long docId = Long.parseLong(jsonObject.getString("docId"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date d = formatter.parse(curDate);
//        System.out.println("after conversion: " + d.toString());
        List<Appointment> apps = appointmentService.findAllByDateAndDoctorIdAndIsAvailable(d, docId, true);
//        for(Appointment a : apps) {
//            System.out.println("get date: " + a.getDate() + " begin time: "+ a.getBeginTime());
//        }
        if(apps.isEmpty()) System.out.println("empty");
        return apps;
    }

    @GetMapping("/user/confirm-appointment")
    public String confirm(@RequestParam String selectedSlot,
                          Model model){
        Long appointmentId = Long.parseLong(selectedSlot);
        Appointment appointment = appointmentService.get(appointmentId);
        Doctor doctor = appointment.getDoctor();
        model.addAttribute("appointment", appointment);
        model.addAttribute("doctor",doctor);
        return "user/confirm-appointment";
    }

    @PostMapping("/user/complete-appointment")
    public String completeAppointment(@RequestParam String appointmentId,
                                      Principal principal,
                                      RedirectAttributes redirectAttributes){
        User user = userService.findByEmail(principal.getName());
        Long id = Long.parseLong(appointmentId);
        Appointment appointment = appointmentService.get(id);
        appointment.setUser(user);
        appointment.setAvailable(false);
        appointmentService.save(appointment);
        redirectAttributes.addFlashAttribute("successMessage", "Appointment created successfully!");
        return "redirect:/user/my-appointment";
    }

    @GetMapping("/doctor/create-appointment")
    public String doctorAppointment(){
        return "doctor/create-appointment";
    }

    @ResponseBody
    @PostMapping("/doctor/create-appointment/date")
    public List<String> doctorGetAppointmentSlots(@RequestBody JSONObject jsonObject) throws ParseException{
        String curDate = jsonObject.getString("curDate");
        Long docId = Long.parseLong(jsonObject.getString("docId"));
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = dateFormatter.parse(curDate);
        List<Appointment> curAppointments = appointmentService.findAllByDateAndDoctorId(date, docId);
        List<Date> curBeginTimes = new ArrayList<>();
        if(curAppointments!=null && !curAppointments.isEmpty()) curBeginTimes = curAppointments.stream().map(Appointment::getBeginTime).collect(Collectors.toList());
        List<String> newBeginTimes = new ArrayList<>();

        for(int i = 8; i <= 18; i++){
            String hour = i + "";
            if(i<10) hour = "0" + hour;
            String onTheHour = hour + ":00:00";
            String half = hour + ":30:00";
            SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm");
            Date t1 = timeFormatter.parse(onTheHour);
            if(!curBeginTimes.contains(t1)){
                newBeginTimes.add(onTheHour);
            }
            Date t2 = timeFormatter.parse(half);
            if(!curBeginTimes.contains(t2)){
                newBeginTimes.add(half);
            }
        }
        return newBeginTimes;
    }


    @PostMapping("/doctor/create-appointment")
    public String createAppointment(@RequestParam("date") String date,
                                    @RequestParam("beginTime") String[] beginTimes,
                                    Principal principal) throws ParseException {

        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm:ss");
        Date d = dateFormatter.parse(date);

        for(String beginTime : beginTimes){
//            beginTime = date + " " + beginTime;
            Appointment appointment = new Appointment();
            appointment.setDate(d);
            appointment.setAvailable(true);
            Date t = timeFormatter.parse(beginTime);
            appointment.setBeginTime(t);
            Doctor doctor = doctorService.findByEmail(principal.getName());
            appointment.setDoctor(doctor);
            appointmentService.save(appointment);
        }
        return "doctor/create-appointment";
    }

    @GetMapping("/user/delete-appointment/{id}")
    public String userDeleteAppointment(@PathVariable Long id, RedirectAttributes redirectAttributes){
        Appointment appointment = appointmentService.get(id);
        appointment.setUser(null);
        appointment.setAvailable(true);
        appointmentService.save(appointment);
        redirectAttributes.addFlashAttribute("successMessage","Appointment cancelled successfully!");
        return "redirect:/user/my-appointment";
    }

    @GetMapping("/doctor/delete-appointment/{id}")
    public String doctorDeleteAppointment(@PathVariable Long id, RedirectAttributes redirectAttributes){
        appointmentService.get(id).setUser(null);
        appointmentService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage","Appointment cancelled successfully!");
        return "redirect:/doctor/my-appointment";
    }



}
