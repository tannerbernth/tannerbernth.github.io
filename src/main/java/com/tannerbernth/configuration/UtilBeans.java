package com.tannerbernth.configuration;

import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.SimpleMailMessage;
import java.util.Properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.tannerbernth.handler.CustomAuthenticationSuccessHandler;
import com.tannerbernth.handler.CustomLogoutSuccessHandler;

import com.tannerbernth.util.RandomStringGenerator;
import java.util.Random;

@Configuration
public class UtilBeans {
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(11);
		return bCryptPasswordEncoder;
	}

	@Bean
	public RandomStringGenerator randomStringGenerator() {
		RandomStringGenerator generator = new RandomStringGenerator();
		return generator;
	}

	@Bean
	public Random randomNumberGenerator() {
		Random random = new Random();
		return random;
	}

	@Bean
	public JavaMailSenderImpl mailSender() {
		JavaMailSenderImpl javaMailSenderImpl = new JavaMailSenderImpl();
		javaMailSenderImpl.setHost("smtp.gmail.com");
		javaMailSenderImpl.setPort(587);
		javaMailSenderImpl.setUsername("tannerbernth@gmail.com");
		javaMailSenderImpl.setPassword("W1ldcatfan!");

		Properties props = javaMailSenderImpl.getJavaMailProperties();
		props.put("mail.transport.protocol", "smtp");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.debug", "true");
		return javaMailSenderImpl;
	}

	@Bean
	public SimpleMailMessage templateMessage() {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom("no-reply@tannerbernth.com");
		message.setSubject("Email Inquiry");
		return message;
	}

}