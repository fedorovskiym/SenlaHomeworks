package com.senla.ProductService.service.impl;

import com.senla.ProductService.dto.ShopDTO;
import com.senla.ProductService.exception.custom.ShopException;
import com.senla.ProductService.mapper.ShopMapper;
import com.senla.ProductService.model.Shop;
import com.senla.ProductService.repository.ShopRepository;
import com.senla.ProductService.service.ShopService;
import com.senla.ProductService.util.YandexCloudUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ShopServiceImpl implements ShopService {

    private final ShopRepository shopRepository;
    private final ShopMapper shopMapper;
    private final YandexCloudUtil yandexCloudUtil;
    private static final String FOLDER = "shops_logo/";

    @Autowired
    public ShopServiceImpl(ShopRepository shopRepository, ShopMapper shopMapper, YandexCloudUtil yandexCloudUtil) {
        this.shopRepository = shopRepository;
        this.shopMapper = shopMapper;
        this.yandexCloudUtil = yandexCloudUtil;
    }

    @Override
    @Transactional
    public void save(ShopDTO shopDTO, MultipartFile photo) {
        if(!shopRepository.findByName(shopDTO.name()).isEmpty()) {
            throw new ShopException("Shop with name - " + shopDTO.name() + " already exists!");
        }
        Shop shop = shopMapper.shopDTOToShop(shopDTO);
        shop.setLogoImageUrl(yandexCloudUtil.saveImageToStorage(photo, FOLDER));
        shopRepository.save(shop);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShopDTO> findAllWithPagination(Integer page, Integer size) {
        return shopRepository.findAllWithPagination(page, size).stream().map(shopMapper::shopToShopDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Shop shop = shopRepository.findById(id).orElseThrow(
                () -> new ShopException("Shop with id - " + id + " does not exist!"));
        yandexCloudUtil.deleteImage(shop.getLogoImageUrl());
        shopRepository.delete(shop);
    }

    @Override
    @Transactional(readOnly = true)
    public ShopDTO findById(Long id) {
        return shopRepository.findById(id).map(shopMapper::shopToShopDTO).orElseThrow(
                () -> new ShopException("Shop with id - " + id + " does not exist!")
        );
    }
}
