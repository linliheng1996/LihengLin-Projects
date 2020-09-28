package com.trojanmd.domain;

import javax.persistence.*;
import java.util.Date;

@Entity
public class ChatLog {
    @Id
    @GeneratedValue
    private Long id;

    @Temporal(TemporalType.TIME)
    private Date time;

    private String sender;

    private String receiver;

    private String content;

    public ChatLog(){}

    public ChatLog(String sender, String content, String receiver){
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
