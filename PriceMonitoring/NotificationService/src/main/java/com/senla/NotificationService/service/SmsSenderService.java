package com.senla.NotificationService.service;

public interface SmsSenderService {

    void sendSms(String phoneNumber, String message);
}
