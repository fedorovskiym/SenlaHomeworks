package com.senla.NotificationService.service;

import com.senla.NotificationService.dto.PriceDTO;
import com.senla.NotificationService.model.Notification;

public interface NotificationService {

    void save(Notification notification);

    void sendNotification(PriceDTO priceDTO);
}
