package com.tannerbernth.repository;

import java.util.Collection;

import com.tannerbernth.model.Article;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleRepository extends JpaRepository<Article, Long> {
	Collection<Article> findTop3ByIsPublicTrueOrderByCreatedDateDesc();
	Collection<Article> findTop10ByIsPublicTrueOrderByCreatedDateDesc();
	Collection<Article> findTop20ByIsPublicTrueOrderByCreatedDateDesc();
	Collection<Article> findAllByIsPublicFalseOrderByCreatedDateDesc();
	Article findOneById(Long id);

	@Override
	void delete(Article article);
}