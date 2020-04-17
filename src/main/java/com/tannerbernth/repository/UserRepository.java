package com.tannerbernth.repository;

import java.util.Collection;

import com.tannerbernth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
	User findByUsername(String username);
	User findByEmail(String email);
	Collection<User> findAllByOrderByUsernameAsc();
	Collection<User> findTop10ByUsernameIgnoreCaseStartingWithOrderByUsernameAsc(String username);

	@Override
	void delete(User user);
}