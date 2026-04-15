package com.senla.ProductService.service.impl;

import com.senla.ProductService.dto.product.ProductDTO;
import com.senla.ProductService.dto.product.ProductUpdateDTO;
import com.senla.ProductService.mapper.ProductMapper;
import com.senla.ProductService.model.Brand;
import com.senla.ProductService.model.Product;
import com.senla.ProductService.model.ProductCategory;
import com.senla.ProductService.repository.ProductRepository;
import com.senla.ProductService.service.BrandService;
import com.senla.ProductService.service.ProductCategoryService;
import com.senla.ProductService.service.ProductService;
import com.senla.ProductService.util.YandexCloudUtil;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

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
        if(!photo.isEmpty()) {
            product.setImageUrl(yandexCloudUtil.saveImageToStorage(photo, FOLDER));
        }
        productRepository.save(product);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> findAll() {
        return productRepository.findAll().stream().map(productMapper::productToProductDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> findByCategoryId(Long categoryId) {
        return List.of();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> findByBrandId(Long brandId) {
        return List.of();
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDTO findById(Long id) {
        return productMapper.productToProductDTO(findByIdIfExists(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Product findByIdIfExists(Long id) {
        return productRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Product with id - " + id + " not found!"));
    }

    @Override
    @Transactional(readOnly = true)
    public void delete(Long id) {
        Product product = findByIdIfExists(id);
        if(product.getImageUrl() != null) {
            yandexCloudUtil.deleteImage(product.getImageUrl());
        }
        productRepository.delete(product);
    }

    @Override
    @Transactional
    public void update(Long id, ProductUpdateDTO productUpdateDTO) {
        Product product = findByIdIfExists(id);

        if(productUpdateDTO.brandId() != null) {
            product.setBrand(brandService.findByIdIfExists(productUpdateDTO.brandId()));
        }
        if(productUpdateDTO.categoryId() != null) {
            product.setProductCategory(productCategoryService.findByIdIfExists(productUpdateDTO.categoryId()));
        }

        product = productMapper.updateProductFromDTO(productUpdateDTO, product);
        productRepository.update(product);
    }

    @Override
    @Transactional
    public void updateImage(Long id, MultipartFile photo) {
        Product product = findByIdIfExists(id);

        if(product.getImageUrl() != null) {
            yandexCloudUtil.deleteImage(product.getImageUrl());
        }

        product.setImageUrl(yandexCloudUtil.saveImageToStorage(photo, FOLDER));
        productRepository.update(product);
    }


}
