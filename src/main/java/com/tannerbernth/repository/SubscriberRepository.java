package com.tannerbernth.repository;

import java.util.Collection;

import com.tannerbernth.model.Subscriber;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriberRepository extends JpaRepository<Subscriber, Long> {
	Collection<Subscriber> findAllByOrderByEmailAsc();

	@Override
	void delete(Subscriber subscriber);
}