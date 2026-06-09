package com.senla.ProductService.service.impl;

import com.senla.ProductService.dto.ShopDTO;
import com.senla.ProductService.mapper.ShopMapper;
import com.senla.ProductService.model.Shop;
import com.senla.ProductService.repository.ShopRepository;
import com.senla.ProductService.util.YandexCloudUtil;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.apache.tomcat.util.http.InvalidParameterException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShopServiceImplTest {

    @Mock
    private ShopRepository shopRepository;
    @Mock
    private ShopMapper shopMapper;
    @Mock
    private YandexCloudUtil yandexCloudUtil;
    @Mock
    private MultipartFile multipartFile;

    @InjectMocks
    private ShopServiceImpl shopService;

    private Shop shop;
    private ShopDTO shopDTO;

    private final UUID shopId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        shop = new Shop();
        shop.setId(shopId);
        shop.setName("shop");
        shop.setLogoImageUrl("url");

        shopDTO = new ShopDTO(shopId, shop.getName(), shop.getLogoImageUrl());
    }

    @Test
    void saveShouldThrowEntityExistsException() {
        when(shopRepository.findByName(shopDTO.name())).thenReturn(Optional.ofNullable(shop));

        assertThrows(EntityExistsException.class, () -> shopService.save(shopDTO, multipartFile));

        verify(shopRepository, never()).save(any(Shop.class));
    }

    @Test
    void saveShouldCallRepositorySaveMethodWithoutImage() {
        when(shopRepository.findByName(shopDTO.name())).thenReturn(Optional.empty());
        when(shopMapper.shopDTOToShop(shopDTO)).thenReturn(shop);
        when(shopMapper.shopToShopDTO(shop)).thenReturn(shopDTO);
        when(multipartFile.isEmpty()).thenReturn(true);

        shopService.save(shopDTO, multipartFile);

        verify(shopRepository, times(1)).save(shop);
        verify(yandexCloudUtil, never()).saveImageToStorage(any(), anyString());
    }

    @Test
    void saveShouldCallRepositorySaveMethodWithImage() {
        String imageUrl = "imageUrl";

        when(shopRepository.findByName(shopDTO.name())).thenReturn(Optional.empty());
        when(shopMapper.shopDTOToShop(shopDTO)).thenReturn(shop);
        when(shopMapper.shopToShopDTO(shop)).thenReturn(shopDTO);
        when(multipartFile.isEmpty()).thenReturn(false);
        when(yandexCloudUtil.saveImageToStorage(multipartFile, "shops_logo/")).thenReturn(imageUrl);

        shopService.save(shopDTO, multipartFile);

        verify(yandexCloudUtil).saveImageToStorage(multipartFile, "shops_logo/");
        verify(shopRepository).save(shop);
    }

    @Test
    void findAllShouldReturnListOfShopDTO() {
        when(shopRepository.findAll()).thenReturn(List.of(shop));
        when(shopMapper.shopToShopDTO(shop)).thenReturn(shopDTO);

        List<ShopDTO> result = shopService.findAll();

        assertEquals(1, result.size());
        assertEquals(shopDTO, result.get(0));
    }

    @Test
    void deleteShouldDeleteExistingShop() {
        when(shopRepository.findById(shopId)).thenReturn(Optional.of(shop));
        doNothing().when(yandexCloudUtil).deleteImage(shop.getLogoImageUrl());
        doNothing().when(shopRepository).delete(shop);

        shopService.delete(shopId);

        verify(yandexCloudUtil).deleteImage(shop.getLogoImageUrl());
        verify(shopRepository).delete(shop);
    }

    @Test
    void findByIdShouldReturnShopDTO() {
        when(shopRepository.findById(shopId)).thenReturn(Optional.of(shop));
        when(shopMapper.shopToShopDTO(shop)).thenReturn(shopDTO);

        ShopDTO result = shopService.findById(shopId);

        assertEquals(shopDTO, result);
    }

    @Test
    void findByIdIfExistsShouldReturnShop() {
        when(shopRepository.findById(shopId)).thenReturn(Optional.of(shop));

        Shop result = shopService.findByIdIfExists(shopId);

        assertEquals(shop, result);
    }

    @Test
    void findByIdIfExistsShouldThrowEntityNotFoundException() {
        when(shopRepository.findById(shopId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> shopService.findByIdIfExists(shopId));
    }

    @Test
    void updateShouldThrowInvalidParameterException() {
        assertThrows(InvalidParameterException.class, () -> shopService.update(shopId, ""));
    }

    @Test
    void updateShouldThrowEntityExistsException() {
        when(shopRepository.findByName(shop.getName())).thenReturn(Optional.ofNullable(shop));

        assertThrows(EntityExistsException.class, () -> shopService.update(shopId, shop.getName()));
    }

    @Test
    void updateShouldSuccessfullyUpdateShopName() {
        when(shopRepository.findById(shopId)).thenReturn(Optional.of(shop));
        when(shopMapper.shopToShopDTO(shop)).thenReturn(shopDTO);

        ShopDTO result = shopService.update(shopId, "newName");

        assertNotNull(result);
        assertEquals("newName", shop.getName());
        verify(shopRepository).update(shop);
    }

    @Test
    void updateLogoShouldUpdateLogoImageSuccessfully() {
        when(shopRepository.findById(shopId)).thenReturn(Optional.of(shop));
        when(yandexCloudUtil.saveImageToStorage(multipartFile, "shops_logo/")).thenReturn("imageUrl");
        when(shopMapper.shopToShopDTO(shop)).thenReturn(shopDTO);

        ShopDTO result = shopService.updateLogo(shopId, multipartFile);

        assertNotNull(result);
        verify(yandexCloudUtil).deleteImage(anyString());
        verify(yandexCloudUtil).saveImageToStorage(multipartFile, "shops_logo/");
        verify(shopRepository).update(shop);
    }
}
