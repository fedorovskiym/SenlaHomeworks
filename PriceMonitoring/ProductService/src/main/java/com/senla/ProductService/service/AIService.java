package com.senla.ProductService.service;

import com.senla.ProductService.dto.product.ProductSearchRequest;

import java.util.List;

/**
 * interface for integration with a external ai service
 */
public interface AIService {

    /**
     * method for creating request to db from user search request
     * @param searchQuery contains user's query
     * @param brandNames contains list of existing brands
     * @param categoryNames contains list of existing product categories
     * @return adapted database query
     */
    ProductSearchRequest getProductSearchRequest(String searchQuery, List<String> brandNames,
                                                 List<String> categoryNames);
}
