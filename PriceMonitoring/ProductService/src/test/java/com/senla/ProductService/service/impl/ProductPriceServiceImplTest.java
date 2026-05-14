package com.senla.ProductService.service.impl;

import com.senla.ProductService.broker.KafkaBroker;
import com.senla.ProductService.dto.price.ComparePrice;
import com.senla.ProductService.dto.price.CreateUpdateProductPriceDTO;
import com.senla.ProductService.dto.price.ProductPriceDTO;
import com.senla.ProductService.dto.price.ProductPriceSearchDTO;
import com.senla.ProductService.dto.price.UpdateProductPrice;
import com.senla.ProductService.dto.product.ProductSearchRequest;
import com.senla.ProductService.dto.subscription.SubscriptionDetailsDTO;
import com.senla.ProductService.exception.CsvImportException;
import com.senla.ProductService.mapper.ProductPriceMapper;
import com.senla.ProductService.model.Brand;
import com.senla.ProductService.model.City;
import com.senla.ProductService.model.PriceHistory;
import com.senla.ProductService.model.Product;
import com.senla.ProductService.model.ProductCategory;
import com.senla.ProductService.model.ProductPrice;
import com.senla.ProductService.model.Shop;
import com.senla.ProductService.model.ShopBranch;
import com.senla.ProductService.model.Subscription;
import com.senla.ProductService.model.enums.PriceStatus;
import com.senla.ProductService.model.enums.ProductPriceSortType;
import com.senla.ProductService.repository.ProductPriceRepository;
import com.senla.ProductService.service.*;
import com.senla.ProductService.util.AIUtil;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.apache.tomcat.util.http.InvalidParameterException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductPriceServiceImplTest {

    @Mock
    private ProductPriceRepository productPriceRepository;
    @Mock
    private ProductPriceMapper productPriceMapper;
    @Mock
    private ProductService productService;
    @Mock
    private ShopBranchService shopBranchService;
    @Mock
    private BrandService brandService;
    @Mock
    private ProductCategoryService productCategoryService;
    @Mock
    private PriceHistoryService priceHistoryService;
    @Mock
    private SubscriptionService subscriptionService;
    @Mock
    private KafkaBroker kafkaBroker;
    @Mock
    private AIUtil aiUtil;

    @InjectMocks
    private ProductPriceServiceImpl productPriceService;

    private final UUID priceId = UUID.randomUUID();
    private final UUID productId = UUID.randomUUID();
    private final UUID shopBranchId = UUID.randomUUID();
    private final UUID cityId = UUID.randomUUID();
    private final UUID subscriptionId = UUID.randomUUID();

    private Product product;
    private ShopBranch shopBranch;
    private ProductPrice productPrice;
    private ProductPriceDTO productPriceDTO;
    private CreateUpdateProductPriceDTO createDTO;
    private ProductPriceSearchDTO searchDTO;
    private UpdateProductPrice updateProductPrice;

    @BeforeEach
    void setUp() {
        Brand brand = new Brand();
        brand.setId(UUID.randomUUID());
        brand.setName("brand");

        ProductCategory productCategory = new ProductCategory();
        productCategory.setId(UUID.randomUUID());
        productCategory.setName("productCategory");

        Shop shop = new Shop();
        shop.setName("shop");

        City city = new City();
        city.setName("city");

        product = new Product();
        product.setId(productId);
        product.setName("product");
        product.setDescription("description");
        product.setAmount(1.0);
        product.setUnit("unit");
        product.setBrand(brand);
        product.setProductCategory(productCategory);

        shopBranch = new ShopBranch();
        shopBranch.setId(shopBranchId);
        shopBranch.setStreet("street");
        shopBranch.setCity(city);
        shopBranch.setShop(shop);

        productPrice = new ProductPrice();
        productPrice.setId(priceId);
        productPrice.setProduct(product);
        productPrice.setShopBranch(shopBranch);
        productPrice.setPrice(1.00);
        productPrice.setDiscountPercent(0);
        productPrice.setStatus(PriceStatus.ACTUAL);
        productPrice.setStartDate(LocalDate.now());

        productPriceDTO = new ProductPriceDTO(
                priceId, productId, product.getName(), product.getDescription(), product.getAmount(), product.getUnit(),
                productCategory.getId(), productCategory.getName(), shopBranchId, shopBranch.getStreet(),
                shop.getName(), cityId, city.getName(), productPrice.getPrice(), LocalDate.now(),
                productPrice.getDiscountPercent(), productPrice.getStatus().getDisplayName()
        );

        createDTO = new CreateUpdateProductPriceDTO();
        createDTO.setProductId(productId);
        createDTO.setShopBranchId(shopBranchId);
        createDTO.setPrice(2.00);
        createDTO.setDiscountPercent(0);

        searchDTO = new ProductPriceSearchDTO(1, 10, shopBranchId,
                ProductPriceSortType.PRICE.getDisplayName(), true, null, null, PriceStatus.ACTUAL);

        updateProductPrice = new UpdateProductPrice(3.00, 10);
    }

    @Test
    void saveShouldThrowEntityExistsExceptionWhenPriceAlreadyExists() {
        when(productPriceRepository.findByProductIdAndShopBranchId(productId, shopBranchId))
                .thenReturn(Optional.ofNullable(productPrice));

        assertThrows(EntityExistsException.class, () -> productPriceService.save(createDTO));
    }

    @Test
    void saveShouldCallRepositorySaveMethod() {
        when(productPriceRepository.findByProductIdAndShopBranchId(productId, shopBranchId))
                .thenReturn(Optional.empty());
        when(productService.findByIdIfExists(productId)).thenReturn(product);
        when(shopBranchService.findByIdIfExists(shopBranchId)).thenReturn(shopBranch);
        when(productPriceMapper.createProductPriceDTOToProductPrice(createDTO)).thenReturn(productPrice);
        when(productPriceMapper.productPriceToProductPriceDTO(productPrice)).thenReturn(productPriceDTO);

        ProductPriceDTO result = productPriceService.save(createDTO);

        assertNotNull(result);
        verify(productPriceRepository).save(productPrice);
    }

    @Test
    void findByIdShouldReturnDTO() {
        when(productPriceRepository.findById(priceId)).thenReturn(Optional.of(productPrice));
        when(productPriceMapper.productPriceToProductPriceDTO(productPrice)).thenReturn(productPriceDTO);

        ProductPriceDTO result = productPriceService.findById(priceId);

        assertEquals(productPriceDTO, result);
    }

    @Test
    void findByIdShouldThrowEntityNotFoundException() {
        when(productPriceRepository.findById(priceId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> productPriceService.findById(priceId));
    }

    @Test
    void deleteShouldDeleteProductPrice() {
        when(productPriceRepository.findById(priceId)).thenReturn(Optional.of(productPrice));
        doNothing().when(productPriceRepository).delete(productPrice);

        productPriceService.delete(priceId);

        verify(productPriceRepository).delete(productPrice);
    }

    @Test
    void deleteShouldThrowEntityNotFoundException() {
        when(productPriceRepository.findById(priceId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> productPriceService.delete(priceId));
    }

    @Test
    void findAllWithPaginationShouldThrowInvalidParameterException() {
        ProductPriceSearchDTO invalid = new ProductPriceSearchDTO(1, 10, shopBranchId, "sort",
                true, null, null, PriceStatus.ACTUAL);

        assertThrows(InvalidParameterException.class, () -> productPriceService.findAllWithPagination(invalid));
    }

    @Test
    void findAllWithPaginationShouldReturnList() {
        when(productPriceRepository.findAllWithPagination(searchDTO)).thenReturn(List.of(productPrice));
        when(productPriceMapper.productPriceToProductPriceDTO(productPrice)).thenReturn(productPriceDTO);

        List<ProductPriceDTO> result = productPriceService.findAllWithPagination(searchDTO);

        assertEquals(1, result.size());
        assertEquals(productPriceDTO, result.get(0));
    }

    @Test
    void comparePricesInShopsShouldThrowEntityNotFoundException() {
        when(productPriceService.findProductInShops(productId, cityId)).thenReturn(List.of());

        assertThrows(EntityNotFoundException.class, () -> productPriceService.comparePricesInShops(productId, cityId));
    }

    @Test
    void comparePricesInShopsShouldReturnComparePrice() {
        when(productPriceService.findProductInShops(productId, cityId)).thenReturn(List.of(productPrice));

        ComparePrice result = productPriceService.comparePricesInShops(productId, cityId);

        assertNotNull(result);
    }

    @Test
    void importFromCsvShouldThrowInvalidParameterExceptionForInvalidExtension() throws IOException {
        ClassPathResource resource = new ClassPathResource("csv/invalid_extension.txt");
        MultipartFile file = new MockMultipartFile("file", resource.getFilename(),
                "text/plain", resource.getInputStream());

        assertThrows(InvalidParameterException.class, () -> productPriceService.importFromCsv(file));
    }

    @Test
    void importFromCsvShouldThrowInvalidParameterExceptionForEmptyFile() throws IOException {
        ClassPathResource resource = new ClassPathResource("csv/empty.csv");
        MultipartFile file = new MockMultipartFile("file", resource.getFilename(),
                "text/csv", resource.getInputStream());

        assertThrows(InvalidParameterException.class, () -> productPriceService.importFromCsv(file));
    }

    @Test
    void importFromCsvShouldProcessSuccessfully() throws IOException {
        ClassPathResource resource = new ClassPathResource("csv/prices.csv");
        MultipartFile file = new MockMultipartFile("file", resource.getFilename(),
                "text/csv", resource.getInputStream());

        when(productService.findAllById(anySet())).thenReturn(Map.of(productId, product));
        when(shopBranchService.findAllById(anySet())).thenReturn(Map.of(shopBranchId, shopBranch));
        doNothing().when(productPriceRepository).saveList(anyList());
        doNothing().when(productPriceRepository).updateList(anyList());
        doNothing().when(priceHistoryService).saveList(anyList());

        productPriceService.importFromCsv(file);

        verify(productPriceRepository).saveList(anyList());
    }

    @Test
    void importFromCsvShouldThrowCsvImportException() throws IOException {
        ClassPathResource resource = new ClassPathResource("csv/invalid.csv");
        MultipartFile file = new MockMultipartFile("file", resource.getFilename(), "text/csv", resource.getInputStream());

        assertThrows(CsvImportException.class, () -> productPriceService.importFromCsv(file));
    }

    @Test
    void findByProductIdAndShopBranchIdShouldReturnProductPrice() {
        when(productPriceRepository.findByProductIdAndShopBranchId(productId, shopBranchId))
                .thenReturn(Optional.ofNullable(productPrice));

        ProductPrice result = productPriceService.findByProductIdAndShopBranchId(productId, shopBranchId);

        assertEquals(productPrice, result);
    }

    @Test
    void findByProductIdAndShopBranchIdShouldReturnNull() {
        when(productPriceRepository.findByProductIdAndShopBranchId(productId, shopBranchId))
                .thenReturn(Optional.empty());

        ProductPrice result = productPriceService.findByProductIdAndShopBranchId(productId, shopBranchId);

        assertNull(result);
    }

    @Test
    void searchShouldReturnEmptyList() {
        when(brandService.findAll()).thenReturn(List.of());
        when(productCategoryService.findAll()).thenReturn(List.of());
        when(aiUtil.getProductSearchRequest(anyString(), anyList(), anyList()))
                .thenReturn(new ProductSearchRequest(null, null, null, null));

        List<ProductPriceDTO> result = productPriceService.search(cityId, "query");

        assertTrue(result.isEmpty());
    }

    @Test
    void searchShouldReturnResults() {
        ProductSearchRequest request = new ProductSearchRequest("productCategory", null,
                null, null);

        when(brandService.findAll()).thenReturn(List.of());
        when(productCategoryService.findAll()).thenReturn(List.of());
        when(aiUtil.getProductSearchRequest(anyString(), anyList(), anyList())).thenReturn(request);
        when(productPriceRepository.findByUserQuery(any(), any(), any(), any(), any())).thenReturn(List.of(productPrice));
        when(productPriceMapper.productPriceToProductPriceDTO(productPrice)).thenReturn(productPriceDTO);

        List<ProductPriceDTO> result = productPriceService.search(cityId, "query");

        assertEquals(1, result.size());
    }

    @Test
    void findByIdIfExistsShouldThrowEntityNotFoundException() {
        when(productPriceRepository.findById(priceId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> productPriceService.findByIdIfExists(priceId));
    }

    @Test
    void updateShouldUpdatePriceAndCreateHistory() {
        when(productPriceRepository.findById(priceId)).thenReturn(Optional.of(productPrice));
        when(productPriceMapper.productPriceToProductPriceDTO(productPrice)).thenReturn(productPriceDTO);

        ProductPriceDTO result = productPriceService.update(priceId, createDTO);

        assertNotNull(result);
        verify(priceHistoryService).save(any(PriceHistory.class));
        verify(productPriceRepository).update(productPrice);
    }

    @Test
    void subscribeShouldCreateSubscription() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getDetails()).thenReturn(UUID.randomUUID());
        SecurityContext securityContext = mock(SecurityContext.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);

        when(productPriceRepository.findByIdWithFetch(priceId)).thenReturn(Optional.of(productPrice));

        productPriceService.subscribe(priceId);

        verify(subscriptionService).save(any(Subscription.class));

        SecurityContextHolder.clearContext();
    }

    @Test
    void subscribeShouldThrowEntityNotFoundException() {
        when(productPriceRepository.findByIdWithFetch(priceId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> productPriceService.subscribe(priceId));
    }

    @Test
    void createRequestShouldCreateNewRequest() {
        when(productPriceRepository.findById(priceId)).thenReturn(Optional.ofNullable(productPrice));

        productPriceService.createRequest(priceId, updateProductPrice);

        verify(productPriceRepository).save(any(ProductPrice.class));
    }

    @Test
    void acceptRequestShouldUpdateExistingPriceAndDeleteRequest() {
        ProductPrice requestPrice = new ProductPrice();
        requestPrice.setId(priceId);
        requestPrice.setProduct(product);
        requestPrice.setShopBranch(shopBranch);
        requestPrice.setPrice(100.00);
        requestPrice.setDiscountPercent(10);

        productPrice.setDiscountPercent(15);

        when(productPriceRepository.findByIdWithFetch(priceId)).thenReturn(Optional.of(requestPrice));
        when(productPriceRepository.findByProductIdAndShopBranchIdAndStatus(any(), any()))
                .thenReturn(productPrice);
        when(productPriceMapper.productPriceToProductPriceDTO(requestPrice)).thenReturn(productPriceDTO);

        ProductPriceDTO result = productPriceService.acceptRequest(priceId, "ACTUAL");

        assertNotNull(result);
        verify(productPriceRepository).delete(requestPrice);
        verify(productPriceRepository).update(productPrice);
        verify(kafkaBroker, never()).sendUpdateProductPriceMessage(any(), any());
    }

    @Test
    void acceptRequestShouldThrowInvalidParameterException() {
        assertThrows(InvalidParameterException.class, () -> productPriceService.acceptRequest(priceId, "status"));
    }

    @Test
    void acceptRequestShouldThrowEntityNotFoundException() {
        when(productPriceRepository.findByIdWithFetch(priceId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> productPriceService.acceptRequest(priceId, "ACTUAL"));
    }

    @Test
    void acceptRequestShouldSendKafkaMessageWhenDiscountIncreased() {
        ProductPrice requestPrice = new ProductPrice();
        requestPrice.setId(priceId);
        requestPrice.setProduct(product);
        requestPrice.setShopBranch(shopBranch);
        requestPrice.setPrice(100.00);
        requestPrice.setDiscountPercent(100);

        Subscription subscription = new Subscription();

        when(subscriptionService.findByProductPriceId(priceId)).thenReturn(List.of(subscription));
        when(productPriceRepository.findByIdWithFetch(priceId)).thenReturn(Optional.of(requestPrice));
        when(productPriceRepository.findByProductIdAndShopBranchIdAndStatus(productId, shopBranchId))
                .thenReturn(productPrice);
        when(productPriceMapper.productPriceToProductPriceDTO(requestPrice)).thenReturn(productPriceDTO);

        productPriceService.acceptRequest(priceId, "ACTUAL");

        verify(kafkaBroker).sendUpdateProductPriceMessage(any(), anyString());
    }

    @Test
    void findSubscriptionByIdWithDetailsShouldReturnSubscriptionDetailsDTO() {
        Subscription subscription = new Subscription();
        subscription.setId(subscriptionId);
        subscription.setProductPrice(productPrice);

        when(subscriptionService.findByIdWithFetch(subscriptionId)).thenReturn(subscription);
        when(productPriceService.findProductInShops(any(), any())).thenReturn(List.of(productPrice));

        SubscriptionDetailsDTO result = productPriceService.findSubscriptionByIdWithDetails(subscriptionId);

        assertNotNull(result);
        assertEquals(subscriptionId, result.getId());
    }

    @Test
    void findProductInShopsShouldReturnList() {
        List<ProductPrice> prices = List.of(productPrice);

        when(productPriceRepository.findProductInShops(productId, cityId)).thenReturn(prices);

        List<ProductPrice> result = productPriceService.findProductInShops(productId, cityId);

        assertEquals(prices, result);
        verify(productPriceRepository).findProductInShops(productId, cityId);
    }

    @Test
    void findByIdOrNullShouldReturnProductPrice() {
        when(productPriceRepository.findById(priceId)).thenReturn(Optional.of(productPrice));

        ProductPrice result = productPriceService.findByIdOrNull(priceId);

        assertEquals(productPrice, result);
    }

    @Test
    void findByIdOrNullShouldReturnNull() {
        when(productPriceRepository.findById(priceId)).thenReturn(Optional.empty());

        ProductPrice result = productPriceService.findByIdOrNull(priceId);

        assertNull(result);
    }

    @Test
    void buildProductPriceFromRequestShouldBuild() {
        when(productPriceRepository.findById(priceId)).thenReturn(Optional.ofNullable(productPrice));

        ProductPrice result = productPriceService.buildProductPriceFromRequest(priceId, updateProductPrice);

        assertNotNull(result);
        assertEquals(product, result.getProduct());
        assertEquals(shopBranch, result.getShopBranch());
        assertEquals(PriceStatus.ON_REVIEW, result.getStatus());
        assertEquals(3.00, result.getPrice());
        assertEquals(10, result.getDiscountPercent());
    }
}