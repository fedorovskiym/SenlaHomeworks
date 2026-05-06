package com.senla.NotificationService.service.impl;

import com.senla.NotificationService.dto.PriceDTO;
import com.senla.NotificationService.model.LocalUser;
import com.senla.NotificationService.model.Notification;
import com.senla.NotificationService.repository.NotificationRepository;
import com.senla.NotificationService.service.LocalUserService;
import com.senla.NotificationService.service.NotificationService;
import com.senla.NotificationService.service.SmsSenderService;
import org.aspectj.weaver.ast.Not;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class NotificationServiceImpl implements NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);
    private final NotificationRepository notificationRepository;
    private final SmsSenderService smsSenderService;
    private final LocalUserService localUserService;

    @Autowired
    public NotificationServiceImpl(NotificationRepository notificationRepository, SmsSenderService smsSenderService,
                                   LocalUserService localUserService) {
        this.notificationRepository = notificationRepository;
        this.smsSenderService = smsSenderService;
        this.localUserService = localUserService;
    }

    @Override
    @Transactional
    public void save(Notification notification) {
        logger.info("Saving notification {}", notification);
        notificationRepository.save(notification);
        logger.info("Successfull saved notification {}", notification);
    }

    @Override
    @Transactional
    public void sendNotification(PriceDTO priceDTO) {
        LocalUser user = localUserService.findByIdOptional(priceDTO.userId());
        if(user == null){
            logger.info("User with id {} not found", priceDTO.userId());
            return;
        }

        String message = String.format("%s %s %s %s %d %s", "Скидка на товар\n", priceDTO.productName(),
                priceDTO.price(), "р.", priceDTO.discountPercent(), "%");
        logger.info("Sending notification {} to user with id {}", message, user.getId());
        Notification notification = buildNotification(message, user);
        save(notification);
        smsSenderService.sendSms(user.getPhoneNumber(), message);
    }

    private Notification buildNotification(String message, LocalUser user) {
        Notification notification = new Notification();
        notification.setMessage(message);
        notification.setUser(user);
        notification.setSendDateTime(LocalDateTime.now());
        return notification;
    }
}
