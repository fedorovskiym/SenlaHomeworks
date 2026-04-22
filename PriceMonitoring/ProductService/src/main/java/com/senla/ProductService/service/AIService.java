package com.senla.ProductService.service;

import com.senla.ProductService.dto.product.ProductSearchRequest;

import java.util.List;

public interface AIService {

    ProductSearchRequest getProductSearchRequest(String searchQuery, List<String> brandNames, List<String> categoryNames);
}
