package com.senla.NotificationService.repository.impl;

import com.senla.NotificationService.model.LocalUser;
import com.senla.NotificationService.repository.LocalUserRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class LocalUserRepositoryImpl extends AbstractGenericRepositoryImpl<LocalUser, UUID> implements LocalUserRepository {

    public LocalUserRepositoryImpl() {
        super(LocalUser.class);
    }
}
