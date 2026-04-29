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
import com.senla.ProductService.dto.price.CreateUpdateProductPriceDTO;
import com.senla.ProductService.dto.price.PriceDTO;
import com.senla.ProductService.dto.price.ProductPriceDTO;
import com.senla.ProductService.dto.price.ProductPriceSearchDTO;
import com.senla.ProductService.dto.product.ProductSearchRequest;
import com.senla.ProductService.dto.productCategory.ProductCategoryDTO;
import com.senla.ProductService.exception.CsvImportException;
import com.senla.ProductService.mapper.ProductPriceMapper;
import com.senla.ProductService.model.PriceHistory;
import com.senla.ProductService.model.Product;
import com.senla.ProductService.model.ProductPrice;
import com.senla.ProductService.model.ShopBranch;
import com.senla.ProductService.model.enums.PriceStatus;
import com.senla.ProductService.model.enums.ProductPriceSortType;
import com.senla.ProductService.repository.ProductPriceRepository;
import com.senla.ProductService.service.AIService;
import com.senla.ProductService.service.BrandService;
import com.senla.ProductService.service.PriceHistoryService;
import com.senla.ProductService.service.ProductCategoryService;
import com.senla.ProductService.service.ProductPriceService;
import com.senla.ProductService.service.ProductService;
import com.senla.ProductService.service.ShopBranchService;
import jakarta.persistence.EntityExistsException;
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
    private final PriceHistoryService priceHistoryService;
    private static final Logger logger = LoggerFactory.getLogger(ProductPriceServiceImpl.class);

    @Autowired
    public ProductPriceServiceImpl(ProductPriceRepository productPriceRepository, ProductPriceMapper productPriceMapper, ProductService productService, ShopBranchService shopBranchService, AIService aiService, BrandService brandService, ProductCategoryService productCategoryService, PriceHistoryService priceHistoryService) {
        this.productPriceRepository = productPriceRepository;
        this.productPriceMapper = productPriceMapper;
        this.productService = productService;
        this.shopBranchService = shopBranchService;
        this.aiService = aiService;
        this.brandService = brandService;
        this.productCategoryService = productCategoryService;
        this.priceHistoryService = priceHistoryService;
    }

    @Override
    @Transactional
    public void save(CreateUpdateProductPriceDTO createProductPriceDTO) {
        logger.info("Saving product price {}", createProductPriceDTO);
        if (findByProductIdAndShopBranchId(createProductPriceDTO.getProductId(), createProductPriceDTO.getShopBranchId()) != null) {
            logger.warn("Product price with product id {} and shop branch id {} already exists", createProductPriceDTO.getProductId(), createProductPriceDTO.getShopBranchId());
            throw new EntityExistsException("Price with product id " + createProductPriceDTO.getProductId() +
                    " in shop branch id " + createProductPriceDTO.getShopBranchId() + " already exists");
        }
        Product product = productService.findByIdIfExists(createProductPriceDTO.getProductId());
        ShopBranch shopBranch = shopBranchService.findByIdIfExists(createProductPriceDTO.getShopBranchId());

        ProductPrice productPrice = productPriceMapper.createProductPriceDTOToProductPrice(createProductPriceDTO);

        productPrice.setProduct(product);
        productPrice.setShopBranch(shopBranch);
        productPrice.setStartDate(LocalDate.now());
        productPrice.setStatus(PriceStatus.ACTUAL);

        productPriceRepository.save(productPrice);
        logger.info("Saved product price {}", productPrice);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductPriceDTO findById(Long id) {
        logger.info("Finding product price dto by id {}", id);
        return productPriceMapper.productPriceToProductPriceDTO(findByIdIfExists(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductPriceDTO> findAllWithPagination(ProductPriceSearchDTO productPriceSearchDTO) {
        logger.info("Finding product price with pagination and filters {}", productPriceSearchDTO);
        if (!productPriceSearchDTO.sortBy().equals(ProductPriceSortType.PRICE.getDisplayName()) &&
                !productPriceSearchDTO.sortBy().equals(ProductPriceSortType.DISCOUNT_PERCENT.getDisplayName()) &&
                !productPriceSearchDTO.sortBy().equals(ProductPriceSortType.ID.getDisplayName())) {
            logger.warn("Wrong sort parameters for filters {}", productPriceSearchDTO);
            throw new InvalidParameterException("Sort only by price, discountPercent or id");
        }

        logger.info("Find product price with pagination and filters {}", productPriceSearchDTO);
        return productPriceRepository.findAllWithPagination(productPriceSearchDTO.page(), productPriceSearchDTO.size(),
                        productPriceSearchDTO.shopBranchId(), productPriceSearchDTO.sortBy(), productPriceSearchDTO.asc(),
                        productPriceSearchDTO.brandId(), productPriceSearchDTO.categoryId())
                .stream().map(productPriceMapper::productPriceToProductPriceDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ComparePrice comparePricesInShops(Long productId, Long cityId) {
        logger.info("Compare price on product with id {} in city with id {}", productId, cityId);
        List<ProductPrice> productPrices = productPriceRepository.findProductInShops(productId, cityId);

        if (productPrices.isEmpty()) {
            logger.warn("Product with id {} has no products in shops", productId);
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
        logger.info("Compare price {}", comparePrice);
        return comparePrice;
    }

    @Override
    @Transactional
    public void importFromCsv(MultipartFile file) {
        logger.info("Importing product price from csv file {}", file.getOriginalFilename());
        if (!file.getOriginalFilename().endsWith("csv")) {
            logger.warn("Invalid file extension {}", file.getOriginalFilename());
            throw new InvalidParameterException("Only .csv files supported!");
        }
        if (file.isEmpty()) {
            logger.warn("Empty product price file");
            throw new InvalidParameterException("Empty file!");
        }

        try (Reader reader = new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8)) {
            List<CreateUpdateProductPriceDTO> rows = parseCsv(reader);
            List<PriceHistory> priceHistoryList = new ArrayList<>();
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
                            if (!productPrice.getPrice().equals(presentProductPrice.getPrice())) {
                                productPrice.setId(presentProductPrice.getId());
                                PriceHistory priceHistory = buildPriceHistory(presentProductPrice, productPrice.getPrice());
                                priceHistoryList.add(priceHistory);
                                updateList.add(productPrice);
                            }
                        } else {
                            saveList.add(productPrice);
                        }
                    });
            logger.info("Import from file end");
            productPriceRepository.saveList(saveList);
            productPriceRepository.updateList(updateList);
            priceHistoryService.saveList(priceHistoryList);
        } catch (IOException e) {
            logger.error("Error while reading file {}", file.getOriginalFilename(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ProductPrice findByProductIdAndShopBranchId(Long productId, Long shopBranchId) {
        logger.info("Find price by product id {} and shop branch id {} or null", productId, shopBranchId);
        return productPriceRepository.findByProductIdAndShopBranchId(productId, shopBranchId).orElse(null);
    }

    private List<CreateUpdateProductPriceDTO> parseCsv(Reader reader) {
        logger.info("Parse csv file");
        CSVParser csvParser = new CSVParserBuilder()
                .withSeparator(';')
                .withIgnoreQuotations(true)
                .build();

        CSVReader csvReader = new CSVReaderBuilder(reader)
                .withCSVParser(csvParser)
                .build();

        CsvToBean<CreateUpdateProductPriceDTO> csvToBean =
                new CsvToBeanBuilder<CreateUpdateProductPriceDTO>(csvReader)
                        .withType(CreateUpdateProductPriceDTO.class)
                        .withIgnoreEmptyLine(true)
                        .withIgnoreLeadingWhiteSpace(true)
                        .withOrderedResults(true)
                        .withThrowExceptions(false)
                        .build();

        List<CreateUpdateProductPriceDTO> rows = csvToBean.parse();

        List<CsvException> errors = csvToBean.getCapturedExceptions();
        logger.info("Parse csv file end");
        if (!errors.isEmpty()) {
            logger.error("{} errors while parsing csv file", errors.size());
            throw new CsvImportException("CSV contains invalid rows: " + errors.size(), null);
        }
        return rows;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductPriceDTO> search(Long cityId, String searchQuery) {
        logger.info("Searching price by city id {} with query {}", cityId, searchQuery);
        List<String> brandsNames = brandService.findAll().stream().map(BrandDTO::name).toList();
        List<String> categoryNames = productCategoryService.findAll().stream().map(ProductCategoryDTO::name).toList();

        ProductSearchRequest productSearchRequest = aiService.getProductSearchRequest(searchQuery, brandsNames, categoryNames);

        System.out.println(productSearchRequest.toString());
        if (productSearchRequest.productName() == null && productSearchRequest.brandName() == null && productSearchRequest.categoryName() == null) {
            logger.info("Search types are null");
            return List.of();
        }

        List<ProductPriceDTO> productPrices = productPriceRepository.findByUserQuery(cityId, productSearchRequest.productName(),
                        productSearchRequest.categoryName(), productSearchRequest.brandName(), productSearchRequest.description())
                .stream().map(productPriceMapper::productPriceToProductPriceDTO).collect(Collectors.toList());
        logger.info("Succesfull search price by city id {} with query {}", cityId, searchQuery);
        return productPrices;
    }

    @Override
    @Transactional(readOnly = true)
    public ProductPrice findByIdIfExists(Long id) {
        logger.info("Find price by id {}", id);
        return productPriceRepository.findById(id).orElseThrow(() -> {
            logger.warn("Product price with id {} not found", id);
            return new EntityNotFoundException("Product's price with id - " + id + " not found!");
        });
    }

    @Override
    @Transactional
    public void update(Long id, CreateUpdateProductPriceDTO createProductPriceDTO) {
        logger.info("Update price by id {}", id);
        ProductPrice productPrice = findByIdIfExists(id);

        PriceHistory priceHistory = buildPriceHistory(productPrice, createProductPriceDTO.getPrice());
        productPrice.setDiscountPercent(createProductPriceDTO.getDiscountPercent());
        productPrice.setPrice(createProductPriceDTO.getPrice());

        priceHistoryService.save(priceHistory);
        productPriceRepository.update(productPrice);
        logger.info("Successfull updated price {}", productPrice);
    }

    private PriceHistory buildPriceHistory(ProductPrice productPrice, Double newPrice) {
        PriceHistory priceHistory = new PriceHistory();
        priceHistory.setProduct(productPrice.getProduct());
        priceHistory.setShopBranch(productPrice.getShopBranch());
        priceHistory.setOldPrice(productPrice.getPrice());
        priceHistory.setNewPrice(newPrice);
        priceHistory.setChangeDate(LocalDate.now());
        return priceHistory;
    }

}
