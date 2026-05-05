package com.senla.NotificationService.service;

import com.senla.NotificationService.model.LocalUser;

import java.util.Optional;
import java.util.UUID;

public interface LocalUserService {

    void save(LocalUser localUser);

    void update(LocalUser localUser);

    LocalUser findByIdIfExists(UUID id);

    LocalUser findByIdOptional(UUID id);
}
