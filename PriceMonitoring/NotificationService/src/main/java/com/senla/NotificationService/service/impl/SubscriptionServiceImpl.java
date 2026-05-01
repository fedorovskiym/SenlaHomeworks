package com.senla.NotificationService.service.impl;

import com.senla.NotificationService.dto.SubscriptionDTO;
import com.senla.NotificationService.mapper.SubscriptionMapper;
import com.senla.NotificationService.model.LocalUser;
import com.senla.NotificationService.model.Subscription;
import com.senla.NotificationService.repository.SubscriptionRepository;
import com.senla.NotificationService.service.LocalUserService;
import com.senla.NotificationService.service.SubscriptionService;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final LocalUserService localUserService;
    private final SubscriptionMapper subscriptionMapper;
    private static final Logger logger = LoggerFactory.getLogger(SubscriptionServiceImpl.class);

    @Autowired
    public SubscriptionServiceImpl(SubscriptionRepository subscriptionRepository, LocalUserService localUserService, SubscriptionMapper subscriptionMapper) {
        this.subscriptionRepository = subscriptionRepository;
        this.localUserService = localUserService;
        this.subscriptionMapper = subscriptionMapper;
    }

    @Override
    @Transactional
    public void saveSubscription(SubscriptionDTO subscriptionDTO) {
        logger.info("Saving subscription from dto {}", subscriptionDTO);
        LocalUser localUser = localUserService.findByIdOptional(subscriptionDTO.userId());
        if(localUser == null){
            logger.warn("User with id {} not found", subscriptionDTO.userId());
            return;
        }

        Subscription subscription = subscriptionMapper.subscriptionDTOToSubscription(subscriptionDTO);
        subscription.setUser(localUser);
        subscriptionRepository.save(subscription);
        logger.info("Subscription saved {}", subscription);
    }
}
