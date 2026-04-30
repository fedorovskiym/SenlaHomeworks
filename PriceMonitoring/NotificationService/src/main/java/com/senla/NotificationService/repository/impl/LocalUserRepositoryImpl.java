package com.senla.NotificationService.repository.impl;

import com.senla.NotificationService.model.LocalUser;
import com.senla.NotificationService.repository.LocalUserRepository;
import org.springframework.stereotype.Repository;

@Repository
public class LocalUserRepositoryImpl extends AbstractGenericRepositoryImpl<LocalUser, Long> implements LocalUserRepository {

    public LocalUserRepositoryImpl() {
        super(LocalUser.class);
    }
}
