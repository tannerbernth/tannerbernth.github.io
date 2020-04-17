package com.tannerbernth.handler;

import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.tannerbernth.service.UserDetailService;

@Component
public class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	private final Logger LOGGER = LoggerFactory.getLogger(CustomAuthenticationSuccessHandler.class);

	@Autowired
	private UserDetailService userDetailService;
	
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, 
										HttpServletResponse response, 
										Authentication authentication) throws IOException, ServletException {
		response.setStatus(HttpServletResponse.SC_OK);

		LOGGER.info("Authentication success handler! Redirecting home...");

		//response.sendRedirect("/");
		super.onAuthenticationSuccess(request,response,authentication);
	}
}