package com.senla.NotificationService.service;

import com.senla.NotificationService.model.LocalUser;

public interface LocalUserService {

    void save(LocalUser localUser);

    void update(LocalUser localUser);

    LocalUser findByIdIfExists(Long id);
}
