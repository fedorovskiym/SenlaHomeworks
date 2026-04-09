package com.senla.ProductService.service;

import com.senla.ProductService.dto.BrandDTO;
import com.senla.ProductService.model.Brand;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface BrandService {

    void save(BrandDTO brandDTO, MultipartFile photo);

    List<BrandDTO> findWithPagination(Integer page, Integer size);

    void delete(Long id);

    BrandDTO findById(Long id);
}
