package com.trojanmd.config;

import com.trojanmd.filter.CustomUsernamePasswordAuthenticationFilter;
import com.trojanmd.service.MyUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class MultipleLoginSecurityConfig {

    @Configuration
    @Order(1)
    public static class UserSecurityConfiguration extends WebSecurityConfigurerAdapter {

        @Autowired
        MyUserDetailsService myUserDetailsService;

        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    //        auth.inMemoryAuthentication().passwordEncoder(new BCryptPasswordEncoder())
    //                .withUser("user@gmail.com").password(new BCryptPasswordEncoder().encode("pwd")).roles("USER");
    //
    //        auth.inMemoryAuthentication().passwordEncoder(new BCryptPasswordEncoder())
    //                .withUser("admin@gmail.com").password(new BCryptPasswordEncoder().encode("pwd")).roles("ADMIN","USER");

            auth.userDetailsService(myUserDetailsService).passwordEncoder(new BCryptPasswordEncoder());
        }
        @Override
        protected void configure(HttpSecurity http) throws Exception {
//            System.out.println("in configure");
            http
                    .antMatcher("/user/**")
                    .authorizeRequests()
                    .anyRequest()
                    .hasRole("USER")
                    .and()
                    .formLogin()
                    .loginPage("/user-login")
                    .loginProcessingUrl("/user/user-login")
                    .usernameParameter("email").passwordParameter("password")
                    .failureUrl("/user-login?error")
                    .defaultSuccessUrl("/user/home")
                    .and()
                    .logout()
                    .logoutUrl("/logout")
                    .logoutSuccessUrl("/")
                    .deleteCookies("JSESSIONID")
                    .and()
                    .csrf()
                    .disable();
            http.sessionManagement().invalidSessionUrl("/");
            http.addFilterBefore(new CustomUsernamePasswordAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        }

    }

    @Configuration
    @Order(2)
    public static class DoctorSecurityConfiguration extends WebSecurityConfigurerAdapter {

        @Autowired
        MyUserDetailsService myUserDetailsService;

        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            //        auth.inMemoryAuthentication().passwordEncoder(new BCryptPasswordEncoder())
            //                .withUser("user@gmail.com").
            //                password(new BCryptPasswordEncoder().encode("pwd")).roles("USER");
            //
            //        auth.inMemoryAuthentication().passwordEncoder(new BCryptPasswordEncoder())
            //                .withUser("admin@gmail.com")
            //                .password(new BCryptPasswordEncoder().encode("pwd")).roles("ADMIN","USER");

            auth.userDetailsService(myUserDetailsService).passwordEncoder(new BCryptPasswordEncoder());
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .antMatcher("/doctor/**")
                    .authorizeRequests()
                    .anyRequest()
                    .hasRole("DOCTOR")
                    .and()
                    .formLogin()
                    .loginPage("/doctor-login")
                    .loginProcessingUrl("/doctor/doctor-login")
                    .usernameParameter("email").passwordParameter("password")
                    .failureUrl("/doctor-login?error")
                    .defaultSuccessUrl("/doctor/home")
                    .and()
                    .logout()
                    .logoutUrl("/doctor/logout")
                    .logoutSuccessUrl("/")
                    .deleteCookies("JSESSIONID")
                    .and()
                    .csrf()
                    .disable();
            http.addFilterBefore(new CustomUsernamePasswordAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        }

    }

    @Configuration
    @Order(3)
    public class OtherSecurityConfiguration extends WebSecurityConfigurerAdapter {

        protected void configure(HttpSecurity http) throws Exception {
            http
                    .authorizeRequests()
                    .antMatchers("/","/css/**", "/img/**", "/js/**")
                    .permitAll()
                    .and()
                    .csrf()
                    .disable();
        }
    }


}
