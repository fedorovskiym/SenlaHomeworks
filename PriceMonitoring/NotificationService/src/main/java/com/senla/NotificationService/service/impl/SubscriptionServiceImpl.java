package com.senla.NotificationService.service.impl;

import com.senla.NotificationService.dto.PriceDTO;
import com.senla.NotificationService.dto.SubscriptionDTO;
import com.senla.NotificationService.mapper.SubscriptionMapper;
import com.senla.NotificationService.model.LocalUser;
import com.senla.NotificationService.model.Subscription;
import com.senla.NotificationService.repository.SubscriptionRepository;
import com.senla.NotificationService.service.LocalUserService;
import com.senla.NotificationService.service.SubscriptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final LocalUserService localUserService;
    private final SubscriptionMapper subscriptionMapper;
    private final SmsSenderService smsSenderService;
    private static final Logger logger = LoggerFactory.getLogger(SubscriptionServiceImpl.class);

    @Autowired
    public SubscriptionServiceImpl(SubscriptionRepository subscriptionRepository, LocalUserService localUserService, SubscriptionMapper subscriptionMapper, SmsSenderService smsSenderService) {
        this.subscriptionRepository = subscriptionRepository;
        this.localUserService = localUserService;
        this.subscriptionMapper = subscriptionMapper;
        this.smsSenderService = smsSenderService;
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

    @Override
    @Transactional
    public void sendMessages(PriceDTO priceDTO) {
        List<Subscription> subscriptions = subscriptionRepository.findByProductPriceId(priceDTO.id());
        logger.info("Sending {} subscriptions", subscriptions.size());
        List<LocalUser> users = subscriptions.stream()
                .map(Subscription::getUser)
                .toList();

        String message = "Discount on product!";
        users.forEach(user -> {
            logger.info("Sending user {}", user);
            smsSenderService.sendSms(user.getPhoneNumber(), message);
        });
    }
}
