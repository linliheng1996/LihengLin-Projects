package com.trojanmd.repository;

import com.trojanmd.domain.ChatLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatLogRepository extends JpaRepository<ChatLog, Long> {
    List<ChatLog> findAllBySenderAndReceiver(String sender, String receiver);
}
