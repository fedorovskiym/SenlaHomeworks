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

    @Autowired
    public BrandServiceImpl(BrandRepository brandRepository, YandexCloudUtil yandexCloudUtil, BrandMapper brandMapper) {
        this.brandRepository = brandRepository;
        this.yandexCloudUtil = yandexCloudUtil;
        this.brandMapper = brandMapper;
    }

    @Override
    @Transactional
    public void save(BrandDTO brandDTO, MultipartFile photo) {
        if (findByNameIfExists(brandDTO.name()) != null) {
            throw new EntityExistsException("Brand with name - " + brandDTO.name() + " already exists!");
        }
        Brand brand = brandMapper.brandDTOToBrand(brandDTO);
        if (!photo.isEmpty()) {
            brand.setLogoImageUrl(yandexCloudUtil.saveImageToStorage(photo, FOLDER));
        }
        brandRepository.save(brand);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BrandDTO> findAll() {
        return brandRepository.findAll()
                .stream().map(brandMapper::brandToBrandDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Brand brand = findByIdIfExists(id);
        if(brand.getLogoImageUrl() != null) {
            yandexCloudUtil.deleteImage(brand.getLogoImageUrl());
        }
        brandRepository.delete(brand);
    }

    @Override
    @Transactional(readOnly = true)
    public BrandDTO findById(Long id) {
        return brandMapper.brandToBrandDTO(findByIdIfExists(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Brand findByIdIfExists(Long id) {
        return brandRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Brand with id - " + id + " not found!"));
    }

    @Override
    public Brand findByNameIfExists(String name) {
        return brandRepository.findByName(name).orElse(null);
    }

    @Override
    @Transactional
    public void update(Long id, BrandUpdateDTO brandDTO) {
        Brand brand = findByIdIfExists(id);
        if(brand.getName().equals(brandDTO.name()) || findByNameIfExists(brandDTO.name()) != null) {
            throw new EntityExistsException("Brand with name - " + brandDTO.name() + " already exists!");
        }
        brand = brandMapper.updateBrandFromBrandUpdateDTO(brandDTO, brand);
        brandRepository.update(brand);
    }

    @Override
    @Transactional
    public void updateLogo(Long id, MultipartFile photo) {
        Brand brand = findByIdIfExists(id);

        if(brand.getLogoImageUrl() != null) {
            yandexCloudUtil.deleteImage(brand.getLogoImageUrl());
        }

        brand.setLogoImageUrl(photo.getOriginalFilename());
        brandRepository.update(brand);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Brand> findByIdOptional(Long id) {
        return brandRepository.findById(id);
    }
}
