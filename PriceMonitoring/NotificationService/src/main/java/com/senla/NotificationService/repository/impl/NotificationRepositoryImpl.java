package com.senla.NotificationService.repository.impl;

import com.senla.NotificationService.model.Notification;
import com.senla.NotificationService.repository.NotificationRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class NotificationRepositoryImpl extends AbstractGenericRepositoryImpl<Notification, UUID> implements NotificationRepository {

    public NotificationRepositoryImpl() {
        super(Notification.class);
    }
}
