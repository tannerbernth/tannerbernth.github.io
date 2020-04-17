package com.tannerbernth.service;

import java.time.LocalDateTime;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.tannerbernth.model.Article;
import com.tannerbernth.model.Comment;
import com.tannerbernth.model.User;
import com.tannerbernth.repository.ArticleRepository;
import com.tannerbernth.repository.CommentRepository;
import com.tannerbernth.web.dto.ArticleDto;
import com.tannerbernth.web.dto.CommentDto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tannerbernth.web.error.ArticleDoesNotExistException;
import com.tannerbernth.web.error.CommentDoesNotExistException;
//import com.tannerbernth.storage.StorageService;
import com.tannerbernth.web.error.StorageException;
//import com.tannerbernth.web.error.StorageFileNotFoundException;
import com.tannerbernth.util.RandomStringGenerator;

import com.tannerbernth.service.AmazonS3Service;

@Service
@Transactional
public class ArticleService {

	private final Logger LOGGER = LoggerFactory.getLogger(ArticleService.class);
	
	@Autowired
	private ArticleRepository articleRespository;

	@Autowired
	private CommentRepository commentRepository;

	//@Autowired 
	//private StorageService storageService;

	@Autowired
	private AmazonS3Service amazonS3Service;

	@Autowired
	private RandomStringGenerator generator;

	public Collection<Article> findTop3Articles() {
		return articleRespository.findTop3ByIsPublicTrueOrderByCreatedDateDesc();
	}

	public Collection<Article> findTop10Articles() {
		return articleRespository.findTop10ByIsPublicTrueOrderByCreatedDateDesc();
	}

	public Collection<Article> findTop20Articles() {
		return articleRespository.findTop20ByIsPublicTrueOrderByCreatedDateDesc();
	}

	public Collection<Article> findAllUnpublishedArticles() {
		return articleRespository.findAllByIsPublicFalseOrderByCreatedDateDesc();
	}

	public Article getArticleById(Long id) throws ArticleDoesNotExistException {
		Article article = articleRespository.findOneById(id);
		articleNullCheck(article);
		return article;
	}

	public Collection<Comment> findAllCommentsByArticleId(Long id) {
		return commentRepository.findAllByArticleIdOrderByCreatedDateAsc(id);
	}

	private void articleNullCheck(Article article) throws ArticleDoesNotExistException { 
		if (article == null) {
			throw new ArticleDoesNotExistException("Article does not exist");
		}
	}

	public Comment getCommentById(Long id) throws CommentDoesNotExistException {
		Comment comment = commentRepository.findOneById(id);
		commentNullCheck(comment);
		return comment;
	}

	private void commentNullCheck(Comment comment) throws CommentDoesNotExistException { 
		if (comment == null) {
			throw new CommentDoesNotExistException("Comment does not exist");
		}
	}

	// Logged user
	public Comment addComment(User user, CommentDto commentDto) {
		Comment comment = makeGeneralComment(commentDto.getArticleId(), commentDto);
		comment.setUser(user);
		comment.setIsAnon(false);
		return commentRepository.save(comment);
	}

	// Anonymous response
	public Comment addComment(CommentDto commentDto) {
		Comment comment = makeGeneralComment(commentDto.getArticleId(), commentDto);
		comment.setAnonUsername(commentDto.getName());
		comment.setAnonAddress(commentDto.getAddress());
		comment.setIsAnon(true);
		return commentRepository.save(comment);
	}

	private Comment makeGeneralComment(Long articleId, CommentDto commentDto) {
		Article article = getArticleById(articleId);
		Comment comment = new Comment(); 
		comment.setMessage(commentDto.getMessage());
		LOGGER.info(commentDto.getMessage());
		comment.setArticle(article);
		comment.setCreatedDate(LocalDateTime.now());
		comment.setLastModifiedDate(LocalDateTime.now());

		return comment;
	}

	public void deleteComment(Long id) throws CommentDoesNotExistException {
		Comment comment = getCommentById(id);
		commentRepository.delete(comment);
	}

	public void publishArticle(Long id) throws ArticleDoesNotExistException {
		Article article = getArticleById(id);
		article.setIsPublic(true);
		article.setCreatedDate(LocalDateTime.now());
		article.setLastModifiedDate(LocalDateTime.now());
	}

	// Offline image uploader to file system
	/*public Article createArticle(ArticleDto articleDto, User author) throws StorageException, StorageFileNotFoundException {
		String randomString = generator.generate(15);
		StringBuilder filebuffer = new StringBuilder();
		filebuffer
			.append(author.getId())
			.append("_")
			.append(randomString)
			.append("_")
			.append(StringUtils.cleanPath(articleDto.getFile().getOriginalFilename()));
		String filename = filebuffer.toString();
		storageService.store(articleDto.getFile(), filename);

		Article article = new Article();
		article.setTitle(articleDto.getTitle());
		article.setAuthor(author);
		article.setArticle(articleDto.getArticle());
		article.setIsPublic(articleDto.getIsPublic());
		article.setCreatedDate(LocalDateTime.now());
		article.setLastModifiedDate(LocalDateTime.now());
		article.setFilename(filename); 

		return articleRespository.save(article);
	}

	public void saveArticle(Long id, ArticleDto articleDto) throws ArticleDoesNotExistException {
		Article article = getArticleById(id);

		if (!articleDto.getFile().isEmpty()) {
			storageService.delete(article.getFilename());

			String randomString = generator.generate(15);
			StringBuilder filebuffer = new StringBuilder();
			filebuffer
				.append(article.getAuthor().getId())
				.append("_")
				.append(randomString)
				.append("_")
				.append(StringUtils.cleanPath(articleDto.getFile().getOriginalFilename()));
			String filename = filebuffer.toString();
			storageService.store(articleDto.getFile(), filename);

			article.setFilename(filename); 
		}

		article.setTitle(articleDto.getTitle());
		article.setArticle(articleDto.getArticle());
		article.setIsPublic(articleDto.getIsPublic());
		article.setLastModifiedDate(LocalDateTime.now());
		articleRespository.save(article);
	}

	public void deleteArticle(Long id) throws ArticleDoesNotExistException {
		Article article = getArticleById(id);
		storageService.delete(article.getFilename());
		articleRespository.delete(article);
	}*/


	// Amazon S3 uploader
	public Article createArticle(ArticleDto articleDto, User author) throws StorageException {

		Article article = new Article();

		if (!articleDto.getFile().isEmpty()) {
			String randomString = generator.generate(15);
			StringBuilder filebuffer = new StringBuilder();
			filebuffer
				.append(author.getId())
				.append("_")
				.append(randomString)
				.append("_")
				.append(StringUtils.cleanPath(articleDto.getFile().getOriginalFilename()));
			String filename = filebuffer.toString();
			//storageService.store(articleDto.getFile(), filename);

			// true - allow public read access
			amazonS3Service.uploadFileToS3Bucket(articleDto.getFile(), filename, true);
			article.setFilename(filename); 
		} else {
			article.setFilename("default.png");
		}

		article.setTitle(articleDto.getTitle());
		article.setAuthor(author);
		article.setArticle(articleDto.getArticle());
		article.setIsPublic(articleDto.getIsPublic());
		article.setCreatedDate(LocalDateTime.now());
		article.setLastModifiedDate(LocalDateTime.now());

		return articleRespository.save(article);
	}	

	public void saveArticle(Long id, ArticleDto articleDto) throws ArticleDoesNotExistException {
		Article article = getArticleById(id);

		if (!articleDto.getFile().isEmpty()) {
			//storageService.delete(article.getFilename());
			amazonS3Service.deleteFileFromS3Bucket(article.getFilename());

			String randomString = generator.generate(15);
			StringBuilder filebuffer = new StringBuilder();
			filebuffer
				.append(article.getAuthor().getId())
				.append("_")
				.append(randomString)
				.append("_")
				.append(StringUtils.cleanPath(articleDto.getFile().getOriginalFilename()));
			String filename = filebuffer.toString();
			//storageService.store(articleDto.getFile(), filename);

			amazonS3Service.uploadFileToS3Bucket(articleDto.getFile(), filename, true);

			article.setFilename(filename); 
		}

		article.setTitle(articleDto.getTitle());
		article.setArticle(articleDto.getArticle());
		article.setIsPublic(articleDto.getIsPublic());
		article.setLastModifiedDate(LocalDateTime.now());
		articleRespository.save(article);
	}

	public void deleteArticle(Long id) throws ArticleDoesNotExistException {
		Article article = getArticleById(id);
		//storageService.delete(article.getFilename());
		if (!article.getFilename().equals("default.png")) {
			amazonS3Service.deleteFileFromS3Bucket(article.getFilename());
		}
		articleRespository.delete(article);
	}
}