package com.senla.UserService.dto;

import java.util.UUID;

public record KafkaMessageWithUser(UUID id, String phoneNumber) {
}
