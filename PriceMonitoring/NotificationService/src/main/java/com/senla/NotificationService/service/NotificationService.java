package com.senla.NotificationService.service;

import com.senla.NotificationService.dto.PriceDTO;
import com.senla.NotificationService.model.Notification;

/**
 * inteface for work with notifications
 */
public interface NotificationService {

    /**
     * method for saving notification
     *
     * @param notification contains data for saving notification
     */
    void save(Notification notification);

    /**
     * method for send notification with sms
     *
     * @param priceDTO contains data to send sms
     */
    void sendNotification(PriceDTO priceDTO);
}
