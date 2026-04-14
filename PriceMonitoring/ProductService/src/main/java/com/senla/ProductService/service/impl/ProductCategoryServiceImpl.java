package com.senla.ProductService.service.impl;

import com.senla.ProductService.dto.ProductCategoryDTO;
import com.senla.ProductService.mapper.ProductCategoryMapper;
import com.senla.ProductService.model.ProductCategory;
import com.senla.ProductService.repository.ProductCategoryRepository;
import com.senla.ProductService.service.ProductCategoryService;
import com.senla.ProductService.util.YandexCloudUtil;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductCategoryServiceImpl implements ProductCategoryService {

    private final ProductCategoryRepository productCategoryRepository;
    private final ProductCategoryMapper productCategoryMapper;
    private final YandexCloudUtil yandexCloudUtil;
    private static final String FOLDER = "category_logo/";

    @Autowired
    public ProductCategoryServiceImpl(ProductCategoryRepository productCategoryRepository, ProductCategoryMapper productCategoryMapper, YandexCloudUtil yandexCloudUtil) {
        this.productCategoryRepository = productCategoryRepository;
        this.productCategoryMapper = productCategoryMapper;
        this.yandexCloudUtil = yandexCloudUtil;
    }

    @Override
    @Transactional
    public void save(ProductCategoryDTO productCategoryDTO, MultipartFile photo) {
        if (productCategoryRepository.findByName(productCategoryDTO.name()).isPresent()) {
            throw new EntityExistsException("Product category with name " + productCategoryDTO.name() + " already exists!");
        }
        ProductCategory productCategory = productCategoryMapper.productCategoryDTOToProductCategory(productCategoryDTO);
        productCategory.setImageUrl(yandexCloudUtil.saveImageToStorage(photo, FOLDER));
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
    public ProductCategory findByIdIfExists(Long id) {
        return productCategoryRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Product category with id - " + id + " not found!"));
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        ProductCategory productCategory = findByIdIfExists(id);
        productCategoryRepository.delete(productCategory);
    }
}
