package com.senla.ProductService.service.impl;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.exceptions.CsvException;
import com.senla.ProductService.dto.brand.BrandDTO;
import com.senla.ProductService.dto.price.ComparePrice;
import com.senla.ProductService.dto.price.CreateProductPriceDTO;
import com.senla.ProductService.dto.price.PriceDTO;
import com.senla.ProductService.dto.price.ProductPriceDTO;
import com.senla.ProductService.dto.price.ProductPriceSearchDTO;
import com.senla.ProductService.dto.product.ProductSearchRequest;
import com.senla.ProductService.dto.productCategory.ProductCategoryDTO;
import com.senla.ProductService.exception.CsvImportException;
import com.senla.ProductService.mapper.ProductPriceMapper;
import com.senla.ProductService.model.Product;
import com.senla.ProductService.model.ProductPrice;
import com.senla.ProductService.model.ShopBranch;
import com.senla.ProductService.model.enums.PriceStatus;
import com.senla.ProductService.model.enums.ProductPriceSortType;
import com.senla.ProductService.repository.ProductPriceRepository;
import com.senla.ProductService.service.AIService;
import com.senla.ProductService.service.BrandService;
import com.senla.ProductService.service.ProductCategoryService;
import com.senla.ProductService.service.ProductPriceService;
import com.senla.ProductService.service.ProductService;
import com.senla.ProductService.service.ShopBranchService;
import jakarta.persistence.EntityNotFoundException;
import org.apache.tomcat.util.http.InvalidParameterException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ProductPriceServiceImpl implements ProductPriceService {

    private final ProductPriceRepository productPriceRepository;
    private final ProductPriceMapper productPriceMapper;
    private final ProductService productService;
    private final ShopBranchService shopBranchService;
    private final AIService aiService;
    private final BrandService brandService;
    private final ProductCategoryService productCategoryService;

    @Autowired
    public ProductPriceServiceImpl(ProductPriceRepository productPriceRepository, ProductPriceMapper productPriceMapper, ProductService productService, ShopBranchService shopBranchService, AIService aiService, BrandService brandService, ProductCategoryService productCategoryService) {
        this.productPriceRepository = productPriceRepository;
        this.productPriceMapper = productPriceMapper;
        this.productService = productService;
        this.shopBranchService = shopBranchService;
        this.aiService = aiService;
        this.brandService = brandService;
        this.productCategoryService = productCategoryService;
    }

    @Override
    @Transactional
    public void save(CreateProductPriceDTO createProductPriceDTO) {
        Product product = productService.findByIdIfExists(createProductPriceDTO.getProductId());
        ShopBranch shopBranch = shopBranchService.findByIdIfExists(createProductPriceDTO.getShopBranchId());

        ProductPrice productPrice = productPriceMapper.createProductPriceDTOToProductPrice(createProductPriceDTO);

        productPrice.setProduct(product);
        productPrice.setShopBranch(shopBranch);
        productPrice.setStartDate(LocalDate.now());
        productPrice.setStatus(PriceStatus.ACTUAL);

        productPriceRepository.save(productPrice);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductPriceDTO findById(Long id) {
        return productPriceMapper.productPriceToProductPriceDTO(productPriceRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Product's price with id - " + id + " not found!")));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductPriceDTO> findAllWithPagination(ProductPriceSearchDTO productPriceSearchDTO) {

        if (!productPriceSearchDTO.sortBy().equals(ProductPriceSortType.PRICE.getDisplayName()) &&
                !productPriceSearchDTO.sortBy().equals(ProductPriceSortType.DISCOUNT_PERCENT.getDisplayName()) &&
                !productPriceSearchDTO.sortBy().equals(ProductPriceSortType.ID.getDisplayName())) {
            throw new InvalidParameterException("Sort only by price or discountPercent or id");
        }

        return productPriceRepository.findAllWithPagination(productPriceSearchDTO.page(), productPriceSearchDTO.size(),
                        productPriceSearchDTO.shopBranchId(), productPriceSearchDTO.sortBy(), productPriceSearchDTO.asc(),
                        productPriceSearchDTO.brandId(), productPriceSearchDTO.categoryId())
                .stream().map(productPriceMapper::productPriceToProductPriceDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ComparePrice comparePricesInShops(Long productId, Long cityId) {
        List<ProductPrice> productPrices = productPriceRepository.findProductInShops(productId, cityId);

        if(productPrices.isEmpty()) {
            throw new EntityNotFoundException("No prices found in shops with id - " + productId);
        }

        ComparePrice comparePrice = new ComparePrice();
        String address = String.format("%s %s %s %s", productPrices.get(0).getShopBranch().getCity().getName(),
                productPrices.get(0).getShopBranch().getStreet(), productPrices.get(0).getShopBranch().getHouse(),
                productPrices.get(0).getShopBranch().getRoom());

        List<PriceDTO> otherPrices = productPrices.stream()
                .skip(1)
                .map(productPrice -> new PriceDTO(
                        productPrice.getPrice(),
                        productPrice.getShopBranch().getShop().getName(),
                        String.format("%s %s %s", productPrice.getShopBranch().getStreet(), productPrice.getShopBranch().getHouse(),
                                productPrice.getShopBranch().getRoom()),
                        productPrice.getShopBranch().getShop().getLogoImageUrl()
                ))
                .toList();

        comparePrice.setProductId(productId);
        comparePrice.setProductName(productPrices.get(0).getProduct().getName());
        comparePrice.setProductImageUrl(productPrices.get(0).getProduct().getImageUrl());
        comparePrice.setMinPrice(productPrices.get(0).getPrice());
        comparePrice.setShopNameMin(productPrices.get(0).getShopBranch().getShop().getName());
        comparePrice.setShopLogoImageUrl(productPrices.get(0).getShopBranch().getShop().getLogoImageUrl());
        comparePrice.setShopAddressMin(address);
        comparePrice.setOtherPrices(otherPrices);

        return comparePrice;
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
            List<CreateProductPriceDTO> rows = parseCsv(reader);
            List<ProductPrice> updateList = new ArrayList<>();
            List<ProductPrice> saveList = new ArrayList<>();

            rows.stream()
                    .map(row -> {
                        if (row.getProductId() == null || row.getShopBranchId() == null) {
                            return null;
                        }

                        Product product = productService.findByIdIfExists(row.getProductId());
                        if (product == null) {
                            return null;
                        }

                        ShopBranch shopBranch = shopBranchService.findByIdIfExists(row.getShopBranchId());
                        if (shopBranch == null) {
                            return null;
                        }

                        ProductPrice productPrice = productPriceMapper.createProductPriceDTOToProductPrice(row);
                        productPrice.setProduct(product);
                        productPrice.setShopBranch(shopBranch);
                        productPrice.setStartDate(LocalDate.now());
                        productPrice.setStatus(PriceStatus.ACTUAL);
                        return productPrice;
                    })
                    .filter(Objects::nonNull)
                    .forEach(productPrice -> {
                        ProductPrice presentProductPrice = findByProductIdAndShopBranchId(productPrice.getProduct().getId(),
                                productPrice.getShopBranch().getId());
                        if (presentProductPrice != null) {
                            productPrice.setId(presentProductPrice.getId());
                            updateList.add(productPrice);
                        } else {
                            saveList.add(productPrice);
                        }
                    });

            productPriceRepository.saveList(saveList);
            productPriceRepository.updateList(updateList);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ProductPrice findByProductIdAndShopBranchId(Long productId, Long shopBranchId) {
        return productPriceRepository.findByProductIdAndShopBranchId(productId, shopBranchId).orElse(null);
    }

    private List<CreateProductPriceDTO> parseCsv(Reader reader) {
            CSVParser csvParser = new CSVParserBuilder()
                    .withSeparator(';')
                    .withIgnoreQuotations(true)
                    .build();

            CSVReader csvReader = new CSVReaderBuilder(reader)
                    .withCSVParser(csvParser)
                    .build();

            CsvToBean<CreateProductPriceDTO> csvToBean =
                    new CsvToBeanBuilder<CreateProductPriceDTO>(csvReader)
                            .withType(CreateProductPriceDTO.class)
                            .withIgnoreEmptyLine(true)
                            .withIgnoreLeadingWhiteSpace(true)
                            .withOrderedResults(true)
                            .withThrowExceptions(false)
                            .build();

            List<CreateProductPriceDTO> rows = csvToBean.parse();

            List<CsvException> errors = csvToBean.getCapturedExceptions();

            if (!errors.isEmpty()) {
                throw new CsvImportException("CSV contains invalid rows: " + errors.size(), null);
            }
            return rows;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductPriceDTO> search(Long cityId, String searchQuery) {
        List<String> brandsNames = brandService.findAll().stream().map(BrandDTO::name).toList();
        List<String> categoryNames = productCategoryService.findAll().stream().map(ProductCategoryDTO::name).toList();

        ProductSearchRequest productSearchRequest = aiService.getProductSearchRequest(searchQuery, brandsNames, categoryNames);

        System.out.println(productSearchRequest.toString());
        if(productSearchRequest.productName() == null && productSearchRequest.brandName() == null && productSearchRequest.categoryName() == null) {
            return List.of();
        }

        List<ProductPriceDTO> productPrices = productPriceRepository.findByUserQuery(cityId, productSearchRequest.productName(),
                productSearchRequest.categoryName(), productSearchRequest.brandName(), productSearchRequest.description())
                .stream().map(productPriceMapper::productPriceToProductPriceDTO).collect(Collectors.toList());
        return productPrices;
    }
}
