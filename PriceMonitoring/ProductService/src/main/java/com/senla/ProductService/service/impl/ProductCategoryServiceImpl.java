package com.senla.ProductService.service.impl;

import com.senla.ProductService.dto.productCategory.ProductCategoryDTO;
import com.senla.ProductService.dto.productCategory.ProductCategoryUpdateDTO;
import com.senla.ProductService.mapper.ProductCategoryMapper;
import com.senla.ProductService.model.ProductCategory;
import com.senla.ProductService.repository.ProductCategoryRepository;
import com.senla.ProductService.service.ProductCategoryService;
import com.senla.ProductService.util.YandexCloudUtil;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(ProductCategoryServiceImpl.class);

    @Autowired
    public ProductCategoryServiceImpl(ProductCategoryRepository productCategoryRepository, ProductCategoryMapper productCategoryMapper, YandexCloudUtil yandexCloudUtil) {
        this.productCategoryRepository = productCategoryRepository;
        this.productCategoryMapper = productCategoryMapper;
        this.yandexCloudUtil = yandexCloudUtil;
    }

    @Override
    @Transactional
    public void save(ProductCategoryDTO productCategoryDTO, MultipartFile photo) {
        logger.info("Saving product category from dto {}", productCategoryDTO);
        if (findByNameIfExists(productCategoryDTO.name()) != null) {
            logger.warn("Product category with name {} already exists", productCategoryDTO.name());
            throw new EntityExistsException("Product category with name " + productCategoryDTO.name() + " already exists!");
        }
        ProductCategory productCategory = productCategoryMapper.productCategoryDTOToProductCategory(productCategoryDTO);
        if (!photo.isEmpty()) {
            logger.info("Saving product category image to Yandex Cloud Storage");
            productCategory.setImageUrl(yandexCloudUtil.saveImageToStorage(photo, FOLDER));
            logger.info("Successfully save product category image to Yandex Cloud Storage");
        }
        productCategoryRepository.save(productCategory);
        logger.info("Successfully save product category {}", productCategoryDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductCategoryDTO> findAll() {
        logger.info("Finding all products categories");
        return productCategoryRepository.findAll()
                .stream().map(productCategoryMapper::productCategoryToProductCategoryDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ProductCategoryDTO findById(Long id) {
        logger.info("Finding product category dto by id {}", id);
        return productCategoryMapper.productCategoryToProductCategoryDTO(findByIdIfExists(id));
    }

    @Override
    @Transactional(readOnly = true)
    public ProductCategory findByIdIfExists(Long id) {
        logger.info("Finding product category by id if exists {}", id);
        return productCategoryRepository.findById(id).orElseThrow(() -> {
            logger.warn("Product category with id {} not found", id);
            return new EntityNotFoundException("Product category with id - " + id + " not found!");
        });
    }

    @Override
    @Transactional(readOnly = true)
    public ProductCategory findByNameIfExists(String name) {
        logger.info("Finding product category by name {} or null", name);
        return productCategoryRepository.findByName(name).orElse(null);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        logger.info("Deleting product category by id {}", id);
        ProductCategory productCategory = findByIdIfExists(id);
        if (productCategory.getImageUrl() != null) {
            logger.info("Deleting product category image from Yandex Cloud Storage");
            yandexCloudUtil.deleteImage(productCategory.getImageUrl());
            logger.warn("Successfull delete product category image from Yandex Cloud Storage");
        }
        productCategoryRepository.delete(productCategory);
        logger.info("Successfully delete product category {}", productCategory);
    }

    @Override
    @Transactional
    public void update(Long id, ProductCategoryUpdateDTO productCategoryUpdateDTO) {
        logger.info("Updating product category by id {}", id);
        ProductCategory productCategory = findByIdIfExists(id);

        if (productCategory.getName().equals(productCategoryUpdateDTO.name()) || findByNameIfExists(productCategoryUpdateDTO.name()) != null) {
            logger.warn("Product category with name {} already exists", productCategoryUpdateDTO.name());
            throw new EntityExistsException("Product category with name - " + productCategory.getName() + " already exists!");
        }

        productCategory = productCategoryMapper.updateProductCategoryFromDTO(productCategoryUpdateDTO, productCategory);
        productCategoryRepository.update(productCategory);
        logger.info("Successfully update product category {}", productCategory);
    }

    @Override
    @Transactional
    public void updateImage(Long id, MultipartFile photo) {
        logger.info("Updating product category image from Yandex Cloud Storage by id {}", id);
        ProductCategory productCategory = findByIdIfExists(id);

        if (productCategory.getImageUrl() != null) {
            logger.info("Delete old image from Yandex Cloud Storage");
            yandexCloudUtil.deleteImage(productCategory.getImageUrl());
            logger.warn("Successfully delete old image from Yandex Cloud Storage");
        }
        logger.info("Saving product category image to Yandex Cloud Storage");
        productCategory.setImageUrl(yandexCloudUtil.saveImageToStorage(photo, FOLDER));
        logger.info("Successfull save product category image to Yandex Cloud Storage");
        productCategoryRepository.update(productCategory);
        logger.info("Successfully update product category {}", productCategory);
    }

    @Override
    public Optional<ProductCategory> findByIdOptional(Long id) {
        logger.info("Finding product category by id {}", id);
        return productCategoryRepository.findById(id);
    }
}
