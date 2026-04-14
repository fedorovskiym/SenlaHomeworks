package com.senla.ProductService.service.impl;

import com.senla.ProductService.dto.ProductDTO;
import com.senla.ProductService.mapper.ProductMapper;
import com.senla.ProductService.model.Brand;
import com.senla.ProductService.model.Product;
import com.senla.ProductService.model.ProductCategory;
import com.senla.ProductService.repository.ProductRepository;
import com.senla.ProductService.service.BrandService;
import com.senla.ProductService.service.ProductCategoryService;
import com.senla.ProductService.service.ProductService;
import com.senla.ProductService.util.YandexCloudUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final BrandService brandService;
    private final ProductCategoryService productCategoryService;
    private final YandexCloudUtil yandexCloudUtil;
    private static final String FOLDER = "product_images/";

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository, ProductMapper productMapper, BrandService brandService, ProductCategoryService productCategoryService, YandexCloudUtil yandexCloudUtil) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
        this.brandService = brandService;
        this.productCategoryService = productCategoryService;
        this.yandexCloudUtil = yandexCloudUtil;
    }

    @Override
    @Transactional
    public void save(ProductDTO productDTO, MultipartFile photo) {
        Brand brand = brandService.findByIdIfExists(productDTO.brandId());
        ProductCategory productCategory = productCategoryService.findByIdIfExists(productDTO.categoryId());

        Product product = productMapper.productDTOToProduct(productDTO);
        product.setBrand(brand);
        product.setProductCategory(productCategory);
        product.setImageUrl(yandexCloudUtil.saveImageToStorage(photo, FOLDER));
        productRepository.save(product);
    }

    @Override
    public List<ProductDTO> findByCategoryId(Long categoryId) {
        return List.of();
    }

    @Override
    public List<ProductDTO> findByBrandId(Long brandId) {
        return List.of();
    }

    @Override
    public ProductDTO findById(Long id) {
        return null;
    }

    @Override
    public Product findByIdIfExists(Long id) {
        return null;
    }
}
