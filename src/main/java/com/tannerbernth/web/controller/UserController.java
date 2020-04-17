package com.tannerbernth.web.controller;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.validation.BindingResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

import com.tannerbernth.web.dto.UserDto;
import com.tannerbernth.web.dto.ChangePasswordDto;
import com.tannerbernth.web.dto.SendEmailDto;
import com.tannerbernth.web.dto.MessageDto;
import com.tannerbernth.web.dto.CommentDto;
import com.tannerbernth.model.Message;
import com.tannerbernth.model.User;
import com.tannerbernth.model.Article;
import com.tannerbernth.model.Comment;
import com.tannerbernth.service.UserDetailService;
import com.tannerbernth.service.ArticleService;
import com.tannerbernth.service.SubscriberService;
import com.tannerbernth.service.UserGroupService;
import com.tannerbernth.service.RecaptchaService;
import com.tannerbernth.web.error.UsernameDoesNotExistException;
import com.tannerbernth.web.error.ArticleDoesNotExistException;
import com.tannerbernth.web.error.PasswordsDoNotMatchException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.Principal;

@Controller
public class UserController {

	private final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

	@Autowired 
	private UserDetailService userDetailService;

	@Autowired
	private ArticleService articleService;

	@Autowired
	private SubscriberService subscriberService;

	@Autowired
	private UserGroupService userGroupService;

	@Autowired
	private RecaptchaService recaptchaService;
	
	public UserController() {
		super();
	}

	@RequestMapping(value={"/","/index"}, method=RequestMethod.GET)
	public ModelAndView index() {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("index");
		String pageTitle = "Tanner Bernth";
		modelAndView.addObject("title", pageTitle);
		Collection<Article> articles = articleService.findTop3Articles();
		modelAndView.addObject("articles", articles);
		SendEmailDto sendEmailDto = new SendEmailDto();
		modelAndView.addObject("sendEmailDto", sendEmailDto);
		return modelAndView;
	}

	@RequestMapping(value={"/send-email"}, method=RequestMethod.POST)
	public ModelAndView sendEmail(@ModelAttribute("sendEmailDto") SendEmailDto sendEmailDto, 
								  RedirectAttributes redirectAttrs,
								  HttpServletRequest request) {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("redirect:"+request.getHeader("Referer"));
		String pageTitle = "Tanner Bernth";
		redirectAttrs.addFlashAttribute("title", pageTitle);
		String response = request.getParameter("g-recaptcha-response");

		//LOGGER.info("{} {} {}", sendEmailDto.getAddress(), sendEmailDto.getTitle(), sendEmailDto.getMessage());

		if (sendEmailDto.getAddress().isEmpty() || 
			sendEmailDto.getTitle().isEmpty() ||
			sendEmailDto.getMessage().isEmpty() ||
			!recaptchaService.validInput(response)) {
			redirectAttrs.addFlashAttribute("error","Please fill in all fields to send a message");
			return modelAndView;
		} 

		if (!sendEmailDto.getAddress().matches(".+@.+\\..+")) {
			redirectAttrs.addFlashAttribute("error","Please enter an email address");
			return modelAndView;
		}

		if (!recaptchaService.isValidResponse(response)) {
			redirectAttrs.addFlashAttribute("error", "reCAPTCHA error");
			return modelAndView;
		}

		String admin = "Tanner Bernth";
		MessageDto messageDto = new MessageDto();
		messageDto.setMessage(sendEmailDto.getTitle() + "\n\n" + sendEmailDto.getMessage());
		messageDto.setSenderUsername(admin);
		messageDto.setSenderId(new Long(1));
		messageDto.setGroupName("Contact Notification - " + sendEmailDto.getAddress());
		messageDto.setLeaderName(admin);
		messageDto.setNewGroup(true);
		Collection<String> members = new ArrayList<String>();
		members.add(admin);
		messageDto.setMembers(members);

		try {
			Message message = userGroupService.sendNewGroupAndMessage(messageDto);

			if (message == null) {
				redirectAttrs.addFlashAttribute("error", "Message failed to send");
				return modelAndView;
			}
			redirectAttrs.addFlashAttribute("success", "Message sent successfully");
		} catch (UsernameDoesNotExistException e) {
			redirectAttrs.addFlashAttribute("error", e.getMessage());
		}
		return modelAndView;
	}

	@RequestMapping(value={"/subscribe-email"}, method=RequestMethod.POST)
	public ModelAndView subscribeEmail(@RequestParam("email") String email, 
								 	   RedirectAttributes redirectAttrs) {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("redirect:/index");

		if (email.matches(".+@.+\\..+")) {
			subscriberService.addNewSubscriber(email);
			redirectAttrs.addFlashAttribute("success", "You are now subscribed for email notifications");
		} else {
			redirectAttrs.addFlashAttribute("error", "Failed to subscribe - Email may not be valid");
		}
		
		return modelAndView;
	}

	@RequestMapping(value={"/blog"}, method=RequestMethod.GET)
	public ModelAndView blog() {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("blog");
		String pageTitle = "TB - Blog";
		modelAndView.addObject("title", pageTitle);
		Collection<Article> articles = articleService.findTop10Articles();
		modelAndView.addObject("articles", articles);

		return modelAndView;
	}	

	@RequestMapping(value={"/blog"}, method=RequestMethod.POST)
	public ModelAndView load20Articles() {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("blog");
		Collection<Article> articles = articleService.findTop20Articles();
		modelAndView.addObject("articles", articles);
		String pageTitle = "TB - Blog";
		modelAndView.addObject("title", pageTitle);

		return modelAndView;
	}

	@RequestMapping(value={"/blog/{articleId}"}, method=RequestMethod.GET)
	public ModelAndView blogArticleId(@PathVariable("articleId") Long articleId,
									  RedirectAttributes redirectAttrs) {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("blog_article");
		CommentDto comment = new CommentDto();
		comment.setArticleId(articleId);
		modelAndView.addObject("commentDto", comment);

		Article article;
		Collection<Comment> comments;
		try {
			article = articleService.getArticleById(articleId);
			comments = articleService.findAllCommentsByArticleId(articleId);
			modelAndView.addObject("article", article);
			modelAndView.addObject("comments", comments);

			if (!article.isPublic()) {
				redirectAttrs.addFlashAttribute("error","Article does not exist");
				modelAndView.setViewName("redirect:/blog");
			} else {
				String pageTitle = "TB - " + article.getTitle();
				modelAndView.addObject("title", pageTitle);
			}
		} catch (ArticleDoesNotExistException e) {
			redirectAttrs.addFlashAttribute("error",e.getMessage());
			modelAndView.setViewName("redirect:/blog");
		}

		return modelAndView;
	}	

	@RequestMapping(value={"/blog/{articleId}/comment"}, method=RequestMethod.POST)
	public ModelAndView blogComment(Principal user,
									@ModelAttribute("commentDto") CommentDto commentDto,
									@PathVariable("articleId") Long articleId,
									 RedirectAttributes redirectAttrs,
									 HttpServletRequest request) {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("redirect:/blog/"+articleId);

		if (user == null) {
			String response = request.getParameter("g-recaptcha-response");
			if (commentDto.getName().isEmpty()) {
				commentDto.setName("Anonymous");
			}

			if (commentDto.getMessage().isEmpty()) {
				redirectAttrs.addFlashAttribute("error", "Please enter a message to comment");
				return modelAndView;
			} 

			if (!commentDto.getAddress().matches(".+@.+\\..+")) {
				redirectAttrs.addFlashAttribute("error","Please enter an email address");
				return modelAndView;
			}

			if (!recaptchaService.isValidResponse(response)) {
				redirectAttrs.addFlashAttribute("error", "reCAPTCHA error");
				return modelAndView;
			}
		} else {
			if (commentDto.getMessage().isEmpty()) {
				redirectAttrs.addFlashAttribute("error","Please enter a message to comment");
				return modelAndView;
			}
		}
		
		Article article;
		try {
			article = articleService.getArticleById(articleId);
			//modelAndView.addObject("article", article);

			if (!article.isPublic()) {
				redirectAttrs.addFlashAttribute("error","Article does not exist");
				modelAndView.setViewName("redirect:/blog");
			} else if (articleId != commentDto.getArticleId()) {
				redirectAttrs.addFlashAttribute("error","Please comment on your selected article");
				modelAndView.setViewName("redirect:/blog/"+articleId);
			} else {
				//String pageTitle = "TB - " + article.getTitle();
				//modelAndView.addObject("title", pageTitle);
				if (user != null) {
					articleService.addComment(getUser(user.getName()), commentDto);
				} else {
					articleService.addComment(commentDto);
				}
			}
		} catch (ArticleDoesNotExistException e) {
			redirectAttrs.addFlashAttribute("error",e.getMessage());
			modelAndView.setViewName("redirect:/blog");
		}

		if (user != null) {
			userDetailService.incrementCommentCount(getUser(user.getName()));
		}

		return modelAndView;
	}	

	@RequestMapping(value={"/profile"}, method=RequestMethod.GET)
	public ModelAndView profile() {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("profile");

		String pageTitle = "TB - Profile";
		modelAndView.addObject("title", pageTitle);
		ChangePasswordDto changePasswordDto = new ChangePasswordDto();
		modelAndView.addObject("changePasswordDto",changePasswordDto);
		return modelAndView;
	}

	@RequestMapping(value={"/profile/change-password"}, method=RequestMethod.POST)
	public ModelAndView changePassword(Principal user,
									  @Valid @ModelAttribute("changePasswordDto") ChangePasswordDto changePasswordDto,
									   BindingResult result,
									   RedirectAttributes redirectAttrs) {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("profile");
		String pageTitle = "TB - Profile";

		if (result.hasErrors()) {
			if (!changePasswordDto.getPassword().equals(changePasswordDto.getMatchingPassword())) {
				modelAndView.addObject("nomatch", "Passwords do not match");
			}
			modelAndView.addObject("title", pageTitle);
			return modelAndView;
		}

		redirectAttrs.addFlashAttribute("title", pageTitle);
		User principal = getUser(user.getName());
		try {
			userDetailService.updatePassword(principal, changePasswordDto);
		} catch (PasswordsDoNotMatchException e) {
			redirectAttrs.addFlashAttribute("error",e.getMessage());
			modelAndView.setViewName("redirect:/profile");
			return modelAndView;
		}

		redirectAttrs.addFlashAttribute("success","Password changed successfully");
		modelAndView.setViewName("redirect:/profile");
		return modelAndView;
	}

	@RequestMapping(value={"/profile/{username}"}, method=RequestMethod.GET)
	public ModelAndView publicProfile(@PathVariable String username,
									  RedirectAttributes redirectAttrs) {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("profile_public");
		try {
			User userProfile = userDetailService.findUserByUsername(username);
			if (userProfile != null) {
				modelAndView.addObject("userProfile",userProfile);
			} else {
				redirectAttrs.addFlashAttribute("error", "User does not exist");
				modelAndView.setViewName("redirect:/users");
				return modelAndView;
			}
		} catch (UsernameDoesNotExistException e) {
			redirectAttrs.addFlashAttribute("error", e.getMessage());
			modelAndView.setViewName("redirect:/users");
			return modelAndView;
		}
		String pageTitle = "TB - @" + username;
		modelAndView.addObject("title", pageTitle);
		return modelAndView;
	}	

	@RequestMapping(value={"/users"}, method=RequestMethod.GET)
	public ModelAndView userList() {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("users");
		String pageTitle = "TB - Users";
		modelAndView.addObject("title", pageTitle);
		Collection<User> users = (Collection<User>)userDetailService.findAllUsers();
		modelAndView.addObject("users", users);

		return modelAndView;
	}	

	@RequestMapping(value={"/about"}, method=RequestMethod.GET)
	public ModelAndView aboutMe() {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("about");
		String pageTitle = "TB - About";
		modelAndView.addObject("title", pageTitle);
		SendEmailDto sendEmailDto = new SendEmailDto();
		modelAndView.addObject("sendEmailDto", sendEmailDto);
		return modelAndView;
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

	/*@RequestMapping(value={"/jstest"}, method=RequestMethod.GET)
	public ModelAndView adminPlayground() {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("jstest");

		return modelAndView;
	}*/
}