package com.trojanmd.service;

import com.trojanmd.domain.Appointment;
import com.trojanmd.domain.User;

import java.util.List;

public interface UserService {
    void save(User user);
    User findByEmail(String email);
    List<User> findAllUsers();
    User update(Long id, User user);
}
