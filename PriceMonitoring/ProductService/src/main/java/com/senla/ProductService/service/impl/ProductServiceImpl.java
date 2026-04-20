package com.senla.ProductService.service.impl;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.exceptions.CsvValidationException;
import com.senla.ProductService.dto.product.CreateProductDTO;
import com.senla.ProductService.dto.product.ProductDTO;
import com.senla.ProductService.dto.product.ProductUpdateDTO;
import com.senla.ProductService.mapper.ProductMapper;
import com.senla.ProductService.model.Brand;
import com.senla.ProductService.model.Product;
import com.senla.ProductService.model.ProductCategory;
import com.senla.ProductService.repository.ProductRepository;
import com.senla.ProductService.service.BrandService;
import com.senla.ProductService.service.ProductCategoryService;
import com.senla.ProductService.service.ProductService;
import com.senla.ProductService.util.YandexCloudUtil;
import jakarta.persistence.EntityNotFoundException;
import org.apache.tomcat.util.http.InvalidParameterException;
import org.apache.tomcat.util.http.fileupload.InvalidFileNameException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.cfg.MapperBuilder;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final BrandService brandService;
    private final ProductCategoryService productCategoryService;
    private final YandexCloudUtil yandexCloudUtil;
    private static final String FOLDER = "product_images/";

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository, ProductMapper productMapper, BrandService brandService, ProductCategoryService productCategoryService, YandexCloudUtil yandexCloudUtil) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
        this.brandService = brandService;
        this.productCategoryService = productCategoryService;
        this.yandexCloudUtil = yandexCloudUtil;
    }

    @Override
    @Transactional
    public void save(ProductDTO productDTO, MultipartFile photo) {
        Brand brand = brandService.findByIdIfExists(productDTO.brandId());
        ProductCategory productCategory = productCategoryService.findByIdIfExists(productDTO.categoryId());

        Product product = productMapper.productDTOToProduct(productDTO);
        product.setBrand(brand);
        product.setProductCategory(productCategory);
        if (!photo.isEmpty()) {
            product.setImageUrl(yandexCloudUtil.saveImageToStorage(photo, FOLDER));
        }
        productRepository.save(product);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> findAll() {
        return productRepository.findAll().stream().map(productMapper::productToProductDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> findByCategoryId(Long categoryId) {
        return List.of();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> findByBrandId(Long brandId) {
        return List.of();
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDTO findById(Long id) {
        return productMapper.productToProductDTO(findByIdIfExists(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Product findByIdIfExists(Long id) {
        return productRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Product with id - " + id + " not found!"));
    }

    @Override
    @Transactional(readOnly = true)
    public void delete(Long id) {
        Product product = findByIdIfExists(id);
        if (product.getImageUrl() != null) {
            yandexCloudUtil.deleteImage(product.getImageUrl());
        }
        productRepository.delete(product);
    }

    @Override
    @Transactional
    public void update(Long id, ProductUpdateDTO productUpdateDTO) {
        Product product = findByIdIfExists(id);

        if (productUpdateDTO.brandId() != null) {
            product.setBrand(brandService.findByIdIfExists(productUpdateDTO.brandId()));
        }
        if (productUpdateDTO.categoryId() != null) {
            product.setProductCategory(productCategoryService.findByIdIfExists(productUpdateDTO.categoryId()));
        }

        product = productMapper.updateProductFromDTO(productUpdateDTO, product);
        productRepository.update(product);
    }

    @Override
    @Transactional
    public void updateImage(Long id, MultipartFile photo) {
        Product product = findByIdIfExists(id);

        if (product.getImageUrl() != null) {
            yandexCloudUtil.deleteImage(product.getImageUrl());
        }

        product.setImageUrl(yandexCloudUtil.saveImageToStorage(photo, FOLDER));
        productRepository.update(product);
    }

    @Override
    @Transactional(readOnly = true)
    public Product findWithPrices(Long id) {
        return productRepository.findByIdWithPrices(id).orElseThrow(
                () -> new EntityNotFoundException("Product with id - " + id + " not found!")
        );
    }

    @Override
    @Transactional
    public void importFromCsv(MultipartFile file) {
        if (!file.getOriginalFilename().endsWith("csv")) {
            throw new InvalidParameterException("Only .csv files supported!");
        }
        if (file.isEmpty()) {
            throw new InvalidParameterException("Empty file!");
        }

        try (Reader reader = new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8)) {
            List<CreateProductDTO> rows = parseCsv(reader);
            List<Product> updateList = new ArrayList<>();
            List<Product> saveList = new ArrayList<>();
            rows.stream()
                    .map(row -> {
                        if (row.getBrandId() == null || row.getCategoryId() == null) {
                            return null;
                        }
                        Brand brand = brandService.findByIdOptional(row.getBrandId()).orElse(null);
                        ProductCategory category = productCategoryService.findByIdOptional(row.getCategoryId()).orElse(null);

                        if (brand == null || category == null) {
                            return null;
                        }

                        Product product = productMapper.createProductDTOToProduct(row);
                        product.setBrand(brand);
                        product.setProductCategory(category);
                        return product;
                    })
                    .filter(Objects::nonNull)
                    .forEach(product -> {
                        Product presentProduct = findByName(product.getName());
                        if (presentProduct != null) {
                            product.setId(presentProduct.getId());
                            updateList.add(product);
                        } else {
                            saveList.add(product);
                        }
                    });

            productRepository.updateList(updateList);
            productRepository.saveList(saveList);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Product findByName(String name) {
        return productRepository.findByName(name).orElse(null);
    }

    private List<CreateProductDTO> parseCsv(Reader reader) throws IOException {
        CSVParser csvParser = new CSVParserBuilder()
                .withSeparator(';')
                .withIgnoreQuotations(true)
                .build();

        CSVReader csvReader = new CSVReaderBuilder(reader)
                .withCSVParser(csvParser)
                .build();

        CsvToBean<CreateProductDTO> csvToBean = new CsvToBeanBuilder<CreateProductDTO>(csvReader)
                .withType(CreateProductDTO.class)
                .withIgnoreEmptyLine(true)
                .withIgnoreLeadingWhiteSpace(true)
                .build();

        return csvToBean.parse();
    }
}
