package com.senla.NotificationService.service.impl;

import com.senla.NotificationService.model.Notification;
import com.senla.NotificationService.repository.NotificationRepository;
import com.senla.NotificationService.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationServiceImpl implements NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);
    private final NotificationRepository notificationRepository;

    @Autowired
    public NotificationServiceImpl(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    @Transactional
    public void save(Notification notification) {
        logger.info("Saving notification {}", notification);
        notificationRepository.save(notification);
        logger.info("Successfull saved notification {}", notification);
    }
}
