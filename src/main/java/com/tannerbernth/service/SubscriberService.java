package com.tannerbernth.service;

import java.util.Collection;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import com.tannerbernth.model.Subscriber;
import com.tannerbernth.repository.SubscriberRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class SubscriberService {

    private final Logger LOGGER = LoggerFactory.getLogger(SubscriberService.class);

    @Autowired
    private SubscriberRepository subscriberRepository;

    public Collection<Subscriber> findAllSubscribersOrderAsc() {
        return subscriberRepository.findAllByOrderByEmailAsc();
    }

    public Subscriber addNewSubscriber(String email) {
        Subscriber subscriber = new Subscriber();
        subscriber.setEmail(email);
        return subscriberRepository.save(subscriber);
    }

}