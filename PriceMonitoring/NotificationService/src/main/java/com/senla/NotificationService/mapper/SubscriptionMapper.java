package com.senla.NotificationService.mapper;

import com.senla.NotificationService.dto.SubscriptionDTO;
import com.senla.NotificationService.model.Subscription;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class SubscriptionMapper {

    public abstract Subscription subscriptionDTOToSubscription(SubscriptionDTO subscriptionDTO);
}
