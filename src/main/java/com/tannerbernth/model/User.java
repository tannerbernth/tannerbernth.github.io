package com.tannerbernth.model;

import java.time.LocalDateTime;
import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

@Entity
@Table(name = "user")
public class User {
    @Id
    @Column(unique = true, nullable = false)
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    @Length(max=20)
    @Column(nullable=false, unique=true)
    @NotEmpty
    private String username;

    @Column(nullable=false, unique=true)
    @NotEmpty
    private String email;

    @Length(min=5)
    private String password;

    @ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name="user_role", 
			joinColumns=@JoinColumn(name = "user_id", referencedColumnName="id"), 
			inverseJoinColumns=@JoinColumn(name = "role_id", referencedColumnName="id"))
	@OrderBy("id asc")
    private Collection<Role> roles;

    @ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name="user_group", 
			joinColumns=@JoinColumn(name = "user_id", referencedColumnName="id"), 
			inverseJoinColumns=@JoinColumn(name = "group_id", referencedColumnName="id"))
    private Collection<Group> groups;

    private boolean enabled;

    private int unreadCount;
    private Long commentCount;

    @Column
	private LocalDateTime createdDate;
	
	@Column
	private LocalDateTime lastLogin;

    public User() {
    	super();
    	this.enabled = true; //set enabled true by default *** change in the future (email verification)
    	this.unreadCount = 0;
    	this.lastLogin = null;
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Collection<Role> getRoles() {
		return roles;
	}

	public void setRoles(Collection<Role> roles) {
		this.roles = roles;
	}

	public Collection<Group> getGroups() {
		return groups;
	}

	public void setGroups(Collection<Group> groups) {
		this.groups = groups;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}

	public LocalDateTime getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(LocalDateTime lastLogin) {
		this.lastLogin = lastLogin;
	}

	public int getUnreadCount() {
		return unreadCount;
	}

	public void setUnreadCount(int unreadCount) {
		this.unreadCount = unreadCount;
	}

	public Long getCommentCount() {
		return commentCount;
	}

	public void setCommentCount(Long commentCount) {
		this.commentCount = commentCount;
	}

}