package com.tannerbernth.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.thymeleaf.extras.springsecurity4.dialect.SpringSecurityDialect;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.tannerbernth.handler.CustomAuthenticationSuccessHandler;
import com.tannerbernth.handler.CustomLogoutSuccessHandler;

@Configuration
public class AuthenticationBeans {
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(11);
		return bCryptPasswordEncoder;
	}

	@Bean
	public SpringSecurityDialect securityDialect() {
		return new SpringSecurityDialect();
	}
}