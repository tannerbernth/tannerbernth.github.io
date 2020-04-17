package com.tannerbernth.repository;

import java.util.Collection;
import com.tannerbernth.model.User;
import com.tannerbernth.model.Group;
import com.tannerbernth.model.UserGroup;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserGroupRepository extends JpaRepository<UserGroup, Long> {

	UserGroup findOneByUserAndGroup(User user, Group group);

	@Override
	void delete(UserGroup userGroup);
}