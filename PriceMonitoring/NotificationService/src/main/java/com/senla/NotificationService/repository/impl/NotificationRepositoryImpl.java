package com.senla.NotificationService.repository.impl;

import com.senla.NotificationService.model.Notification;
import com.senla.NotificationService.repository.NotificationRepository;
import org.springframework.stereotype.Repository;

@Repository
public class NotificationRepositoryImpl extends AbstractGenericRepositoryImpl<Notification, Long> implements NotificationRepository {

    public NotificationRepositoryImpl() {
        super(Notification.class);
    }
}
