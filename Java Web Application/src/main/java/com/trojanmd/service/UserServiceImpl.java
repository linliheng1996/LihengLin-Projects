package com.trojanmd.service;

import com.trojanmd.domain.Appointment;
import com.trojanmd.domain.Doctor;
import com.trojanmd.domain.User;
import com.trojanmd.repository.UserRepository;
//import com.trojanmd.util.MD5Utils;
import com.trojanmd.utils.MyBeanUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    UserRepository userRepository;

    @Override
    public void save(User user) {
        userRepository.save(user);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User update(Long id, User user) {
        User u = userRepository.getOne(id);
        BeanUtils.copyProperties(user, u, MyBeanUtils.getNullPropertyNames(user));
        u.setActive(true);
        return userRepository.save(u);
    }

}
