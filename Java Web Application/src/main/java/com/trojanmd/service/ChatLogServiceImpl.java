package com.trojanmd.service;

import com.trojanmd.domain.ChatLog;
import com.trojanmd.repository.ChatLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ChatLogServiceImpl implements ChatLogService{

    @Autowired
    ChatLogRepository chatLogRepository;

    @Override
    public List<ChatLog> getAllBySenderAndReceiver(String sender, String receiver) {
        return chatLogRepository.findAllBySenderAndReceiver(sender, receiver);
    }

    @Override
    public void saveLog(String sender, String content, String receiver) {
        ChatLog chatLog = new ChatLog(sender, content, receiver);
        chatLog.setTime(new Date());
        chatLogRepository.save(chatLog);
    }
}
