package com.senla.ProductService.service.impl;

import com.senla.ProductService.dto.brand.BrandDTO;
import com.senla.ProductService.dto.brand.BrandUpdateDTO;
import com.senla.ProductService.mapper.BrandMapper;
import com.senla.ProductService.model.Brand;
import com.senla.ProductService.repository.BrandRepository;
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
class BrandServiceImplTest {

    @Mock
    private BrandRepository brandRepository;
    @Mock
    private BrandMapper brandMapper;
    @Mock
    private YandexCloudUtil yandexCloudUtil;

    @InjectMocks
    private BrandServiceImpl brandService;

    private Brand brand;
    private BrandDTO brandDTO;
    private BrandUpdateDTO updateDTO;

    private final UUID brandId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        brand = new Brand();
        brand.setId(brandId);
        brand.setName("brand");
        brand.setCountry("country");
        brand.setLogoImageUrl("imageUrl");

        brandDTO = new BrandDTO(brandId, brand.getName(), brand.getCountry(), brand.getLogoImageUrl());

        updateDTO = new BrandUpdateDTO("updatedName", "updatedCountry");
    }

    @Test
    void saveShouldThrowEntityExistsException() {
        MultipartFile file = mock(MultipartFile.class);

        when(brandRepository.findByName(brandDTO.name())).thenReturn(Optional.of(brand));

        assertThrows(EntityExistsException.class, () -> brandService.save(brandDTO, file));
    }

    @Test
    void saveShouldSaveWithoutPhoto() {
        MultipartFile file = mock(MultipartFile.class);

        when(brandRepository.findByName(brandDTO.name())).thenReturn(Optional.empty());
        when(brandMapper.brandDTOToBrand(brandDTO)).thenReturn(brand);
        when(brandMapper.brandToBrandDTO(brand)).thenReturn(brandDTO);
        when(file.isEmpty()).thenReturn(true);

        brandService.save(brandDTO, file);

        verify(brandRepository).save(brand);
        verify(yandexCloudUtil, never()).saveImageToStorage(any(), anyString());
    }

    @Test
    void saveShouldSaveWithPhoto() {
        MultipartFile file = mock(MultipartFile.class);

        when(brandRepository.findByName(brandDTO.name())).thenReturn(Optional.empty());
        when(brandMapper.brandDTOToBrand(brandDTO)).thenReturn(brand);
        when(brandMapper.brandToBrandDTO(brand)).thenReturn(brandDTO);
        when(file.isEmpty()).thenReturn(false);
        when(yandexCloudUtil.saveImageToStorage(file, "brands_logo/")).thenReturn("imageUrl");

        brandService.save(brandDTO, file);

        verify(yandexCloudUtil).saveImageToStorage(file, "brands_logo/");
        verify(brandRepository).save(brand);
    }

    @Test
    void findAllShouldReturnList() {
        when(brandRepository.findAll()).thenReturn(List.of(brand));
        when(brandMapper.brandToBrandDTO(brand)).thenReturn(brandDTO);

        List<BrandDTO> result = brandService.findAll();

        assertEquals(1, result.size());
        assertEquals(brandDTO, result.get(0));

        verify(brandRepository).findAll();
    }

    @Test
    void findByIdShouldReturnDTO() {
        when(brandRepository.findById(brandId)).thenReturn(Optional.of(brand));
        when(brandMapper.brandToBrandDTO(brand)).thenReturn(brandDTO);

        BrandDTO result = brandService.findById(brandId);

        assertEquals(brandDTO, result);
    }

    @Test
    void findByIdIfExistsShouldReturnEntity() {
        when(brandRepository.findById(brandId)).thenReturn(Optional.of(brand));

        Brand result = brandService.findByIdIfExists(brandId);

        assertEquals(brand, result);
    }

    @Test
    void findByIdIfExistsShouldThrowException() {
        when(brandRepository.findById(brandId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> brandService.findByIdIfExists(brandId));
    }

    @Test
    void findByNameIfExistsShouldReturnEntity() {
        when(brandRepository.findByName(brand.getName())).thenReturn(Optional.of(brand));

        Brand result = brandService.findByNameIfExists(brand.getName());

        assertEquals(brand, result);
    }

    @Test
    void findByNameIfExistsShouldReturnNull() {
        when(brandRepository.findByName(brand.getName())).thenReturn(Optional.empty());

        Brand result = brandService.findByNameIfExists(brand.getName());

        assertNull(result);
    }

    @Test
    void deleteShouldDeleteWithImage() {
        when(brandRepository.findById(brandId)).thenReturn(Optional.of(brand));
        doNothing().when(yandexCloudUtil).deleteImage("imageUrl");

        brandService.delete(brandId);

        verify(yandexCloudUtil).deleteImage("imageUrl");
        verify(brandRepository).delete(brand);
    }

    @Test
    void deleteShouldDeleteWithoutImage() {
        brand.setLogoImageUrl(null);

        when(brandRepository.findById(brandId)).thenReturn(Optional.of(brand));

        brandService.delete(brandId);

        verify(yandexCloudUtil, never()).deleteImage(anyString());
        verify(brandRepository).delete(brand);
    }

    @Test
    void updateShouldThrowEntityExistsException() {
        Brand existing = new Brand();
        existing.setName(updateDTO.name());

        when(brandRepository.findById(brandId)).thenReturn(Optional.of(brand));
        when(brandRepository.findByName(updateDTO.name())).thenReturn(Optional.of(existing));

        assertThrows(EntityExistsException.class, () -> brandService.update(brandId, updateDTO));
    }

    @Test
    void updateShouldUpdateBrand() {
        when(brandRepository.findById(brandId)).thenReturn(Optional.of(brand));
        when(brandRepository.findByName(updateDTO.name())).thenReturn(Optional.empty());
        when(brandMapper.updateBrandFromBrandUpdateDTO(updateDTO, brand)).thenReturn(brand);
        when(brandMapper.brandToBrandDTO(brand)).thenReturn(brandDTO);

        brandService.update(brandId, updateDTO);

        verify(brandRepository).update(brand);
        verify(brandMapper).updateBrandFromBrandUpdateDTO(updateDTO, brand);
    }

    @Test
    void updateLogoShouldUpdateImage() {
        MultipartFile file = mock(MultipartFile.class);

        when(brandRepository.findById(brandId)).thenReturn(Optional.of(brand));
        when(yandexCloudUtil.saveImageToStorage(file, "brands_logo/")).thenReturn("imageUrl");
        when(brandMapper.brandToBrandDTO(brand)).thenReturn(brandDTO);

        BrandDTO result = brandService.updateLogo(brandId, file);

        assertNotNull(result);
        assertEquals("imageUrl", brand.getLogoImageUrl());

        verify(yandexCloudUtil).deleteImage("imageUrl");
        verify(yandexCloudUtil).saveImageToStorage(file, "brands_logo/");
        verify(brandRepository).update(brand);
    }

    @Test
    void findAllByIdShouldReturnMap() {
        Set<UUID> ids = Set.of(brandId);

        when(brandRepository.findAllById(ids)).thenReturn(Map.of(brandId, brand));

        Map<UUID, Brand> result = brandService.findAllById(ids);

        assertEquals(1, result.size());
        assertTrue(result.containsKey(brandId));
    }
}