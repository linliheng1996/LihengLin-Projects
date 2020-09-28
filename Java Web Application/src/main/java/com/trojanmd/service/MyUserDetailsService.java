package com.trojanmd.service;

import com.trojanmd.domain.MyUserDetails;
import com.trojanmd.domain.User;
import com.trojanmd.repository.DoctorRepository;
import com.trojanmd.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.Optional;

@Service
public class MyUserDetailsService implements UserDetailsService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    DoctorRepository doctorRepository;
    private static String USER_TYPE = "userType";


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
//        System.out.println("in loadUserByUsername, email:" + email);
        String userType = (String) RequestContextHolder.getRequestAttributes().getAttribute(USER_TYPE, RequestAttributes.SCOPE_SESSION);
//        System.out.println("in load, user type: " + userType);
        if(StringUtils.isEmpty(userType)) {
            throw new UsernameNotFoundException("Invalid email or password.");
        }
        UserDetails userDetails;
        if(userType.equals("doctor")){
            userDetails = new MyUserDetails(doctorRepository.findByEmail(email));
        }
        else if(userType.equals("user")) {
            User user = userRepository.findByEmail(email);
//            if(user==null) System.out.println("user null");
            userDetails = new MyUserDetails(user);
//            System.out.println("in user");
        }
        else userDetails = null;

        if(userDetails==null) throw new UsernameNotFoundException("Invalid email or password.");

        return userDetails;
    }
}
