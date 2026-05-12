package com.senla.NotificationService.service.impl;

import com.senla.NotificationService.dto.PriceDTO;
import com.senla.NotificationService.model.LocalUser;
import com.senla.NotificationService.model.Notification;
import com.senla.NotificationService.repository.NotificationRepository;
import com.senla.NotificationService.service.LocalUserService;
import com.senla.NotificationService.util.SmsSenderUtil;
import org.apache.kafka.common.quota.ClientQuotaAlteration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock
    private NotificationRepository notificationRepository;
    @Mock
    private SmsSenderUtil smsSenderUtil;
    @Mock
    private LocalUserService localUserService;

    @InjectMocks
    private NotificationServiceImpl notificationServiceImpl;

    private LocalUser user;

    @BeforeEach
    void setUp() {
        user = new LocalUser();
        user.setId(UUID.randomUUID());
        user.setPhoneNumber("79999999999");
    }

    @Test
    void saveShouldCallRepositorySave() {
        Notification notification = new Notification();
        notification.setMessage("Test");
        notification.setId(UUID.randomUUID());
        notification.setSendDateTime(LocalDateTime.now());
        notification.setUser(user);

        notificationServiceImpl.save(notification);

        verify(notificationRepository, times(1)).save(notification);
    }

    @Test
    void sendNotificationShouldCallSmsSenderUtil() {
        PriceDTO priceDTO = new PriceDTO(UUID.randomUUID(), "product",
                100.0, 10, user.getId());

        when(localUserService.findByIdOptional(user.getId())).thenReturn(user);

        notificationServiceImpl.sendNotification(priceDTO);

        verify(notificationRepository, times(1)).save(any(Notification.class));
        verify(smsSenderUtil, times(1)).sendSms(user.getPhoneNumber(), any(String.class));
    }

    @Test
    void sendNotificationShouldReturn() {
        PriceDTO priceDTO = new PriceDTO(UUID.randomUUID(), "product",
                100.0, 10, user.getId());

        when(localUserService.findByIdOptional(user.getId())).thenReturn(null);

        notificationServiceImpl.sendNotification(priceDTO);

        verifyNoInteractions(smsSenderUtil);
        verifyNoInteractions(notificationRepository);
    }
}