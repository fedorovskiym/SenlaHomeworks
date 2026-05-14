package com.senla.ProductService.service.impl;

import com.senla.ProductService.dto.history.PriceHistoryDTO;
import com.senla.ProductService.dto.history.PriceHistoryOverPeriodOfTimeDTO;
import com.senla.ProductService.model.PriceHistory;
import com.senla.ProductService.model.Product;
import com.senla.ProductService.model.Shop;
import com.senla.ProductService.model.ShopBranch;
import com.senla.ProductService.repository.PriceHistoryRepository;
import org.apache.tomcat.util.http.InvalidParameterException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PriceHistoryServiceImplTest {

    @Mock
    private PriceHistoryRepository priceHistoryRepository;

    @InjectMocks
    private PriceHistoryServiceImpl priceHistoryService;

    private PriceHistory priceHistory;
    private Product product;

    private final UUID productId = UUID.randomUUID();
    private final UUID shopBranchId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(productId);
        product.setName("product");

        Shop shop = new Shop();
        shop.setName("shop");

        ShopBranch shopBranch = new ShopBranch();
        shopBranch.setId(shopBranchId);
        shopBranch.setShop(shop);

        priceHistory = new PriceHistory();
        priceHistory.setId(UUID.randomUUID());
        priceHistory.setProduct(product);
        priceHistory.setShopBranch(shopBranch);
        priceHistory.setOldPrice(1.00);
        priceHistory.setNewPrice(2.00);
        priceHistory.setChangeDate(LocalDate.of(2025, 1, 1));
    }

    @Test
    void saveShouldCallRepositorySave() {
        doNothing().when(priceHistoryRepository).save(priceHistory);

        priceHistoryService.save(priceHistory);

        verify(priceHistoryRepository).save(priceHistory);
    }

    @Test
    void saveListShouldSaveAllItems() {
        List<PriceHistory> list = List.of(priceHistory);

        doNothing().when(priceHistoryRepository).save(any(PriceHistory.class));

        priceHistoryService.saveList(list);

        verify(priceHistoryRepository, times(1)).save(priceHistory);
    }

    @Test
    void getCoordsForChartShouldReturnEmptyDTO() {
        when(priceHistoryRepository.findAllByProductIdAndShopBranchId(productId, shopBranchId)).thenReturn(List.of());

        PriceHistoryDTO result = priceHistoryService.getCoordsForChart(productId, shopBranchId);

        assertNotNull(result);
        assertEquals(productId, result.productId());
        assertEquals(shopBranchId, result.shopBranchId());
    }

    @Test
    void getCoordsForChartShouldReturnDTO() {
        when(priceHistoryRepository.findAllByProductIdAndShopBranchId(productId, shopBranchId))
                .thenReturn(List.of(priceHistory));

        PriceHistoryDTO result = priceHistoryService.getCoordsForChart(productId, shopBranchId);

        assertNotNull(result);
        assertEquals(productId, result.productId());
        assertEquals(shopBranchId, result.shopBranchId());
        assertEquals(product.getName(), result.productName());
        assertEquals(1, result.prices().size());
    }

    @Test
    void generateCsvShouldThrowInvalidParameterException() {
        PriceHistoryOverPeriodOfTimeDTO dto = new PriceHistoryOverPeriodOfTimeDTO(productId, shopBranchId,
                LocalDate.of(2025, 1, 10),
                LocalDate.of(2025, 1, 1));

        assertThrows(InvalidParameterException.class, () -> priceHistoryService.generateCsv(dto));
    }

    @Test
    void generateCsvShouldReturnEmptyString() {
        PriceHistoryOverPeriodOfTimeDTO dto = new PriceHistoryOverPeriodOfTimeDTO(productId, shopBranchId,
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 12, 31));

        when(priceHistoryRepository.findOverPeriodOfTime(dto.productId(), dto.shopBranchId(),
                dto.startDate(), dto.endDate())).thenReturn(List.of());

        String result = priceHistoryService.generateCsv(dto);

        assertEquals("", result);
    }

    @Test
    void generateCsvShouldReturnCsvString() {
        PriceHistoryOverPeriodOfTimeDTO dto = new PriceHistoryOverPeriodOfTimeDTO(productId, shopBranchId,
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 12, 31));

        when(priceHistoryRepository.findOverPeriodOfTime(dto.productId(), dto.shopBranchId(),
                dto.startDate(), dto.endDate())).thenReturn(List.of(priceHistory));

        String result = priceHistoryService.generateCsv(dto);

        assertNotNull(result);

        assertTrue(result.contains("id;oldPrice;newPrice;changeDate;"));
        assertTrue(result.contains("1,00"));
        assertTrue(result.contains("2,00"));
    }
}