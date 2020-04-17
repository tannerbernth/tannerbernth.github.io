package com.tannerbernth.service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.tannerbernth.model.Role;
import com.tannerbernth.model.User;
import com.tannerbernth.model.Group;
import com.tannerbernth.model.UserGroup;
import com.tannerbernth.model.Article;
import com.tannerbernth.web.dto.UserDto;
import com.tannerbernth.web.dto.ChangePasswordDto;
import com.tannerbernth.repository.PermissionRepository;
import com.tannerbernth.repository.RoleRepository;
import com.tannerbernth.repository.UserRepository;
import com.tannerbernth.repository.UserGroupRepository;
import com.tannerbernth.web.error.UsernameAlreadyExistsException;
import com.tannerbernth.web.error.UsernameDoesNotExistException;
import com.tannerbernth.web.error.EmailAlreadyExistsException;
import com.tannerbernth.web.error.PasswordsDoNotMatchException; 

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@Transactional
public class UserDetailService implements UserDetailsService {

	private final Logger LOGGER = LoggerFactory.getLogger(UserGroupService.class);

	@Autowired
	private UserRepository userRepository;

	@Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private UserGroupRepository userGroupRepository;

	@Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserDetailService() {
    	super();
    }

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		User user = userRepository.findByUsername(username);
		if (user == null) {
			throw new UsernameNotFoundException("No user found with username: "+ username);
		}

		return new org.springframework.security.core.userdetails.User(
			user.getUsername(),
			user.getPassword(), 
			true, // enabled 
			true, // account non expired
			true, // credentials non expired
			true, // account non locked
			getAuthorities(user.getRoles()));
	}

	private static Collection<GrantedAuthority> getAuthorities (Collection<Role> roles) {
		List<GrantedAuthority> authorities = new ArrayList<>();
		for (Role role : roles) {
		    authorities.add(new SimpleGrantedAuthority(role.getName()));
		}
		return authorities;
	}

	public boolean passwordMatches(String password, User user) {
		if (!bCryptPasswordEncoder.matches(password,user.getPassword())) {
			return false;
		} 
		return true;
	}

	public void updatePassword(User user, ChangePasswordDto changePasswordDto) throws PasswordsDoNotMatchException {
		/*if (!bCryptPasswordEncoder.matches(changePasswordDto.getCurrentPassword(),user.getPassword())) {
			throw new PasswordsDoNotMatchException("Current password was incorrect");
		}*/
		if (!passwordMatches(changePasswordDto.getCurrentPassword(), user)) {
			throw new PasswordsDoNotMatchException("Incorrect password");
		}
		user.setPassword(bCryptPasswordEncoder.encode(changePasswordDto.getPassword()));
	}

	public User registerNewUser(UserDto userDto) throws UsernameAlreadyExistsException, EmailAlreadyExistsException {
		userDto.setUsername(userDto.getUsername().trim());
		if (usernameExists(userDto.getUsername()) || userDto.getUsername().equals("")) {
			throw new UsernameAlreadyExistsException("User already exists with that username: " + userDto.getUsername());
		}
		if (emailExists(userDto.getEmail())) {
			throw new EmailAlreadyExistsException("User already exists with that email: " + userDto.getEmail());
		}
		
		User user = new User();
		user.setUsername(userDto.getUsername());
		user.setEmail(userDto.getEmail());
		user.setPassword(bCryptPasswordEncoder.encode(userDto.getPassword()));
		user.setRoles(Arrays.asList(roleRepository.findByName("USER")));
		user.setGroups(new ArrayList<Group>());
		user.setCommentCount(0L);
		user.setCreatedDate(LocalDateTime.now());
		return userRepository.save(user);
	}

	public boolean usernameExists(String username) {
		User user = userRepository.findByUsername(username);
		if (user != null) {
			return true;
		}
		return false;
	}

	public void updateLastLogin(String username) {
		User user = userRepository.findByUsername(username);
		if (user != null) {
			user.setLastLogin(LocalDateTime.now());
		}
	}

	public boolean emailExists(String email) {
		User user = userRepository.findByEmail(email);
		if (user != null) {
			return true;
		}
		return false;
	}

	public User findUserByUsername(String username) throws UsernameDoesNotExistException {
		User user = userRepository.findByUsername(username);
		if (user == null) {
			throw new UsernameDoesNotExistException("User does not exist");
		}
		return user;
	}

	public Collection<User> findTop10ByUsernameStartingWith(String username) {
		return userRepository.findTop10ByUsernameIgnoreCaseStartingWithOrderByUsernameAsc(username);
	}

	public Collection<User> findAllUsers() {
		return userRepository.findAllByOrderByUsernameAsc();
	}

	public void updateUserGroups(User user, Group group, LocalDateTime time) {
		if (group.getId() == null) {
			return;
		}
		UserGroup userGroup = new UserGroup();
		userGroup.setUser(user);
		userGroup.setGroup(group);
		userGroup.setLastReadDate(time);
		userGroup = userGroupRepository.save(userGroup);
	}

	public void resetUnreadCount(User user) {
		if (user != null) {
			user.setUnreadCount(0);
		}
	}

	public void incrementCommentCount(User user) {
		user.setCommentCount(user.getCommentCount()+1);
	}

}