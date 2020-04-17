package com.tannerbernth.repository;

import java.util.Collection;
import com.tannerbernth.model.User;

import com.tannerbernth.model.Group;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepository extends JpaRepository<Group, Long> {
	Group findOneById(Long id);
	Group findByName(String name);
	Collection<Group> findByUsersIdOrderByLastModifiedDateDesc(Long id);

	@Override
	void delete(Group group);
}