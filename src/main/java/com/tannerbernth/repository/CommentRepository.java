package com.tannerbernth.repository;

import java.util.Collection;

import com.tannerbernth.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
	Collection<Comment> findAllByArticleIdOrderByCreatedDateAsc(Long id);
	Comment findOneById(Long id);

	@Override
	void delete(Comment comment);
}