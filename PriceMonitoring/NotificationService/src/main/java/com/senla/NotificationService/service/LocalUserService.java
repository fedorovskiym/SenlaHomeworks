package com.senla.NotificationService.service;

import com.senla.NotificationService.model.LocalUser;
import jakarta.persistence.EntityNotFoundException;

import java.util.Optional;
import java.util.UUID;

/**
 * interface for work with local users
 */
public interface LocalUserService {

    /**
     * method for saving local user
     *
     * @param localUser contains data for saving local user
     */
    void save(LocalUser localUser);

    /**
     * method for updating local user
     *
     * @param localUser contains data for update local user
     */
    void update(LocalUser localUser);

    /**
     * method for finding local user by id
     *
     * @param id local user id
     * @return local user with id from request
     * @throws EntityNotFoundException if local user with id from request not found
     */
    LocalUser findByIdIfExists(UUID id);

    /**
     * method for find local user or null
     *
     * @param id local user id
     * @return local user with id from request
     */
    LocalUser findByIdOptional(UUID id);

    /**
     * method for deleting local user by id
     *
     * @param id local user id to delete
     */
    void delete(UUID id);
}
