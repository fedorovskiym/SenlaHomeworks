package com.senla.task1.dto;

import java.time.Duration;
import java.time.LocalDateTime;

public record OrderDTO(Integer id, String carName, String mechanicName, String mechanicSurname, Integer placeNumber,
                       String status, LocalDateTime submissionDateTime, LocalDateTime plannedCompletionDateTime,
                       LocalDateTime completionDateTime, LocalDateTime endDateTime, Duration duration, Double price) {
}
