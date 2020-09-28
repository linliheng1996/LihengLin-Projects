package com.trojanmd.controller;

import com.trojanmd.domain.Message;
import com.trojanmd.service.ChatLogService;
import com.trojanmd.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private ChatLogService chatLogService;

    @MessageMapping("/chat")
    public void handleChat(Message message){
        String email = message.getReceiver();
//        System.out.println("sender: " + principal.getName());
//        System.out.println("receiver: " + email);
//        if (principal.getName().equals("test@gmail.com")) {
//            simpMessagingTemplate.convertAndSendToUser("doctor1@gmail.com",
//                    "/queue/notification", principal.getName() + " send message to you: "
//                            + message.getInfo());
//        } else {
//            simpMessagingTemplate.convertAndSendToUser("test@gmail.com",
//                    "/queue/notification", principal.getName() + " send message to you: "
//                            + message.getInfo());
//        }
        System.out.println(message.getSender() + " send " + message.getMessage() + " to " + message.getReceiver());
        chatLogService.saveLog(message.getSender(), message.getMessage(), message.getReceiver());
        simpMessagingTemplate.convertAndSendToUser(email,
                    "/queue/notification", message.getMessage());
    }

}
