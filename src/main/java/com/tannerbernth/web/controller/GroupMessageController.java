package com.tannerbernth.web.controller;

import javax.validation.Valid;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.Principal;

import com.tannerbernth.model.User;
import com.tannerbernth.model.Group;
import com.tannerbernth.model.Message;
import com.tannerbernth.model.UserGroup;
import com.tannerbernth.service.UserDetailService;
import com.tannerbernth.service.UserGroupService;
import com.tannerbernth.web.dto.MessageDto;
import com.tannerbernth.web.dto.UsernameDto;
import com.tannerbernth.web.dto.SimpleUserDto;
import com.tannerbernth.web.error.GroupDoesNotExistException;
import com.tannerbernth.web.error.UsernameDoesNotExistException;

@Controller
public class GroupMessageController {

	private final Logger LOGGER = LoggerFactory.getLogger(GroupMessageController.class);

	@Autowired 
	private UserDetailService userDetailService;

	@Autowired
	private UserGroupService userGroupService;

	@RequestMapping(value={"/messages"}, method=RequestMethod.GET)
	public ModelAndView inbox(Principal user) {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("messages");
		MessageDto messageDto = new MessageDto();
		modelAndView.addObject("messageDto", messageDto);
		if (user == null) {
			return modelAndView;
		}

		/*Collection<Group> userGroups = userGroupService.findGroupsByUser(getUser(user.getName()));
		if (userGroups.size() > 0) {
			modelAndView.addObject("userGroups",userGroups);
		}*/

		User principal = getUser(user.getName());

		Collection<Group> groups = userGroupService.findAllGroupsByUser(principal);

		ArrayList<Message> messageByGroup = new ArrayList<Message>();
		ArrayList<LocalDateTime> lastReadDates = new ArrayList<LocalDateTime>();
		if (groups != null) {
			for (Group group : groups) {
				messageByGroup.add(userGroupService.findMostRecentMessageByGroupId(group.getId()));
				lastReadDates.add(userGroupService.findUserGroupByUserAndGroup(principal,group).getLastReadDate());
			}
		}
		if (messageByGroup.size() != 0) {
			LOGGER.info("should be some messages **********");
			userDetailService.resetUnreadCount(principal);
			modelAndView.addObject("messages",messageByGroup);
			modelAndView.addObject("lastreaddates",lastReadDates);
		}

		String pageTitle = "TB - Messages";
		modelAndView.addObject("title", pageTitle);
		
		return modelAndView;
	}

	@RequestMapping(value={"/messages"}, method=RequestMethod.POST)
	public ModelAndView newGroupAndMessage(Principal user, 
											@ModelAttribute("messageDto") @Valid MessageDto messageDto, 
											BindingResult result, 
											RedirectAttributes redirectAttrs) {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("redirect:/messages");

		if (messageDto.getMessage().equals("")) {
			return modelAndView;
		}

		updateMessageDto(messageDto, user, true);

		Message message = null;
		if (result != null) {
			try {
				message = userGroupService.sendNewGroupAndMessage(messageDto);
			} catch (UsernameDoesNotExistException e) {
				redirectAttrs.addFlashAttribute("error", e.getMessage());
			}
		}

		String pageTitle = "TB - Messages";
		redirectAttrs.addFlashAttribute("title", pageTitle);

		if (message == null) {
			redirectAttrs.addFlashAttribute("error", "Message failed to send");
			return modelAndView;
		}

		redirectAttrs.addFlashAttribute("success", "Message sent successfully");
		return modelAndView;
	}

	@RequestMapping(value={"/_messages/{groupId}"})//, method=RequestMethod.GET)
	public @ResponseBody ModelAndView selectedGroupMessage(@RequestHeader("Content-Type") String content,
											Principal user, 
											@PathVariable("groupId") 
											Long groupId, 
											RedirectAttributes redirectAttrs) {
		ModelAndView modelAndView = new ModelAndView();

		if (content == null || !content.equals("application/json")) {
			modelAndView.setViewName("redirect:/messages");
			return modelAndView;
		}
		modelAndView.setViewName("messages_groupId");
		User principal = getUser(user.getName());
		Group group = userGroupService.findGroupById(groupId);

		LOGGER.info("is user in this group? {}",userGroupService.userInGroup(principal,group));

		if (group == null) {
			//redirectAttrs.addFlashAttribute("error", "This chat does not exist");
			modelAndView.addObject("error","This chat does not exist");
			//modelAndView.setViewName("redirect:/messages/");
			return modelAndView;
		}

		if (!userGroupService.userInGroup(principal,group)) {
			modelAndView.addObject("error","This chat does not exist");
			//redirectAttrs.addFlashAttribute("error", "This chat does not exist *");
			//modelAndView.setViewName("redirect:/messages/");
			return modelAndView;
		}

		MessageDto messageDto = new MessageDto();
		messageDto.setGroupId(groupId);
		modelAndView.addObject("messageDto", messageDto);

		//Collection<Message> messages = userGroupService.findMessagesByGroupId(groupId);
		Collection<Message> messages = userGroupService.findBottom100MessagesByGroupId(groupId);
		modelAndView.addObject("messages", messages);

		UserGroup userGroup = userGroupService.findUserGroupByUserAndGroup(principal, group);
		modelAndView.addObject("lastreaddate",userGroup.getLastReadDate());
		userGroupService.updateLastReadDate(userGroup);

		return modelAndView;
	}

	@RequestMapping(value={"/messages/{groupId}"}, method=RequestMethod.POST)
	public @ResponseBody ModelAndView newGroupMessage(Principal user, 
										@PathVariable("groupId") Long groupId, 
										/*@ModelAttribute("messageDto") @Valid MessageDto messageDto*/
										@RequestBody MessageDto messageDto, 
										BindingResult result, 
										RedirectAttributes redirectAttrs) {
		ModelAndView modelAndView = new ModelAndView();
		//modelAndView.setViewName("redirect:/messages/"+groupId);
		//modelAndView.setViewName("redirect:/messages/");
		modelAndView.setViewName("messages_groupId");
		//MessageDto messageDto = new MessageDto();
		//messageDto.setMessage(submitMessage);
		LOGGER.info("message '{}' group id '{}' ", messageDto.getMessage(), groupId);
		messageDto.setGroupId(groupId);
		if (messageDto.getMessage().equals("")) {
			return modelAndView;
		}

		User principal = getUser(user.getName());
		Group group = userGroupService.findGroupById(groupId);

		if (group == null) {
			redirectAttrs.addFlashAttribute("error", "This chat does not exist");
			//modelAndView.setViewName("redirect:/messages/");
			return modelAndView;
		}

		if (!userGroupService.userInGroup(principal,group)) {
			LOGGER.warn("User {} Id {} attempted to send a message to chat id {}",principal.getUsername(), principal.getId(), groupId);
			redirectAttrs.addFlashAttribute("error", "This chat does not exist *");
			//modelAndView.setViewName("redirect:/messages/");
			return modelAndView;
		}
		modelAndView.addObject("group",group);

		updateMessageDto(messageDto, user, false);

		Message message = null;
		if (result != null) {
			try {
				message = userGroupService.sendNewMessage(messageDto);
			} catch (GroupDoesNotExistException e) {
				redirectAttrs.addFlashAttribute("error", e.getMessage());
				LOGGER.info("group doesnt exist?");
			}
		}

		if (message == null) {
			LOGGER.info("message failed to send");
			redirectAttrs.addFlashAttribute("error", "Message failed to send");
			return modelAndView;
		}

		return modelAndView;
	}

	@RequestMapping(value = "/_messages/users", produces="application/json")
	public @ResponseBody Collection<SimpleUserDto> findSimilarUsernames(@RequestBody UsernameDto username) {
		//LOGGER.info("what is {}",username.getUsername());
		if (username.getUsername() == null || username.getUsername().equals("")) {
			return null;
		}
		Collection<User> users = userDetailService.findTop10ByUsernameStartingWith(username.getUsername());
		if (users.size() == 0) {
			return null;
		}
		Collection<SimpleUserDto> simpleUsers = new ArrayList<SimpleUserDto>();
		for (User user : users) {
			SimpleUserDto simpleUser = new SimpleUserDto();
			simpleUser.setId(user.getId());
			simpleUser.setUsername(user.getUsername());
			simpleUsers.add(simpleUser);
		}
		LOGGER.info("users return size {}", users.size());
		return simpleUsers;
	}

	private void updateMessageDto(MessageDto messageDto, Principal user, boolean newGroup) {
		User principal = getUser(user.getName());
		messageDto.setSenderUsername(principal.getUsername());
		messageDto.setSenderId(principal.getId());
		if (newGroup) {
			messageDto.setLeaderName(principal.getUsername());
		}
	}

	private User getUser(String username) {
		User user = null;
		try {
			user = userDetailService.findUserByUsername(username);
		} catch (UsernameDoesNotExistException e) {
			LOGGER.info("Error: {} Username: {}",e.getMessage(),username);
		}

		return user;
	}
}