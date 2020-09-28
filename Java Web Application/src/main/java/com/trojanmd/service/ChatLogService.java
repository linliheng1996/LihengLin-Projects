package com.trojanmd.service;

import com.trojanmd.domain.ChatLog;

import java.util.List;

public interface ChatLogService {
    List<ChatLog> getAllBySenderAndReceiver(String sender, String receiver);
    void saveLog(String sender, String content, String receiver);
}
