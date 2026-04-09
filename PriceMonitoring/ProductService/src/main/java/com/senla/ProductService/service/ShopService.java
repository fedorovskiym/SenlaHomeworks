package com.senla.ProductService.service;

import com.senla.ProductService.dto.ShopDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ShopService {

    void save (ShopDTO shopDTO, MultipartFile photo);

    List<ShopDTO> findAllWithPagination(Integer page, Integer size);

    void delete(Long id);

    ShopDTO findById(Long id);
}
