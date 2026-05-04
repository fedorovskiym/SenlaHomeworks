package com.senla.NotificationService.service;

import com.senla.NotificationService.dto.PriceDTO;
import com.senla.NotificationService.dto.SubscriptionDTO;

public interface SubscriptionService {

    /**
     * Добавь документацию к методам интерфейса
     * @param subscriptionDTO
     */
    void saveSubscription(SubscriptionDTO subscriptionDTO);

    /**
     *
     * @param priceDTO
     */
    void sendMessages(PriceDTO priceDTO);
}
