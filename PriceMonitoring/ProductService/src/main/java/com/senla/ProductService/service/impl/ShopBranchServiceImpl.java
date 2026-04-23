package com.senla.ProductService.service.impl;

import com.senla.ProductService.dto.ShopBranchDTO;
import com.senla.ProductService.mapper.ProductPriceMapper;
import com.senla.ProductService.mapper.ShopBranchMapper;
import com.senla.ProductService.model.City;
import com.senla.ProductService.model.Shop;
import com.senla.ProductService.model.ShopBranch;
import com.senla.ProductService.repository.ShopBranchRepository;
import com.senla.ProductService.service.CityService;
import com.senla.ProductService.service.ShopBranchService;
import com.senla.ProductService.service.ShopService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ShopBranchServiceImpl implements ShopBranchService {

    private final ShopBranchRepository shopBranchRepository;
    private final ShopBranchMapper shopBranchMapper;
    private final CityService cityService;
    private final ShopService shopService;

    @Autowired
    public ShopBranchServiceImpl(ShopBranchRepository shopBranchRepository, ShopBranchMapper shopBranchMapper, CityService cityService, ShopService shopService) {
        this.shopBranchRepository = shopBranchRepository;
        this.shopBranchMapper = shopBranchMapper;
        this.cityService = cityService;
        this.shopService = shopService;
    }


    @Override
    @Transactional
    public void save(ShopBranchDTO shopBranchDTO) {
        City city = cityService.getCityByIdIfExists(shopBranchDTO.cityId());
        Shop shop = shopService.findByIdIfExists(shopBranchDTO.shopId());

        ShopBranch shopBranch = shopBranchMapper.shopBranchDTOToShopBranch(shopBranchDTO);
        shopBranch.setCity(city);
        shopBranch.setShop(shop);
        shopBranchRepository.save(shopBranch);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShopBranchDTO> findAllByShopId(Long shopId) {
        return shopBranchRepository.findAllByShopIdFetch(shopId)
                .stream().map(shopBranchMapper::shopBranchToShopBranchDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ShopBranchDTO findById(Long id) {
        return shopBranchMapper.shopBranchToShopBranchDTO(findByIdIfExists(id));
    }

    @Override
    @Transactional(readOnly = true)
    public ShopBranch findByIdIfExists(Long id) {
        return shopBranchRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Shop branch with id - " + id + " not found!"));
    }
}
