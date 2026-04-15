package com.senla.ProductService.service.impl;

import com.senla.ProductService.dto.price.CreateProductPriceDTO;
import com.senla.ProductService.dto.price.ProductPriceDTO;
import com.senla.ProductService.mapper.ProductPriceMapper;
import com.senla.ProductService.model.Product;
import com.senla.ProductService.model.ProductPrice;
import com.senla.ProductService.model.ShopBranch;
import com.senla.ProductService.model.enums.PriceStatus;
import com.senla.ProductService.repository.ProductPriceRepository;
import com.senla.ProductService.service.ProductPriceService;
import com.senla.ProductService.service.ProductService;
import com.senla.ProductService.service.ShopBranchService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

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
    @Transactional
    public void save(CreateProductPriceDTO createProductPriceDTO) {
        Product product = productService.findByIdIfExists(createProductPriceDTO.productId());
        ShopBranch shopBranch = shopBranchService.findByIdIfExists(createProductPriceDTO.shopBranchId());

        ProductPrice productPrice = productPriceMapper.createProductPriceDTOToProductPrice(createProductPriceDTO);

        productPrice.setProduct(product);
        productPrice.setShopBranch(shopBranch);
        productPrice.setStartDate(LocalDate.now());
        productPrice.setStatus(PriceStatus.ACTUAL);

        productPriceRepository.save(productPrice);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductPriceDTO findById(Long id) {
        return productPriceMapper.productPriceToProductPriceDTO(productPriceRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Product's price with id - " + id + " not found!")));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductPriceDTO> findAllWithPagination(Integer page, Integer size) {
        return productPriceRepository.findAllWithPagination(page, size)
                .stream().map(productPriceMapper::productPriceToProductPriceDTO).collect(Collectors.toList());
    }
}
