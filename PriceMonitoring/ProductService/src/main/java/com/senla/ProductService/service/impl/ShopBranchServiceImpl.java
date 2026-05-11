package com.senla.ProductService.service.impl;

import com.senla.ProductService.dto.ShopBranchDTO;
import com.senla.ProductService.mapper.ShopBranchMapper;
import com.senla.ProductService.model.City;
import com.senla.ProductService.model.Shop;
import com.senla.ProductService.model.ShopBranch;
import com.senla.ProductService.repository.ShopBranchRepository;
import com.senla.ProductService.service.CityService;
import com.senla.ProductService.service.ShopBranchService;
import com.senla.ProductService.service.ShopService;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ShopBranchServiceImpl implements ShopBranchService {

    private final ShopBranchRepository shopBranchRepository;
    private final ShopBranchMapper shopBranchMapper;
    private final CityService cityService;
    private final ShopService shopService;
    private static final Logger logger = LoggerFactory.getLogger(ShopBranchServiceImpl.class);

    @Autowired
    public ShopBranchServiceImpl(ShopBranchRepository shopBranchRepository,
                                 ShopBranchMapper shopBranchMapper, CityService cityService,
                                 ShopService shopService) {
        this.shopBranchRepository = shopBranchRepository;
        this.shopBranchMapper = shopBranchMapper;
        this.cityService = cityService;
        this.shopService = shopService;
    }


    @Override
    @Transactional
    public ShopBranchDTO save(ShopBranchDTO shopBranchDTO) {
        logger.info("Save shop branch from dto {}", shopBranchDTO);
        City city = cityService.getCityByIdIfExists(shopBranchDTO.cityId());
        Shop shop = shopService.findByIdIfExists(shopBranchDTO.shopId());

        ShopBranch shopBranch = shopBranchMapper.shopBranchDTOToShopBranch(shopBranchDTO);
        shopBranch.setCity(city);
        shopBranch.setShop(shop);
        shopBranchRepository.save(shopBranch);
        logger.info("Successfull save shop branch {}", shopBranch);
        return shopBranchMapper.shopBranchToShopBranchDTO(shopBranch);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShopBranchDTO> findAllByShopId(UUID shopId) {
        logger.info("Find all shop branches dto by city id {}", shopId);
        return shopBranchRepository.findAllByShopIdFetch(shopId)
                .stream().map(shopBranchMapper::shopBranchToShopBranchDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ShopBranchDTO findById(UUID id) {
        logger.info("Find shop branch dto by id {}", id);
        return shopBranchMapper.shopBranchToShopBranchDTO(findByIdIfExists(id));
    }

    @Override
    @Transactional(readOnly = true)
    public ShopBranch findByIdIfExists(UUID id) {
        logger.info("Find shop branch by id {}", id);
        return shopBranchRepository.findById(id).orElseThrow(() -> {
            logger.warn("Shop branch not found with id {}", id);
            return new EntityNotFoundException("Shop branch with id - " + id + " not found!");
        });
    }

    @Override
    @Transactional(readOnly = true)
    public Map<UUID, ShopBranch> findAllById(Set<UUID> listShopBranchId) {
        return shopBranchRepository.findAllById(listShopBranchId);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        logger.info("Delete shop branch {}", id);
        ShopBranch shopBranch = findByIdIfExists(id);
        shopBranchRepository.delete(shopBranch);
        logger.info("Successfully delete shop branch {}", id);
    }
}
