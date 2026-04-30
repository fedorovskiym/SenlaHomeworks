package com.senla.NotificationService.service.impl;

import com.senla.NotificationService.model.LocalUser;
import com.senla.NotificationService.repository.LocalUserRepository;
import com.senla.NotificationService.service.LocalUserService;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LocalUserServiceImpl implements LocalUserService {

    private final LocalUserRepository localUserRepository;
    private static final Logger logger = LoggerFactory.getLogger(LocalUserServiceImpl.class);

    @Autowired
    public LocalUserServiceImpl(LocalUserRepository localUserRepository) {
        this.localUserRepository = localUserRepository;
    }

    @Override
    @Transactional
    public void save(LocalUser localUser) {
        logger.info("Saving local user {}", localUser);
        localUserRepository.save(localUser);
        logger.info("Succesfull save local user {}", localUser);
    }

    @Override
    @Transactional
    public void update(LocalUser localUser) {
        logger.info("Updating local user with id {}", localUser.getId());
        LocalUser localUserToUpdate = findByIdIfExists(localUser.getId());
        localUserToUpdate.setPhoneNumber(localUser.getPhoneNumber());
        localUserRepository.update(localUserToUpdate);
        logger.info("Succesfull update local user with id {}", localUser.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public LocalUser findByIdIfExists(Long id) {
        return localUserRepository.findById(id).orElseThrow(() -> {
            logger.info("Local user with id {} not found", id);
            return new EntityNotFoundException("Local user with id " + id + " not found!");
        });
    }
}
