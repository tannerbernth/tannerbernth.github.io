package com.tannerbernth.web.controller;

import javax.validation.Valid;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

import com.tannerbernth.web.dto.ArticleDto;
import com.tannerbernth.web.dto.CommentDto;
import com.tannerbernth.model.Article;
import com.tannerbernth.model.User;
import com.tannerbernth.service.ArticleService;
import com.tannerbernth.service.UserDetailService;
import com.tannerbernth.web.error.UsernameDoesNotExistException;
import com.tannerbernth.web.error.ArticleDoesNotExistException;

import com.tannerbernth.web.error.StorageException;
import com.tannerbernth.web.error.StorageFileNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.Principal;

@Controller
public class AdminController {

	private final Logger LOGGER = LoggerFactory.getLogger(AdminController.class);

	@Autowired
	ArticleService articleService;

	@Autowired
	UserDetailService userDetailService;

	public AdminController() {
		super();
	}

	@RequestMapping(value={"/admin/","/admin/index"}, method=RequestMethod.GET)
	public ModelAndView adminIndex() {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("admin/index");

		LOGGER.info("Accessing admin controller");
		return modelAndView;
	}

	@RequestMapping(value={"/admin/article-manager"}, method=RequestMethod.GET)
	public ModelAndView articleManager() {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("admin/article-manager");
		ArticleDto articleDto = new ArticleDto();
		modelAndView.addObject("articleDto",articleDto);

		Collection<Article> articles = articleService.findAllUnpublishedArticles();
		modelAndView.addObject("articles", articles);

		return modelAndView;
	}

	@RequestMapping(value={"/admin/article-manager"}, method=RequestMethod.POST)
	public ModelAndView createNewArticle(@ModelAttribute("articleDto") @Valid ArticleDto articleDto, 
										 Principal user,
										 RedirectAttributes redirectAttrs) {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("redirect:/admin/article-manager");

		if (articleDto.getTitle().length() > 255) {
			redirectAttrs.addFlashAttribute("error", "Title is longer than 255 characters");
			return modelAndView;
		}

		User author = null;
		try {
			author = userDetailService.findUserByUsername(user.getName());
		} catch (UsernameDoesNotExistException e) {
			redirectAttrs.addFlashAttribute("error", "Failed to create article");
			return modelAndView;
		}

		try {
			//storageService.store(articleDto.getFile());

			articleService.createArticle(articleDto, author);
			redirectAttrs.addFlashAttribute("success","Article created successfully");

			return modelAndView;
		} catch (StorageException e) {
			redirectAttrs.addFlashAttribute("error", "Error uploading image");
			return modelAndView;
		} catch (StorageFileNotFoundException e) {
			redirectAttrs.addFlashAttribute("error", "Error uploading image");
			return modelAndView;
		}
	}

	@RequestMapping(value={"/admin/article-manager/{id}"}, method=RequestMethod.POST)
	public ModelAndView publishArticle(@PathVariable Long id, 
										RedirectAttributes redirectAttrs) {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("redirect:/admin/article-manager");
		
		try {
			articleService.publishArticle(id);
			redirectAttrs.addFlashAttribute("success","Published article successfully");

			return modelAndView;
		} catch (ArticleDoesNotExistException e) {
			redirectAttrs.addFlashAttribute("error", e.getMessage());
			return modelAndView;
		}
	}

	@RequestMapping(value={"/admin/article-manager/{id}/edit"}, method=RequestMethod.GET)
	public ModelAndView editArticle(@PathVariable Long id, RedirectAttributes redirectAttrs) {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("admin/article-manager");

		ArticleDto articleDto = new ArticleDto();
		Collection<Article> articles = articleService.findAllUnpublishedArticles();

		try {
			Article editArticle = articleService.getArticleById(id);
			
			articleDto.setTitle(editArticle.getTitle());
			articleDto.setArticle(editArticle.getArticle());
			articleDto.setIsPublic(editArticle.isPublic());

			modelAndView.addObject("articleImage", editArticle.getFilename());
			modelAndView.addObject("articleDto",articleDto);
			modelAndView.addObject("articles", articles);

			return modelAndView;
		} catch (ArticleDoesNotExistException e) {
			modelAndView.setViewName("redirect:/admin/article-manager");
			redirectAttrs.addFlashAttribute("error", e.getMessage());
			return modelAndView;
		}
	}

	@RequestMapping(value={"/admin/article-manager/{id}/edit"}, method=RequestMethod.POST)
	public ModelAndView saveArticle(@PathVariable Long id, 
									@ModelAttribute("articleDto") @Valid ArticleDto articleDto,
									 RedirectAttributes redirectAttrs) {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("redirect:/admin/article-manager");

		if (articleDto.getTitle().length() > 255) {
			redirectAttrs.addFlashAttribute("error", "Title is longer than 255 characters");
			return modelAndView;
		}
		
		try {
			articleService.saveArticle(id, articleDto);
			redirectAttrs.addFlashAttribute("success","Saved article successfully");
			return modelAndView;
		} catch (ArticleDoesNotExistException e) {
			redirectAttrs.addFlashAttribute("error", e.getMessage());
			return modelAndView;
		}
	}

	@RequestMapping(value={"/admin/article-manager/{id}/delete"}, method=RequestMethod.POST)
	public ModelAndView deleteArticle(@PathVariable Long id, 
									 RedirectAttributes redirectAttrs,
									 HttpServletRequest request) {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("redirect:"+request.getHeader("Referer"));
		
		try {
			articleService.deleteArticle(id);
			redirectAttrs.addFlashAttribute("success","Deleted article successfully");
			return modelAndView;
		} catch (ArticleDoesNotExistException e) {
			redirectAttrs.addFlashAttribute("error", e.getMessage());
			return modelAndView;
		}
	}

	@RequestMapping(value={"/admin/article-manager/view/{articleId}"}, method=RequestMethod.GET)
	public ModelAndView adminViewArticle(@PathVariable("articleId") Long articleId,
										 RedirectAttributes redirectAttrs) {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("blog_article");
		CommentDto comment = new CommentDto();
		comment.setArticleId(articleId);
		modelAndView.addObject("commentDto", comment);

		Article article;
		try {
			article = articleService.getArticleById(articleId);
			modelAndView.addObject("article", article);

			if (article.isPublic()) {
				redirectAttrs.addFlashAttribute("error","Article is public");
				modelAndView.setViewName("redirect:/admin/article-manager");
			}
		} catch (ArticleDoesNotExistException e) {
			redirectAttrs.addFlashAttribute("error",e.getMessage());
			modelAndView.setViewName("redirect:/admin/article-manager");
		}

		return modelAndView;
	}

	@RequestMapping(value={"/admin/article-manager/view/{articleId}/comment"}, method=RequestMethod.POST)
	public ModelAndView blogComment(Principal user,
									@ModelAttribute("commentDto") CommentDto commentDto,
									@PathVariable("articleId") Long articleId,
									 RedirectAttributes redirectAttrs) {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("blog_article");

		Article article;
		try {
			article = articleService.getArticleById(articleId);
			modelAndView.addObject("article", article);

			if (!article.isPublic()) {
				redirectAttrs.addFlashAttribute("error","Article does not exist");
				modelAndView.setViewName("redirect:/admin/article-manager");
			} else if (articleId != commentDto.getArticleId()) {
				redirectAttrs.addFlashAttribute("error","Please comment on your selected article");
				modelAndView.setViewName("redirect:/admin/article-manager/"+articleId);
			} else {
				String pageTitle = "TB - " + article.getTitle();
				modelAndView.addObject("title", pageTitle);
			}
		} catch (ArticleDoesNotExistException e) {
			redirectAttrs.addFlashAttribute("error",e.getMessage());
			modelAndView.setViewName("redirect:/admin/article-manager");
		}

		return modelAndView;
	}	

}