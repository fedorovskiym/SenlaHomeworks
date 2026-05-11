package com.senla.ProductService.service;

import com.senla.ProductService.dto.ShopDTO;
import com.senla.ProductService.model.Shop;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface ShopService {

    ShopDTO save(ShopDTO shopDTO, MultipartFile photo);

    List<ShopDTO> findAllByCityId(UUID cityId);

    void delete(UUID id);

    ShopDTO findById(UUID id);

    Shop findByIdIfExists(UUID id);
}
