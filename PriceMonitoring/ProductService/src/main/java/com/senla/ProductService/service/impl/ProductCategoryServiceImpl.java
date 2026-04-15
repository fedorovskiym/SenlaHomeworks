package com.senla.ProductService.service.impl;

import com.senla.ProductService.dto.productCategory.ProductCategoryDTO;
import com.senla.ProductService.dto.productCategory.ProductCategoryUpdateDTO;
import com.senla.ProductService.mapper.ProductCategoryMapper;
import com.senla.ProductService.model.Brand;
import com.senla.ProductService.model.ProductCategory;
import com.senla.ProductService.repository.ProductCategoryRepository;
import com.senla.ProductService.service.ProductCategoryService;
import com.senla.ProductService.util.YandexCloudUtil;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductCategoryServiceImpl implements ProductCategoryService {

    private final ProductCategoryRepository productCategoryRepository;
    private final ProductCategoryMapper productCategoryMapper;
    private final YandexCloudUtil yandexCloudUtil;
    private static final String FOLDER = "category_logo/";
    private final ResourcePatternResolver resourcePatternResolver;

    @Autowired
    public ProductCategoryServiceImpl(ProductCategoryRepository productCategoryRepository, ProductCategoryMapper productCategoryMapper, YandexCloudUtil yandexCloudUtil, ResourcePatternResolver resourcePatternResolver) {
        this.productCategoryRepository = productCategoryRepository;
        this.productCategoryMapper = productCategoryMapper;
        this.yandexCloudUtil = yandexCloudUtil;
        this.resourcePatternResolver = resourcePatternResolver;
    }

    @Override
    @Transactional
    public void save(ProductCategoryDTO productCategoryDTO, MultipartFile photo) {
        if (findByNameIfExists(productCategoryDTO.name()) != null) {
            throw new EntityExistsException("Product category with name " + productCategoryDTO.name() + " already exists!");
        }
        ProductCategory productCategory = productCategoryMapper.productCategoryDTOToProductCategory(productCategoryDTO);
        if (!photo.isEmpty()) {
            productCategory.setImageUrl(yandexCloudUtil.saveImageToStorage(photo, FOLDER));
        }
        productCategoryRepository.save(productCategory);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductCategoryDTO> findAll() {
        return productCategoryRepository.findAll()
                .stream().map(productCategoryMapper::productCategoryToProductCategoryDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ProductCategoryDTO findById(Long id) {
        return productCategoryMapper.productCategoryToProductCategoryDTO(findByIdIfExists(id));
    }

    @Override
    @Transactional(readOnly = true)
    public ProductCategory findByIdIfExists(Long id) {
        return productCategoryRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Product category with id - " + id + " not found!"));
    }

    @Override
    @Transactional(readOnly = true)
    public ProductCategory findByNameIfExists(String name) {
        return productCategoryRepository.findByName(name).orElse(null);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        ProductCategory productCategory = findByIdIfExists(id);
        if(productCategory.getImageUrl() != null) {
            yandexCloudUtil.deleteImage(productCategory.getImageUrl());
        }
        productCategoryRepository.delete(productCategory);
    }

    @Override
    @Transactional
    public void update(Long id, ProductCategoryUpdateDTO productCategoryUpdateDTO) {
        ProductCategory productCategory = findByIdIfExists(id);

        if(productCategory.getName().equals(productCategoryUpdateDTO.name()) || findByNameIfExists(productCategoryUpdateDTO.name()) != null) {
            throw new EntityExistsException("Product category with name - " + productCategory.getName() + " already exists!");
        }

        productCategory = productCategoryMapper.updateProductCategoryFromDTO(productCategoryUpdateDTO, productCategory);
        productCategoryRepository.update(productCategory);
    }

    @Override
    @Transactional
    public void updateImage(Long id, MultipartFile photo) {
        ProductCategory productCategory = findByIdIfExists(id);

        if(productCategory.getImageUrl() != null) {
            yandexCloudUtil.deleteImage(productCategory.getImageUrl());
        }

        productCategory.setImageUrl(yandexCloudUtil.saveImageToStorage(photo, FOLDER));
        productCategoryRepository.update(productCategory);
    }
}
