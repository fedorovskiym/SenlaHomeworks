package com.senla.ProductService.dto;

public record ShopBranchUpdateDTO(
        String street,
        Integer house,
        Integer room
) {}