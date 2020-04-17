package com.tannerbernth.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

	public MvcConfig() {
		super();
	}

	@Override 
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/").setViewName("forward:/index");
		registry.addViewController("/about");
		registry.addViewController("/blog");
		registry.addViewController("/index");
		registry.addViewController("/login");
		registry.addViewController("/messages");
		registry.addViewController("/_messages").setViewName("forward:/messages");
		registry.addViewController("/profile");
		registry.addViewController("/register");
		registry.addViewController("/users");
		registry.addViewController("/admin").setViewName("forward:/admin/index");
		registry.addViewController("/admin/index");
		registry.addViewController("/admin/article-manager");
		//registry.addViewController("/jstest");
	}
}