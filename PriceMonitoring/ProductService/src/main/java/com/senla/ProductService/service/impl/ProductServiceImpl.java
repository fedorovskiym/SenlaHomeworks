package com.senla.ProductService.service.impl;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.exceptions.CsvException;
import com.senla.ProductService.dto.price.CreateUpdateProductPriceDTO;
import com.senla.ProductService.dto.product.CreateProductDTO;
import com.senla.ProductService.dto.product.ProductDTO;
import com.senla.ProductService.dto.product.ProductSearchDTO;
import com.senla.ProductService.dto.product.ProductUpdateDTO;
import com.senla.ProductService.exception.CsvImportException;
import com.senla.ProductService.mapper.ProductMapper;
import com.senla.ProductService.model.Brand;
import com.senla.ProductService.model.Product;
import com.senla.ProductService.model.ProductCategory;
import com.senla.ProductService.model.enums.ProductSortType;
import com.senla.ProductService.repository.ProductRepository;
import com.senla.ProductService.service.BrandService;
import com.senla.ProductService.service.ProductCategoryService;
import com.senla.ProductService.service.ProductService;
import com.senla.ProductService.util.YandexCloudUtil;
import jakarta.persistence.EntityNotFoundException;
import org.apache.tomcat.util.http.InvalidParameterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final BrandService brandService;
    private final ProductCategoryService productCategoryService;
    private final YandexCloudUtil yandexCloudUtil;
    private static final String FOLDER = "product_images/";
    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository, ProductMapper productMapper,
                              BrandService brandService, ProductCategoryService productCategoryService,
                              YandexCloudUtil yandexCloudUtil) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
        this.brandService = brandService;
        this.productCategoryService = productCategoryService;
        this.yandexCloudUtil = yandexCloudUtil;
    }

    @Override
    @Transactional
    public ProductDTO save(ProductDTO productDTO, MultipartFile photo) {
        logger.info("Saving product from dto {}", productDTO);
        Brand brand = brandService.findByIdIfExists(productDTO.brandId());
        ProductCategory productCategory = productCategoryService.findByIdIfExists(productDTO.categoryId());

        Product product = productMapper.productDTOToProduct(productDTO);
        product.setBrand(brand);
        product.setProductCategory(productCategory);
        if (!photo.isEmpty()) {
            logger.info("Saving product image to Yandex Cloud Storage");
            product.setImageUrl(yandexCloudUtil.saveImageToStorage(photo, FOLDER));
            logger.info("Successfull saved product image to Yandex Cloud Storage");
        }

        productRepository.save(product);
        logger.info("Succesfull save product {}", product);
        return productMapper.productToProductDTO(product);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> findAll(ProductSearchDTO productSearchDTO) {
        logger.info("Finding all products");

        if(!productSearchDTO.sortBy().equals(ProductSortType.ID.getDisplayName()) &&
                !productSearchDTO.sortBy().equals(ProductSortType.AMOUNT.getDisplayName()) &&
                !productSearchDTO.sortBy().equals(ProductSortType.NAME.getDisplayName())) {
            logger.warn("Wrong sort parameters for filters {}", productSearchDTO);
            throw new InvalidParameterException("Sort only by id, amount or name");
        }

        return productRepository.findAllWithPagination(productSearchDTO).stream()
                .map(productMapper::productToProductDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDTO findById(UUID id) {
        logger.info("Finding product dto by id {}", id);
        return productMapper.productToProductDTO(findByIdIfExists(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Product findByIdIfExists(UUID id) {
        logger.info("Finding product by id is exists {}", id);
        return productRepository.findById(id).orElseThrow(() -> {
            logger.warn("Product with id {} not found", id);
            return new EntityNotFoundException("Product with id - " + id + " not found!");
        });
    }

    @Override
    @Transactional(readOnly = true)
    public void delete(UUID id) {
        logger.info("Deleting product by id {}", id);
        Product product = findByIdIfExists(id);
        if (product.getImageUrl() != null) {
            logger.info("Deleting product image from Yandex Cloud Storage");
            yandexCloudUtil.deleteImage(product.getImageUrl());
            logger.info("Successfull deleted product image from Yandex Cloud Storage");
        }
        productRepository.delete(product);
        logger.info("Successfully deleted product {}", product);
    }

    @Override
    @Transactional
    public ProductDTO update(UUID id, ProductUpdateDTO productUpdateDTO) {
        logger.info("Updating product by id {} from dto {}", id, productUpdateDTO);
        Product product = findByIdIfExists(id);

        if (productUpdateDTO.brandId() != null) {
            product.setBrand(brandService.findByIdIfExists(productUpdateDTO.brandId()));
        }
        if (productUpdateDTO.categoryId() != null) {
            product.setProductCategory(productCategoryService.findByIdIfExists(productUpdateDTO.categoryId()));
        }

        product = productMapper.updateProductFromDTO(productUpdateDTO, product);
        productRepository.update(product);
        logger.info("Successfull updated product {}", product);
        return productMapper.productToProductDTO(product);
    }

    @Override
    @Transactional
    public ProductDTO updateImage(UUID id, MultipartFile photo) {
        logger.info("Updating product image with id {}", id);
        Product product = findByIdIfExists(id);

        if (product.getImageUrl() != null) {
            logger.info("Deleting old product image from Yandex Cloud Storage");
            yandexCloudUtil.deleteImage(product.getImageUrl());
            logger.warn("Successfull deleted old product image from Yandex Cloud Storage");
        }

        logger.info("Saving product image to Yandex Cloud Storage");
        product.setImageUrl(yandexCloudUtil.saveImageToStorage(photo, FOLDER));
        logger.info("Successfull saved product image to Yandex Cloud Storage");
        productRepository.update(product);
        logger.info("Successfull updated product {} ", product);
        return productMapper.productToProductDTO(product);
    }

    @Override
    @Transactional
    public void importFromCsv(MultipartFile file) {
        logger.info("Import product from csv file");
        validateFile(file);

        try (Reader reader = new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8)) {
            List<CreateProductDTO> rows = parseCsv(reader);
            List<Product> updateList = new ArrayList<>();
            List<Product> saveList = new ArrayList<>();

            Set<UUID> setBrandId = rows.stream().map(CreateProductDTO::getBrandId)
                    .collect(Collectors.toSet());
            Set<UUID> setProductCategoryId = rows.stream().map(CreateProductDTO::getCategoryId)
                    .collect(Collectors.toSet());
            Map<UUID, Brand> brandMap = brandService.findAllById(setBrandId);
            Map<UUID, ProductCategory> categoryMap = productCategoryService.findAllById(setProductCategoryId);
            for (CreateProductDTO row : rows) {
                Brand brand = brandMap.get(row.getBrandId());
                if (brand == null) {
                    continue;
                }

                ProductCategory productCategory = categoryMap.get(row.getCategoryId());
                if (productCategory == null) {
                    continue;
                }
                System.out.println(row.getProductId());
                if (row.getProductId() == null) {
                    Product product = buildProduct(row, brand, productCategory);
                    saveList.add(product);
                    continue;
                }

                Product existingProduct = findByIdOrNull(row.getProductId());
                if (existingProduct == null) {
                    continue;
                }
                buildUpdateProdict(existingProduct, row, brand, productCategory);
                updateList.add(existingProduct);
            }

            logger.info("Import from file end");
            productRepository.updateList(updateList);
            productRepository.saveList(saveList);
        } catch (IOException e) {
            logger.error("Error while reading file {}", file.getOriginalFilename(), e);
            throw new RuntimeException(e);
        }

    }

    private void buildUpdateProdict(Product product, CreateProductDTO createProductDTO,
                                    Brand brand, ProductCategory productCategory) {
        product.setBrand(brand);
        product.setProductCategory(productCategory);
        product.setDescription(createProductDTO.getDescription());
        product.setAmount(createProductDTO.getAmount());
        product.setUnit(createProductDTO.getUnit());
    }

    private Product buildProduct(CreateProductDTO createProductDTO, Brand brand,
                                 ProductCategory productCategory) {
        Product product = productMapper.createProductDTOToProduct(createProductDTO);
        product.setBrand(brand);
        product.setProductCategory(productCategory);
        product.setId(null);
        return product;
    }

    @Override
    @Transactional(readOnly = true)
    public Product findByName(String name) {
        logger.info("Finding product by name {} or null", name);
        return productRepository.findByName(name).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<UUID, Product> findAllById(Set<UUID> listProductId) {
        return productRepository.findAllById(listProductId);
    }

    private List<CreateProductDTO> parseCsv(Reader reader) throws IOException {
        logger.info("Parsing CSV file");
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
                .withOrderedResults(true)
                .withThrowExceptions(false)
                .build();

        List<CreateProductDTO> rows = csvToBean.parse();

        List<CsvException> errors = csvToBean.getCapturedExceptions();
        logger.info("Parsing CSV file end");
        if (!errors.isEmpty()) {
            logger.warn("{} errors while reading CSV file", errors.size());
            throw new CsvImportException("CSV contains invalid rows: " + errors.size(), null);
        }
        return rows;
    }

    private void validateFile(MultipartFile file) {
        if (!file.getOriginalFilename().endsWith("csv")) {
            logger.warn("Invalid file extension {}", file.getOriginalFilename());
            throw new InvalidParameterException("Only .csv files supported!");
        }
        if (file.isEmpty()) {
            logger.warn("Empty product price file");
            throw new InvalidParameterException("Empty file!");
        }
    }

    @Transactional(readOnly = true)
    protected Product findByIdOrNull(UUID id) {
        return productRepository.findById(id).orElse(null);
    }

}
