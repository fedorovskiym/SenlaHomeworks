package com.senla.NotificationService.service;

import com.senla.NotificationService.dto.PriceDTO;
import com.senla.NotificationService.dto.SubscriptionDTO;

public interface SubscriptionService {

    void saveSubscription(SubscriptionDTO subscriptionDTO);

    void sendMessages(PriceDTO priceDTO);
}
