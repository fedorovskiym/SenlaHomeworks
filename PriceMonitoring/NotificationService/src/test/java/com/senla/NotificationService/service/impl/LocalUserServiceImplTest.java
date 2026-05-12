package com.senla.NotificationService.service.impl;

import com.senla.NotificationService.model.LocalUser;
import com.senla.NotificationService.repository.LocalUserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LocalUserServiceImplTest {

    @Mock
    private LocalUserRepository localUserRepository;

    @InjectMocks
    private LocalUserServiceImpl service;

    private LocalUser user;
    private UUID id;

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID();
        user = new LocalUser();
        user.setId(id);
        user.setPhoneNumber("79999999999");
    }

    @Test
    void saveShouldCallRepositorySave() {
        service.save(user);

        verify(localUserRepository, times(1)).save(user);
    }

    @Test
    void updateShouldUpdatePhoneNumber() {
        LocalUser existing = new LocalUser();
        existing.setId(id);
        existing.setPhoneNumber("71111111111");

        when(localUserRepository.findById(id)).thenReturn(Optional.of(existing));

        LocalUser updated = new LocalUser();
        updated.setId(id);
        updated.setPhoneNumber("72222222222");

        service.update(updated);

        assertEquals("72222222222", existing.getPhoneNumber());
        verify(localUserRepository, times(1)).update(existing);
    }

    @Test
    void updateShouldThrowEntityNotFoundException() {
        when(localUserRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.update(user));
    }

    @Test
    void findByIdIfExistsShouldReturnUser() {
        when(localUserRepository.findById(id)).thenReturn(Optional.of(user));

        LocalUser result = service.findByIdIfExists(id);

        assertEquals(user, result);
    }

    @Test
    void findByIdIfExistsShouldThrowException() {
        when(localUserRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.findByIdIfExists(id));
    }

    @Test
    void findByIdOptionalShouldReturnUser() {
        when(localUserRepository.findById(id)).thenReturn(Optional.of(user));

        LocalUser result = service.findByIdOptional(id);

        assertNotNull(result);
    }

    @Test
    void findByIdOptionalShouldReturnNull() {
        when(localUserRepository.findById(id)).thenReturn(Optional.empty());

        LocalUser result = service.findByIdOptional(id);

        assertNull(result);
    }

    @Test
    void deleteShouldCallRepositoryDelete() {
        when(localUserRepository.findById(id)).thenReturn(Optional.of(user));

        service.delete(id);

        verify(localUserRepository, times(1)).delete(user);
    }

    @Test
    void deleteShouldThrowEntityNotFoundException() {
        when(localUserRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.delete(id));
    }
}