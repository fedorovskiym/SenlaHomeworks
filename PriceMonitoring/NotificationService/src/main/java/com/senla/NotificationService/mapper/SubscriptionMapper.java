package com.senla.NotificationService.mapper;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class SubscriptionMapper {

    public abstract Subscription subscriptionDTOToSubscription(SubscriptionDTO subscriptionDTO);
}
