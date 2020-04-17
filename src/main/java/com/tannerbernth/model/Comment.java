package com.tannerbernth.model;

import java.time.LocalDateTime;
import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
public class Comment {
		
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;

	@ManyToOne()
    @JoinColumn(name = "user_id")
	private User user;

	private String anonUsername;
	private String anonAddress;
	private boolean isAnon;

	@Column(columnDefinition = "TEXT")
	private String message;
	
	@ManyToOne()
    @JoinColumn(name = "article_id", nullable = false)
	private Article article;

	@Column
	private LocalDateTime createdDate;

	@Column
	private LocalDateTime lastModifiedDate;

	public Comment() {
		super();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getAnonUsername() {
		return anonUsername;
	}

	public void setAnonUsername(String anonUsername) {
		this.anonUsername = anonUsername;
	}

	public String getAnonAddress() {
		return anonAddress;
	}

	public void setAnonAddress(String anonAddress) {
		this.anonAddress = anonAddress;
	}

	public boolean getIsAnon() {
		return isAnon;
	}

	public void setIsAnon(boolean isAnon) {
		this.isAnon = isAnon;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Article getArticle() {
		return article;
	}

	public void setArticle(Article article) {
		this.article = article;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}

	public LocalDateTime getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}
}