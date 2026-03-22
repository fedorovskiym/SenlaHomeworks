package com.senla.consumer.dto;

public record TransferDTO(Long id, Long fromAccount, Long targetAccount, Integer amount) {
}
