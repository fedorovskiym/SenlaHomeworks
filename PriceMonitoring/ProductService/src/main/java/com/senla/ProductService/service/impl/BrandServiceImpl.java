package com.senla.ProductService.service.impl;

import com.senla.ProductService.dto.BrandDTO;
import com.senla.ProductService.exception.custom.BrandException;
import com.senla.ProductService.mapper.BrandMapper;
import com.senla.ProductService.model.Brand;
import com.senla.ProductService.repository.BrandRepository;
import com.senla.ProductService.service.BrandService;
import com.senla.ProductService.util.YandexCloudUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
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
        if (!brandRepository.findByName(brandDTO.name()).isEmpty()) {
            throw new BrandException("Brand with name - " + brandDTO.name() + " already exists!");
        }
        Brand brand = brandMapper.brandDTOToBrand(brandDTO);
        brand.setLogoImageUrl(yandexCloudUtil.saveImageToStorage(photo, FOLDER));
        brandRepository.save(brand);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BrandDTO> findWithPagination(Integer page, Integer size) {
        return brandRepository.findWithPagination(page, size)
                .stream().map(brandMapper::brandToBrandDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Brand brand = brandRepository.findById(id).orElseThrow(
                () -> new BrandException("Brand with id - " + id + " not found!"));
        yandexCloudUtil.deleteImage(brand.getLogoImageUrl());
        brandRepository.delete(brand);
    }

    @Override
    public BrandDTO findById(Long id) {
        return brandMapper.brandToBrandDTO(brandRepository.findById(id).orElseThrow(
                () -> new BrandException("Brand with id - " + id + " not found!")
        ));
    }
}
