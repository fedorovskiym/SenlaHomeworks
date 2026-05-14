package com.senla.ProductService.service.impl;

import com.senla.ProductService.dto.ShopDTO;
import com.senla.ProductService.mapper.ShopMapper;
import com.senla.ProductService.model.Shop;
import com.senla.ProductService.repository.ShopRepository;
import com.senla.ProductService.service.ShopService;
import com.senla.ProductService.util.YandexCloudUtil;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.ws.rs.BadRequestException;
import org.apache.tomcat.util.http.InvalidParameterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ShopServiceImpl implements ShopService {

    private final ShopRepository shopRepository;
    private final ShopMapper shopMapper;
    private final YandexCloudUtil yandexCloudUtil;
    private static final String FOLDER = "shops_logo/";
    private static final Logger logger = LoggerFactory.getLogger(ShopServiceImpl.class);

    @Autowired
    public ShopServiceImpl(ShopRepository shopRepository, ShopMapper shopMapper, YandexCloudUtil yandexCloudUtil) {
        this.shopRepository = shopRepository;
        this.shopMapper = shopMapper;
        this.yandexCloudUtil = yandexCloudUtil;
    }

    @Override
    @Transactional
    public ShopDTO save(ShopDTO shopDTO, MultipartFile photo) {
        logger.info("Save shop from dto {}", shopDTO);
        if (!shopRepository.findByName(shopDTO.name()).isEmpty()) {
            logger.warn("Error while save shop, shop with name {} already exists", shopDTO.name());
            throw new EntityExistsException("Shop with name - " + shopDTO.name() + " already exists!");
        }
        Shop shop = shopMapper.shopDTOToShop(shopDTO);
        if (!photo.isEmpty()) {
            logger.info("Save shop logo image to Yandex Cloud Storage");
            shop.setLogoImageUrl(yandexCloudUtil.saveImageToStorage(photo, FOLDER));
            logger.info("Save shop logo image to Yandex Cloud Storage");
        }
        shopRepository.save(shop);
        logger.info("Successfull saved shop {}", shop);
        return shopMapper.shopToShopDTO(shop);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShopDTO> findAllByCityId(UUID cityId) {
        logger.info("Find shops dto by city id {}", cityId);
        return shopRepository.findAllByCityId(cityId).stream()
                .map(shopMapper::shopToShopDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        logger.info("Delete shop with id {}", id);
        Shop shop = findByIdIfExists(id);
        logger.info("Delete shop logo image from Yandex Cloud Storage");
        yandexCloudUtil.deleteImage(shop.getLogoImageUrl());
        logger.info("Succesfull delete shop logo image from Yandex Cloud Storage");
        shopRepository.delete(shop);
        logger.info("Succesfull delete shop {}", shop);
    }

    @Override
    @Transactional(readOnly = true)
    public ShopDTO findById(UUID id) {
        logger.info("Find shop dto with id {}", id);
        return shopMapper.shopToShopDTO(findByIdIfExists(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Shop findByIdIfExists(UUID id) {
        logger.info("Find shop with id {}", id);
        return shopRepository.findById(id).orElseThrow(() -> {
            logger.warn("Shop with id {} not found", id);
            return new EntityNotFoundException("Shop with id - " + id + " not found!");
        });
    }

    @Override
    @Transactional
    public ShopDTO update(UUID id, String shopName) {
        if (shopName.isEmpty()) {
            logger.warn("Shop name is empty");
            throw new InvalidParameterException("Shop name is empty");
        }
        if (shopRepository.findByName(shopName).isPresent()) {
            logger.warn("Shop with name {} already exists", shopName);
            throw new EntityExistsException("Shop with name - " + shopName + " already exists!");
        }

        Shop shop = findByIdIfExists(id);
        shop.setName(shopName);
        shopRepository.update(shop);
        logger.info("Successfull update shop {}", shop);
        return shopMapper.shopToShopDTO(shop);
    }

    @Override
    @Transactional
    public ShopDTO updateLogo(UUID id, MultipartFile photo) {
        Shop shop = findByIdIfExists(id);

        if (shop.getLogoImageUrl() != null) {
            yandexCloudUtil.deleteImage(shop.getLogoImageUrl());
        }

        shop.setLogoImageUrl(yandexCloudUtil.saveImageToStorage(photo, FOLDER));
        logger.info("Succesfull update logo image");
        shopRepository.update(shop);
        return shopMapper.shopToShopDTO(shop);
    }

}
