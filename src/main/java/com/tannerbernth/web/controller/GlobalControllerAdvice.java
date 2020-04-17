package com.tannerbernth.web.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import com.tannerbernth.web.dto.UserDto;
import com.tannerbernth.model.User;
import com.tannerbernth.model.Group;
import com.tannerbernth.model.Message;
import java.util.Collection;
import com.tannerbernth.service.UserDetailService;
import com.tannerbernth.service.UserGroupService;

import java.security.Principal;
import com.tannerbernth.web.error.UsernameDoesNotExistException;

@ControllerAdvice
public class GlobalControllerAdvice {

	@Autowired 
	private UserDetailService userDetailService;

	@Autowired
	private UserGroupService userGroupService;
	
	@ModelAttribute("user") 
	public User getUser(Principal user) {
		if (user != null) {
			try {
				User loggedUser = userDetailService.findUserByUsername(user.getName());
				return loggedUser;
			} catch (UsernameDoesNotExistException e) {
				return null;
			}

		}
		return null;
	}
}