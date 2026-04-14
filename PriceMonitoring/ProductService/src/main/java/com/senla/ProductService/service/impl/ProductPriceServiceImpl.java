package com.senla.ProductService.service.impl;

import com.senla.ProductService.dto.CreateProductPriceDTO;
import com.senla.ProductService.mapper.ProductPriceMapper;
import com.senla.ProductService.model.Product;
import com.senla.ProductService.model.ProductPrice;
import com.senla.ProductService.model.ShopBranch;
import com.senla.ProductService.repository.ProductPriceRepository;
import com.senla.ProductService.service.ProductPriceService;
import com.senla.ProductService.service.ProductService;
import com.senla.ProductService.service.ShopBranchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductPriceServiceImpl implements ProductPriceService {

    private final ProductPriceRepository productPriceRepository;
    private final ProductPriceMapper productPriceMapper;
    private final ProductService productService;
    private final ShopBranchService shopBranchService;

    @Autowired
    public ProductPriceServiceImpl(ProductPriceRepository productPriceRepository, ProductPriceMapper productPriceMapper, ProductService productService, ShopBranchService shopBranchService) {
        this.productPriceRepository = productPriceRepository;
        this.productPriceMapper = productPriceMapper;
        this.productService = productService;
        this.shopBranchService = shopBranchService;
    }

    @Override
    public void save(CreateProductPriceDTO createProductPriceDTO) {
        Product product = productService.findByIdIfExists(createProductPriceDTO.productId());
        ShopBranch shopBranch = shopBranchService.findByIdIfExists(createProductPriceDTO.shopBranchId());

        ProductPrice productPrice = productPriceMapper.createProductPriceDTOToProductPrice(createProductPriceDTO);
        productPrice.setProduct(product);
        productPrice.setShopBranch(shopBranch);
        productPriceRepository.save(productPrice);
    }
}
