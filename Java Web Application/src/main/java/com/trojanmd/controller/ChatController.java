package com.trojanmd.controller;

import com.trojanmd.domain.ChatLog;
import com.trojanmd.domain.Doctor;
import com.trojanmd.domain.User;
import com.trojanmd.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Controller
public class ChatController {

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private UserService userService;

    @Autowired
    private ChatLogService chatLogService;

    @GetMapping("/chat")
    public String chat(@RequestParam("email") String email,
                       HttpSession session,
                       Principal principal,
                       Model model){
//        System.out.println("in chat, email: " + email);
        User user = userService.findByEmail(email);
        Doctor doctor = doctorService.findByEmail(email);
        String myEmail = principal.getName();
        if(user != null){
            session.setAttribute("receiver", user);
        }
        else{
            session.setAttribute("receiver", doctor);
        }
        List<ChatLog> chatLogs =  chatLogService.getAllBySenderAndReceiver(myEmail, email);
        List<ChatLog> chatLogs2 = chatLogService.getAllBySenderAndReceiver(email, myEmail);
        chatLogs.addAll(chatLogs2);
        chatLogs.sort(Comparator.comparing(ChatLog::getTime));
//        for(ChatLog chatLog : chatLogs) System.out.println(chatLog);
        model.addAttribute("chatLogs", chatLogs);
        return "chat";
    }


}
