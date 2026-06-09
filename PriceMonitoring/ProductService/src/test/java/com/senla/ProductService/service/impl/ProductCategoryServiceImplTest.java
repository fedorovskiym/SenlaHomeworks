package com.senla.ProductService.service.impl;

import com.senla.ProductService.dto.productCategory.ProductCategoryDTO;
import com.senla.ProductService.dto.productCategory.ProductCategoryUpdateDTO;
import com.senla.ProductService.mapper.ProductCategoryMapper;
import com.senla.ProductService.model.ProductCategory;
import com.senla.ProductService.repository.ProductCategoryRepository;
import com.senla.ProductService.util.YandexCloudUtil;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductCategoryServiceImplTest {

    @Mock
    private ProductCategoryRepository productCategoryRepository;
    @Mock
    private ProductCategoryMapper productCategoryMapper;
    @Mock
    private YandexCloudUtil yandexCloudUtil;
    @Mock
    private MultipartFile multipartFile;

    @InjectMocks
    private ProductCategoryServiceImpl productCategoryService;

    private ProductCategory productCategory;
    private ProductCategoryDTO productCategoryDTO;
    private ProductCategoryUpdateDTO updateDTO;

    private final UUID categoryId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        productCategory = new ProductCategory();
        productCategory.setId(categoryId);
        productCategory.setName("category");
        productCategory.setDescription("description");
        productCategory.setImageUrl("imageUrl");

        productCategoryDTO = new ProductCategoryDTO(categoryId, productCategory.getName(),
                productCategory.getDescription(), productCategory.getImageUrl());

        updateDTO = new ProductCategoryUpdateDTO("updatedCategory", "updatedDescription");
    }

    @Test
    void saveShouldThrowEntityExistsException() {
        when(productCategoryRepository.findByName(productCategoryDTO.name())).thenReturn(Optional.of(productCategory));

        assertThrows(EntityExistsException.class, () -> productCategoryService.save(productCategoryDTO, multipartFile));

        verify(productCategoryRepository, never()).save(any(ProductCategory.class));
    }

    @Test
    void saveShouldSaveWithoutPhoto() {
        when(productCategoryRepository.findByName(productCategoryDTO.name())).thenReturn(Optional.empty());
        when(productCategoryMapper.productCategoryDTOToProductCategory(productCategoryDTO)).thenReturn(productCategory);
        when(productCategoryMapper.productCategoryToProductCategoryDTO(productCategory)).thenReturn(productCategoryDTO);
        when(multipartFile.isEmpty()).thenReturn(true);

        productCategoryService.save(productCategoryDTO, multipartFile);


        verify(productCategoryRepository).save(productCategory);
        verify(yandexCloudUtil, never()).saveImageToStorage(any(), anyString());
    }

    @Test
    void saveShouldSaveWithPhoto() {
        when(productCategoryRepository.findByName(productCategoryDTO.name())).thenReturn(Optional.empty());
        when(productCategoryMapper.productCategoryDTOToProductCategory(productCategoryDTO)).thenReturn(productCategory);
        when(productCategoryMapper.productCategoryToProductCategoryDTO(productCategory)).thenReturn(productCategoryDTO);
        when(multipartFile.isEmpty()).thenReturn(false);
        when(yandexCloudUtil.saveImageToStorage(multipartFile, "category_logo/")).thenReturn("imageUrl");

        productCategoryService.save(productCategoryDTO, multipartFile);

        verify(yandexCloudUtil).saveImageToStorage(multipartFile, "category_logo/");
        verify(productCategoryRepository).save(productCategory);
    }

    @Test
    void findAllShouldReturnList() {
        when(productCategoryRepository.findAll()).thenReturn(List.of(productCategory));
        when(productCategoryMapper.productCategoryToProductCategoryDTO(productCategory)).thenReturn(productCategoryDTO);

        List<ProductCategoryDTO> result = productCategoryService.findAll();

        assertEquals(1, result.size());
        assertEquals(productCategoryDTO, result.get(0));
    }

    @Test
    void findByIdShouldReturnDTO() {
        when(productCategoryRepository.findById(categoryId)).thenReturn(Optional.of(productCategory));
        when(productCategoryMapper.productCategoryToProductCategoryDTO(productCategory)).thenReturn(productCategoryDTO);

        ProductCategoryDTO result = productCategoryService.findById(categoryId);

        assertEquals(productCategoryDTO, result);
    }

    @Test
    void findByIdIfExistsShouldReturnCategory() {
        when(productCategoryRepository.findById(categoryId)).thenReturn(Optional.of(productCategory));

        ProductCategory result = productCategoryService.findByIdIfExists(categoryId);

        assertEquals(productCategory, result);
    }

    @Test
    void findByIdIfExistsShouldThrowEntityNotFoundException() {
        when(productCategoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> productCategoryService.findByIdIfExists(categoryId));
    }

    @Test
    void findByNameIfExistsShouldReturnCategory() {
        when(productCategoryRepository.findByName(productCategory.getName())).thenReturn(Optional.of(productCategory));

        ProductCategory result = productCategoryService.findByNameIfExists(productCategory.getName());

        assertEquals(productCategory, result);
    }

    @Test
    void findByNameIfExistsShouldReturnNull() {
        when(productCategoryRepository.findByName(productCategory.getName())).thenReturn(Optional.empty());

        ProductCategory result = productCategoryService.findByNameIfExists(productCategory.getName());

        assertNull(result);
    }

    @Test
    void deleteByIdShouldDeleteCategoryAndImage() {
        when(productCategoryRepository.findById(categoryId)).thenReturn(Optional.of(productCategory));
        doNothing().when(yandexCloudUtil).deleteImage(productCategory.getImageUrl());

        productCategoryService.deleteById(categoryId);

        verify(yandexCloudUtil).deleteImage(productCategory.getImageUrl());
        verify(productCategoryRepository).delete(productCategory);
    }

    @Test
    void deleteByIdShouldDeleteCategoryWithoutImage() {
        productCategory.setImageUrl(null);

        when(productCategoryRepository.findById(categoryId)).thenReturn(Optional.of(productCategory));

        productCategoryService.deleteById(categoryId);

        verify(yandexCloudUtil, never()).deleteImage(anyString());
        verify(productCategoryRepository).delete(productCategory);
    }

    @Test
    void updateShouldThrowEntityExistsException() {
        ProductCategory existing = new ProductCategory();
        existing.setName(updateDTO.name());

        when(productCategoryRepository.findById(categoryId)).thenReturn(Optional.of(productCategory));
        when(productCategoryRepository.findByName(updateDTO.name())).thenReturn(Optional.of(existing));

        assertThrows(EntityExistsException.class, () -> productCategoryService.update(categoryId, updateDTO));
    }

    @Test
    void updateShouldUpdateCategory() {
        when(productCategoryRepository.findById(categoryId)).thenReturn(Optional.of(productCategory));
        when(productCategoryRepository.findByName(updateDTO.name())).thenReturn(Optional.empty());
        when(productCategoryMapper.updateProductCategoryFromDTO(updateDTO, productCategory)).thenReturn(productCategory);
        when(productCategoryMapper.productCategoryToProductCategoryDTO(productCategory)).thenReturn(productCategoryDTO);

        productCategoryService.update(categoryId, updateDTO);

        verify(productCategoryRepository).update(productCategory);
        verify(productCategoryMapper).updateProductCategoryFromDTO(updateDTO, productCategory);
    }

    @Test
    void updateImageShouldUpdateImage() {
        when(productCategoryRepository.findById(categoryId)).thenReturn(Optional.of(productCategory));
        when(yandexCloudUtil.saveImageToStorage(multipartFile, "category_logo/")).thenReturn("imageUrl");
        when(productCategoryMapper.productCategoryToProductCategoryDTO(productCategory)).thenReturn(productCategoryDTO);

        ProductCategoryDTO result = productCategoryService.updateImage(categoryId, multipartFile);

        assertNotNull(result);
        assertEquals("imageUrl", productCategory.getImageUrl());

        verify(yandexCloudUtil).deleteImage("imageUrl");
        verify(yandexCloudUtil).saveImageToStorage(multipartFile, "category_logo/");
        verify(productCategoryRepository).update(productCategory);
    }

    @Test
    void findAllByIdShouldReturnMap() {
        Set<UUID> ids = Set.of(categoryId);
        Map<UUID, ProductCategory> categoryMap = Map.of(categoryId, productCategory);

        when(productCategoryRepository.findAllById(ids)).thenReturn(categoryMap);

        Map<UUID, ProductCategory> result = productCategoryService.findAllById(ids);

        assertEquals(categoryMap, result);
        verify(productCategoryRepository).findAllById(ids);
    }
}