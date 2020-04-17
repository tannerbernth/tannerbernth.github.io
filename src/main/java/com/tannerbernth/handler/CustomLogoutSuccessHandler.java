package com.tannerbernth.handler;

import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.stereotype.Component;

import com.tannerbernth.service.UserDetailService;

@Component
public class CustomLogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler {

	private final Logger LOGGER = LoggerFactory.getLogger(CustomLogoutSuccessHandler.class);

	@Autowired
	private UserDetailService userDetailService;
	
	@Override
	public void onLogoutSuccess(HttpServletRequest request, 
										HttpServletResponse response, 
										Authentication authentication) throws IOException, ServletException {
		response.setStatus(HttpServletResponse.SC_OK);

		if (authentication != null) {
			LOGGER.info("Logout success handler! Redirecting home...");

			userDetailService.updateLastLogin(authentication.getName());
		}

		response.sendRedirect("/login");
		super.onLogoutSuccess(request,response,authentication);
	}
}