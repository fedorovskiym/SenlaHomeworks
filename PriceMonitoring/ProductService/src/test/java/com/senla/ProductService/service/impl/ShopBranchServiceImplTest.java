package com.senla.ProductService.service.impl;

import com.senla.ProductService.dto.ShopBranchDTO;
import com.senla.ProductService.dto.ShopBranchUpdateDTO;
import com.senla.ProductService.mapper.ShopBranchMapper;
import com.senla.ProductService.model.City;
import com.senla.ProductService.model.Shop;
import com.senla.ProductService.model.ShopBranch;
import com.senla.ProductService.repository.ShopBranchRepository;
import com.senla.ProductService.service.CityService;
import com.senla.ProductService.service.ShopService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShopBranchServiceImplTest {

    @Mock
    private ShopBranchRepository shopBranchRepository;
    @Mock
    private ShopBranchMapper shopBranchMapper;
    @Mock
    private CityService cityService;
    @Mock
    private ShopService shopService;

    @InjectMocks
    private ShopBranchServiceImpl shopBranchService;

    private ShopBranch shopBranch;
    private ShopBranchDTO shopBranchDTO;
    private Shop shop;
    private City city;

    private final UUID branchId = UUID.randomUUID();
    private final UUID shopId = UUID.randomUUID();
    private final UUID cityId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        shop = new Shop();
        shop.setId(shopId);
        shop.setName("shopName");

        city = new City();
        city.setId(cityId);
        city.setName("city");

        shopBranch = new ShopBranch();
        shopBranch.setId(branchId);
        shopBranch.setStreet("street");
        shopBranch.setHouse(1);
        shopBranch.setRoom(1);
        shopBranch.setShop(shop);
        shopBranch.setCity(city);

        shopBranchDTO = new ShopBranchDTO(branchId, shopId, cityId, shop.getName(), city.getName(),
                shopBranch.getStreet(), shopBranch.getHouse(), shopBranch.getRoom(), "logoImageUrl");
    }

    @Test
    void saveShouldCallRepositorySaveMethod() {
        when(cityService.getCityByIdIfExists(cityId)).thenReturn(city);
        when(shopService.findByIdIfExists(shopId)).thenReturn(shop);
        when(shopBranchMapper.shopBranchDTOToShopBranch(shopBranchDTO)).thenReturn(shopBranch);
        when(shopBranchMapper.shopBranchToShopBranchDTO(shopBranch)).thenReturn(shopBranchDTO);

        ShopBranchDTO result = shopBranchService.save(shopBranchDTO);

        assertNotNull(result);
        assertEquals(shopBranchDTO, result);


        verify(shopBranchRepository, times(1)).save(shopBranch);
    }

    @Test
    void findAllByShopIdShouldReturnListOfShopBranchDTO() {
        when(shopBranchRepository.findAllByShopIdFetch(shopId)).thenReturn(List.of(shopBranch));
        when(shopBranchMapper.shopBranchToShopBranchDTO(shopBranch)).thenReturn(shopBranchDTO);

        List<ShopBranchDTO> result = shopBranchService.findAllByShopId(shopId);

        assertEquals(1, result.size());
        assertEquals(shopBranchDTO, result.get(0));
    }

    @Test
    void findByIdShouldReturnShopBranchDTO() {
        when(shopBranchRepository.findById(branchId)).thenReturn(Optional.of(shopBranch));
        when(shopBranchMapper.shopBranchToShopBranchDTO(shopBranch)).thenReturn(shopBranchDTO);

        ShopBranchDTO result = shopBranchService.findById(branchId);

        assertEquals(shopBranchDTO, result);
    }

    @Test
    void findByIdIfExistsShouldReturnShopBranch() {
        when(shopBranchRepository.findById(branchId)).thenReturn(Optional.of(shopBranch));

        ShopBranch result = shopBranchService.findByIdIfExists(branchId);

        assertEquals(shopBranch, result);
    }

    @Test
    void findByIdIfExistsShouldThrowEntityNotFoundException() {
        when(shopBranchRepository.findById(branchId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> shopBranchService.findByIdIfExists(branchId));
    }

    @Test
    void findAllByIdShouldReturnMap() {
        Set<UUID> ids = Set.of(branchId);
        Map<UUID, ShopBranch> expectedMap = Map.of(branchId, shopBranch);

        when(shopBranchRepository.findAllById(ids)).thenReturn(expectedMap);

        Map<UUID, ShopBranch> result = shopBranchService.findAllById(ids);

        assertEquals(1, result.size());
        assertEquals(expectedMap, result);
    }

    @Test
    void deleteShouldDeleteShopBranch() {
        when(shopBranchRepository.findById(branchId)).thenReturn(Optional.of(shopBranch));
        doNothing().when(shopBranchRepository).delete(shopBranch);

        shopBranchService.delete(branchId);

        verify(shopBranchRepository).delete(shopBranch);
    }

    @Test
    void updateShouldSuccessfullyUpdateShopBranch() {
        ShopBranchUpdateDTO updateDTO = new ShopBranchUpdateDTO("updatedStret", 1, 1);

        when(shopBranchRepository.findById(branchId)).thenReturn(Optional.ofNullable(shopBranch));
        when(shopBranchMapper.updateShopBranchFromUpdateShopBranchDTO(updateDTO, shopBranch)).thenReturn(shopBranch);
        when(shopBranchMapper.shopBranchToShopBranchDTO(any())).thenReturn(shopBranchDTO);

        ShopBranchDTO result = shopBranchService.update(branchId, updateDTO);

        assertNotNull(result);
        verify(shopBranchRepository).update(any(ShopBranch.class));
    }
}