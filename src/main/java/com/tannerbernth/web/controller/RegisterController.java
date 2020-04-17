package com.tannerbernth.controller;

import javax.validation.Valid;
import javax.servlet.http.HttpServletRequest;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.ModelAndView;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import org.springframework.web.context.request.WebRequest;
import org.springframework.ui.Model;

import java.security.Principal;
import org.springframework.security.core.userdetails.UserDetails;

import com.tannerbernth.model.User;
import com.tannerbernth.service.UserDetailService;
import com.tannerbernth.service.RecaptchaService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tannerbernth.web.dto.UserDto;
import com.tannerbernth.web.dto.UsernameDto;
import com.tannerbernth.web.error.UsernameAlreadyExistsException;
import com.tannerbernth.web.error.EmailAlreadyExistsException;

import javax.validation.ConstraintViolationException;
import org.springframework.transaction.TransactionSystemException;

@Controller
public class RegisterController {

	private final Logger LOGGER = LoggerFactory.getLogger(RegisterController.class);

	@Autowired 
	private UserDetailService userService;

	@Autowired
	private RecaptchaService recaptchaService;

	public RegisterController() {
		super();
	}

	@RequestMapping(value = "/login", method = RequestMethod.GET)
    public ModelAndView showLoginForm(Model model) {
        //LOGGER.info("Rendering registration page.");
        ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("login");
        String pageTitle = "TB - Login";
		modelAndView.addObject("title", pageTitle);
        return modelAndView;
    }

	@RequestMapping(value = "/register", method = RequestMethod.GET)
    public ModelAndView showRegistrationForm(Model model) {
        //LOGGER.info("Rendering registration page.");
        ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("register");
        UserDto userDto = new UserDto();
        modelAndView.addObject("userDto", userDto);
        String pageTitle = "TB - Register";
		modelAndView.addObject("title", pageTitle);
        return modelAndView;
    }

	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public ModelAndView registerNewUser(@Valid @ModelAttribute("userDto") UserDto userDto, 
										BindingResult result,
										RedirectAttributes redirectAttrs,
										HttpServletRequest request) {
		/*LOGGER.info("Registering user account with information {} {} {} {}",
					userDto.getUsername(),
					userDto.getEmail(),
					userDto.getPassword(),
					userDto.getMatchingPassword());*/

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("register");
		//modelAndView.addObject("userDto",userDto);
		String pageTitle = "TB - Register";
		modelAndView.addObject("title", pageTitle);
		String captchaResponse = request.getParameter("g-recaptcha-response");

		if (result.hasErrors()) {
			//modelAndView.addObject("error", "Unable to register user");
			if (!userDto.getPassword().equals(userDto.getMatchingPassword())) {
				modelAndView.addObject("nomatch", "Passwords do not match");
			}
			if (!recaptchaService.validInput(captchaResponse)) {
				modelAndView.addObject("recaptchaError", "reCAPTCHA error");
				return modelAndView;
			}

			return modelAndView;
		}

		if (!recaptchaService.isValidResponse(captchaResponse)) {
			modelAndView.addObject("recaptchaError", "reCAPTCHA error");
			return modelAndView;
		}

		try {
			userDto.setUsername(userDto.getUsername().replace(";",""));
			User registered = userService.registerNewUser(userDto);
		} catch (UsernameAlreadyExistsException e) {
			modelAndView.addObject("error", "Username is already taken");
			return modelAndView;
		} catch (EmailAlreadyExistsException e) {
			modelAndView.addObject("error", "Email is already in use");
			return modelAndView;
		} catch (TransactionSystemException e) {
			modelAndView.addObject("error", true);
			return modelAndView;
		}

		//modelAndView.addObject("successMessage", "User has been registered successfully");
		redirectAttrs.addFlashAttribute("success","User has been registered successfully");
		redirectAttrs.addFlashAttribute("title","TB - Login");
		modelAndView.setViewName("redirect:/login");
		return modelAndView;
	}

	@RequestMapping(value = "/_users/username")
	public @ResponseBody boolean userExists(@RequestBody UsernameDto username) {
		//LOGGER.info("what is {}",username.getUsername());
		if (userService.usernameExists(username.getUsername())) {
			return true;
		}
		return false;
	}

	@RequestMapping(value = "/_users/email")
	public @ResponseBody boolean emailExists(@RequestBody UsernameDto username) {
		//LOGGER.info("what is {}",username.getUsername());
		if (userService.emailExists(username.getEmail())) {
			return true;
		}
		return false;
	}
}