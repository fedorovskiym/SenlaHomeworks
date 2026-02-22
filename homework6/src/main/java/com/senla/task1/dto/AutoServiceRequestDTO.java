package com.senla.task1.dto;

public record AutoServiceRequestDTO(String carModel, Integer mechanicId, Integer placeNumber,
                                    Double price, Integer hours, Integer minutes) {
}
