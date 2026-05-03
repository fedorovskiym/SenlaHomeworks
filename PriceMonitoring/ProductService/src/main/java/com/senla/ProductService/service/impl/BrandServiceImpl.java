package com.senla.ProductService.service.impl;

import com.senla.ProductService.dto.brand.BrandDTO;
import com.senla.ProductService.dto.brand.BrandUpdateDTO;
import com.senla.ProductService.mapper.BrandMapper;
import com.senla.ProductService.model.Brand;
import com.senla.ProductService.repository.BrandRepository;
import com.senla.ProductService.service.BrandService;
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
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;
    private final YandexCloudUtil yandexCloudUtil;
    private final BrandMapper brandMapper;
    private static final String FOLDER = "brands_logo/";
    private static final Logger logger = LoggerFactory.getLogger(BrandServiceImpl.class);

    @Autowired
    public BrandServiceImpl(BrandRepository brandRepository, YandexCloudUtil yandexCloudUtil, BrandMapper brandMapper) {
        this.brandRepository = brandRepository;
        this.yandexCloudUtil = yandexCloudUtil;
        this.brandMapper = brandMapper;
    }

    @Override
    @Transactional
    public BrandDTO save(BrandDTO brandDTO, MultipartFile photo) {
        logger.info("Save brand from dto {} with logo image", brandDTO);
        if (findByNameIfExists(brandDTO.name()) != null) {
            logger.warn("Brand with name {} already exists", brandDTO.name());
            throw new EntityExistsException("Brand with name - " + brandDTO.name() + " already exists!");
        }
        Brand brand = brandMapper.brandDTOToBrand(brandDTO);
        if (!photo.isEmpty()) {
            logger.info("Saving brand logo image to Yandex Cloud Storage");
            brand.setLogoImageUrl(yandexCloudUtil.saveImageToStorage(photo, FOLDER));
            logger.info("Succesfull saved logo image to Yandex Cloud Storage");
        }
        logger.info("Succesfull save brand {}", brand);
        brandRepository.save(brand);
        return brandMapper.brandToBrandDTO(brand);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BrandDTO> findAll() {
        logger.info("Find all brands");
        return brandRepository.findAll()
                .stream().map(brandMapper::brandToBrandDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void delete(Long id) {
        logger.info("Delete brand by id {}", id);
        Brand brand = findByIdIfExists(id);
        if (brand.getLogoImageUrl() != null) {
            logger.info("Deleting brand logo image from Yandex Cloud Storage");
            yandexCloudUtil.deleteImage(brand.getLogoImageUrl());
            logger.info("Succesfully deleted logo image from Yandex Cloud Storage");
        }
        brandRepository.delete(brand);
        logger.info("Succesfully delete brand {}", brand);
    }

    @Override
    @Transactional(readOnly = true)
    public BrandDTO findById(Long id) {
        logger.info("Find brand with id {}", id);
        return brandMapper.brandToBrandDTO(findByIdIfExists(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Brand findByIdIfExists(Long id) {
        logger.info("Find brand with id is exists {}", id);
        return brandRepository.findById(id).orElseThrow(() -> {
            logger.warn("Brand with id {} not found", id);
            return new EntityNotFoundException("Brand with id - " + id + " not found!");
        });
    }

    @Override
    public Brand findByNameIfExists(String name) {
        logger.info("Find brand with name {}", name);
        return brandRepository.findByName(name).orElse(null);
    }

    @Override
    @Transactional
    public BrandDTO update(Long id, BrandUpdateDTO brandDTO) {
        logger.info("Update brand with id {}", id);
        Brand brand = findByIdIfExists(id);
        if (brand.getName().equals(brandDTO.name()) || findByNameIfExists(brandDTO.name()) != null) {
            logger.warn("Brand with name {} already exists", brandDTO.name());
            throw new EntityExistsException("Brand with name - " + brandDTO.name() + " already exists!");
        }
        brand = brandMapper.updateBrandFromBrandUpdateDTO(brandDTO, brand);
        brandRepository.update(brand);
        logger.info("Succesfully updated brand {}", brand);
        return brandMapper.brandToBrandDTO(brand);
    }

    @Override
    @Transactional
    public BrandDTO updateLogo(Long id, MultipartFile photo) {
        logger.info("Update logo by brand id {}", id);
        Brand brand = findByIdIfExists(id);

        if (brand.getLogoImageUrl() != null) {
            logger.info("Update logo image to Yandex Cloud Storage");
            yandexCloudUtil.deleteImage(brand.getLogoImageUrl());
            logger.info("Succesfully updated logo image to Yandex Cloud Storage");
        }

        brand.setLogoImageUrl(photo.getOriginalFilename());
        brandRepository.update(brand);
        logger.info("Succesfully updated brand {}", brand);
        return brandMapper.brandToBrandDTO(brand);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Brand> findByIdOptional(Long id) {
        logger.info("Find brand with id {}", id);
        return brandRepository.findById(id);
    }
}
