package com.trojanmd.filter;

import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class CustomUsernamePasswordAuthenticationFilter extends GenericFilterBean {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if(servletRequest instanceof HttpServletRequest) {
            HttpServletRequest r = (HttpServletRequest)servletRequest;
            String loginType = r.getParameter("userType");
            String email = r.getParameter("email");
            r.getSession().setAttribute("userType", loginType);
//            System.out.println("Filter: " + loginType + " email:" +email);
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
