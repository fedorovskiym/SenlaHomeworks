package com.senla.ProductService.service.impl;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.exceptions.CsvException;
import com.senla.ProductService.broker.KafkaBroker;
import com.senla.ProductService.dto.price.UpdateProductPriceMessage;
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
import com.senla.ProductService.model.Subscription;
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
import com.senla.ProductService.service.SubscriptionService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.apache.tomcat.util.http.InvalidParameterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
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
    private final SubscriptionService subscriptionService;
    private final KafkaBroker kafkaBroker;
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(ProductPriceServiceImpl.class);

    @Autowired
    public ProductPriceServiceImpl(ProductPriceRepository productPriceRepository, ProductPriceMapper productPriceMapper,
                                   ShopBranchService shopBranchService, AIService aiService,
                                   BrandService brandService, ProductCategoryService productCategoryService,
                                   PriceHistoryService priceHistoryService, SubscriptionService subscriptionService,
                                   KafkaBroker kafkaBroker, ProductService productService) {
        this.productPriceRepository = productPriceRepository;
        this.productPriceMapper = productPriceMapper;
        this.productService = productService;
        this.shopBranchService = shopBranchService;
        this.aiService = aiService;
        this.brandService = brandService;
        this.productCategoryService = productCategoryService;
        this.priceHistoryService = priceHistoryService;
        this.subscriptionService = subscriptionService;
        this.kafkaBroker = kafkaBroker;
    }

    @Override
    @Transactional
    public ProductPriceDTO save(CreateUpdateProductPriceDTO createProductPriceDTO) {
        logger.info("Saving product price {}", createProductPriceDTO);
        if (findByProductIdAndShopBranchId(createProductPriceDTO.getProductId(), createProductPriceDTO.getShopBranchId()) != null) {
            logger.warn("Product price with product id {} and shop branch id {} already exists",
                    createProductPriceDTO.getProductId(), createProductPriceDTO.getShopBranchId());
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
        return productPriceMapper.productPriceToProductPriceDTO(productPrice);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductPriceDTO findById(UUID id) {
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
    public ComparePrice comparePricesInShops(UUID productId, UUID cityId) {
        logger.info("Compare price on product with id {} in city with id {}", productId, cityId);

        List<ProductPrice> productPrices = findProductInShops(productId, cityId);
        if (productPrices.isEmpty()) {
            logger.warn("Product with id {} has no products in shops", productId);
            throw new EntityNotFoundException("No prices found in shops with id - " + productId);
        }

        String address = buildAddress(productPrices);

        List<PriceDTO> otherPrices = buildOtherPrices(productPrices);

        ComparePrice comparePrice = buildComparePrice(productId, address, productPrices, otherPrices);
        logger.info("Compare price build");
        return comparePrice;
    }


    @Override
    @Transactional(readOnly = true)
    public List<PriceDTO> buildOtherPrices(List<ProductPrice> productPrices) {
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

        return otherPrices;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductPrice> findProductInShops(UUID productId, UUID cityId) {
        return productPriceRepository.findProductInShops(productId, cityId);
    }

    @Override
    @Transactional
    public void importFromCsv(MultipartFile file) {
        logger.info("Importing product price from csv file {}", file.getOriginalFilename());
        validateFile(file);

        try (Reader reader = new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8)) {
            List<PriceHistory> priceHistoryList = new ArrayList<>();
            List<ProductPrice> updateList = new ArrayList<>();
            List<ProductPrice> saveList = new ArrayList<>();

            List<CreateUpdateProductPriceDTO> rows = parseCsv(reader);
            Set<UUID> listProductId = rows.stream()
                    .map(CreateUpdateProductPriceDTO::getProductId).collect(Collectors.toSet());
            Set<UUID> listShopBranchId = rows.stream()
                    .map(CreateUpdateProductPriceDTO::getShopBranchId).collect(Collectors.toSet());
            Set<UUID> listProductPriceId = rows.stream()
                    .map(CreateUpdateProductPriceDTO::getId).collect(Collectors.toSet());
            Map<UUID, Product> productMap = productService.findAllById(listProductId);
            Map<UUID, ShopBranch> shopBranchMap = shopBranchService.findAllById(listShopBranchId);
            Map<UUID, ProductPrice> productPriceMap = getProductPriceMap(listProductPriceId);

            for (CreateUpdateProductPriceDTO row : rows) {
                Product product = productMap.get(row.getProductId());
                if (product == null) {
                    continue;
                }

                ShopBranch shopBranch = shopBranchMap.get(row.getShopBranchId());
                if (shopBranch == null) {
                    continue;
                }

                ProductPrice existingProductPrice = productPriceMap.get(row.getProductId());

                if (existingProductPrice == null) {
                    ProductPrice productPrice = buildProductPrice(row, product, shopBranch);
                    saveList.add(productPrice);
                } else {
                    PriceHistory priceHistory = buildPriceHistory(existingProductPrice, row.getPrice());
                    priceHistoryList.add(priceHistory);

                    if (row.getDiscountPercent() > existingProductPrice.getDiscountPercent()) {
                        sendUpdatePriceMessage(existingProductPrice.getId(), row, existingProductPrice);
                    }

                    existingProductPrice.setPrice(row.getPrice());
                    existingProductPrice.setDiscountPercent(row.getDiscountPercent());
                    existingProductPrice.setStartDate(LocalDate.now());
                    updateList.add(existingProductPrice);
                }
            }
            logger.info("Import from file end");
            productPriceRepository.saveList(saveList);
            productPriceRepository.updateList(updateList);
            priceHistoryService.saveList(priceHistoryList);
        } catch (IOException e) {
            logger.error("Error while reading file {}", file.getOriginalFilename(), e);
            throw new CsvImportException("Error while reading file " + file.getOriginalFilename(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ProductPrice findByProductIdAndShopBranchId(UUID productId, UUID shopBranchId) {
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
    public List<ProductPriceDTO> search(UUID cityId, String searchQuery) {
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
    public ProductPrice findByIdIfExists(UUID id) {
        logger.info("Find price by id {}", id);
        return productPriceRepository.findById(id).orElseThrow(() -> {
            logger.warn("Product price with id {} not found", id);
            return new EntityNotFoundException("Product's price with id - " + id + " not found!");
        });
    }

    @Override
    @Transactional
    public ProductPriceDTO update(UUID id, CreateUpdateProductPriceDTO createProductPriceDTO) {
        logger.info("Update price by id {}", id);
        ProductPrice productPrice = findByIdIfExists(id);

        if (productPrice.getPrice() > createProductPriceDTO.getPrice()) {
            sendUpdatePriceMessage(id, createProductPriceDTO, productPrice);
        }

        PriceHistory priceHistory = buildPriceHistory(productPrice, createProductPriceDTO.getPrice());
        productPrice.setDiscountPercent(createProductPriceDTO.getDiscountPercent());
        productPrice.setPrice(createProductPriceDTO.getPrice());

        priceHistoryService.save(priceHistory);
        productPriceRepository.update(productPrice);
        logger.info("Successfull updated price {}", productPrice);
        return productPriceMapper.productPriceToProductPriceDTO(productPrice);
    }

    @Override
    @Transactional
    public void subscribe(UUID id) {
        ProductPrice productPrice = productPriceRepository.findByIdWithFetch(id).orElseThrow(() -> {
            logger.warn("Product price with id {} not found", id);
            return new EntityNotFoundException("Product price with id - " + id + " not found!");
        });
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = (UUID) authentication.getDetails();

        Subscription subscription = buildSubscription(productPrice, userId);
        subscriptionService.save(subscription);
    }

    @Transactional(readOnly = true)
    protected Map<UUID, ProductPrice> getProductPriceMap(Set<UUID> listProductPriceId) {
        return productPriceRepository.findAllById(listProductPriceId);
    }

    private String buildAddress(List<ProductPrice> productPrices) {
        return String.format("%s %s %s %s", productPrices.get(0).getShopBranch().getCity().getName(),
                productPrices.get(0).getShopBranch().getStreet(), productPrices.get(0).getShopBranch().getHouse(),
                productPrices.get(0).getShopBranch().getRoom());
    }


    private ComparePrice buildComparePrice(UUID productId, String address,
                                           List<ProductPrice> productPrices, List<PriceDTO> otherPrices) {
        ComparePrice comparePrice = new ComparePrice();
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

    private void sendUpdatePriceMessage(UUID id, CreateUpdateProductPriceDTO createProductPriceDTO, ProductPrice productPrice) {
        List<Subscription> subscriptions = subscriptionService.findByProductPriceId(id);
        subscriptions.forEach(subscription -> {
            UpdateProductPriceMessage updateProductPriceMessage = new UpdateProductPriceMessage(id, productPrice.getProduct().getName(),
                    createProductPriceDTO.getPrice(), createProductPriceDTO.getDiscountPercent(), subscription.getUserId());
            String json = objectMapper.writeValueAsString(updateProductPriceMessage);
            logger.info("Sending update price message {} to kafka for user with id {}", json, subscription.getUserId());
            kafkaBroker.sendUpdateProductPriceMessage(id, json);
        });
    }

    private ProductPrice buildProductPrice(CreateUpdateProductPriceDTO row, Product product, ShopBranch shopBranch) {
        ProductPrice productPrice = productPriceMapper.createProductPriceDTOToProductPrice(row);
        productPrice.setId(null);
        productPrice.setProduct(product);
        productPrice.setShopBranch(shopBranch);
        productPrice.setStartDate(LocalDate.now());
        productPrice.setStatus(PriceStatus.ACTUAL);
        return productPrice;
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

    private Subscription buildSubscription(ProductPrice productPrice, UUID userId) {
        Subscription subscription = new Subscription();
        subscription.setId(UUID.randomUUID());
        subscription.setUserId(userId);
        subscription.setProductPrice(productPrice);
        return subscription;
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
}
