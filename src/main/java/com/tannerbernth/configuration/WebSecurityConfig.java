package com.tannerbernth.configuration;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.tannerbernth.service.UserDetailService;
import com.tannerbernth.repository.UserRepository;
//import com.tannerbernth.configuration.AuthenticationBeans;

import com.tannerbernth.handler.CustomAuthenticationSuccessHandler;
import com.tannerbernth.handler.CustomLogoutSuccessHandler;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailService userDetailsService;

    @Autowired
    private BCryptPasswordEncoder encoder;

    @Autowired
    private CustomAuthenticationSuccessHandler authenticationSuccessHandler;

    @Autowired
    private CustomLogoutSuccessHandler logoutSuccessHandler;

    public WebSecurityConfig() {
        super();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
            .userDetailsService(userDetailsService)
            .passwordEncoder(encoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .authorizeRequests()
                .antMatchers("/","/index","/login","/register","/about","/blog","/blog/**"/*,"/jstest"*/).permitAll()
                .antMatchers("/login","/login/**","/register","/register/**").not().hasAuthority("USER")
                .antMatchers("/users","/users/**","/profile","/profile/**","/messages","/messages/**").hasAuthority("USER")
                .antMatchers("/admin","/admin/**").hasAuthority("ADMIN")
                .anyRequest().permitAll()
                .and()
            .formLogin()
                .successHandler(authenticationSuccessHandler)
                .loginPage("/login")
                .permitAll()
                .and()
            .logout()
                .logoutSuccessHandler(logoutSuccessHandler)
                .logoutUrl("/logout");
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/resources/**");
    }

}