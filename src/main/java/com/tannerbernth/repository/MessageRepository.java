package com.tannerbernth.repository;

import java.util.Collection;

import com.tannerbernth.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long>{
	Collection<Message> findByGroupIdOrderByCreatedDateAsc(Long groupId);
	Collection<Message> findBottom100ByGroupIdOrderByCreatedDateAsc(Long groupId);
	Message findTopByGroupIdOrderByCreatedDateDesc(Long groupId);

	@Override
	void delete(Message message);
}