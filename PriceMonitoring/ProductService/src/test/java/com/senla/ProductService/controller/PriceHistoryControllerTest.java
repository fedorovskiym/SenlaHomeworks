package com.senla.ProductService.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.senla.ProductService.dto.history.PriceHistoryDTO;
import com.senla.ProductService.dto.history.PriceHistoryDataDTO;
import com.senla.ProductService.dto.history.PriceHistoryOverPeriodOfTimeDTO;
import com.senla.ProductService.service.PriceHistoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PriceHistoryControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PriceHistoryService priceHistoryService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private UUID productId;
    private UUID shopBranchId;

    private PriceHistoryDTO priceHistoryDTO;
    private PriceHistoryOverPeriodOfTimeDTO periodOfTimeDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        PriceHistoryController priceHistoryController = new PriceHistoryController(priceHistoryService);
        mockMvc = MockMvcBuilders.standaloneSetup(priceHistoryController).build();

        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.findAndRegisterModules();

        productId = UUID.randomUUID();
        shopBranchId = UUID.randomUUID();

        PriceHistoryDataDTO historyDataDTO = new PriceHistoryDataDTO(1.00, 2.00, LocalDate.now());

        priceHistoryDTO = new PriceHistoryDTO(UUID.randomUUID(), productId, "Product",
                shopBranchId, "Shop", List.of(historyDataDTO));

        periodOfTimeDTO = new PriceHistoryOverPeriodOfTimeDTO(productId, shopBranchId,
                LocalDate.now().minusDays(1), LocalDate.now()
        );
    }

    @Test
    void getPriceHistoryShouldReturnPriceHistoryDTO() throws Exception {
        when(priceHistoryService.getCoordsForChart(productId, shopBranchId)).thenReturn(priceHistoryDTO);

        mockMvc.perform(get("/history/chart")
                        .param("productId", productId.toString())
                        .param("shopBranchId", shopBranchId.toString()))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    PriceHistoryDTO response = objectMapper.readValue(jsonResponse, PriceHistoryDTO.class);
                    assertEquals(priceHistoryDTO.productId(), response.productId());
                    assertEquals(priceHistoryDTO.productName(), response.productName());
                    assertEquals(1, response.prices().size());
                });

        verify(priceHistoryService).getCoordsForChart(productId, shopBranchId);
    }

    @Test
    void exportPriceHistoryInTableShouldReturnCsvFile() throws Exception {
        String csv = """
                date,price
                2025-01-01,1
                2025-01-02,2
                """;

        when(priceHistoryService.generateCsv(periodOfTimeDTO)).thenReturn(csv);

        mockMvc.perform(post("/history/export")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(periodOfTimeDTO)))
                .andExpect(status().isOk())
                .andExpect(header().string(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"price_history.csv\""))
                .andExpect(content().string(csv));

        verify(priceHistoryService).generateCsv(periodOfTimeDTO);
    }

    @Test
    void exportPriceHistoryInTableShouldReturnNoContent() throws Exception {
        when(priceHistoryService.generateCsv(periodOfTimeDTO)).thenReturn("");

        mockMvc.perform(post("/history/export")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(periodOfTimeDTO)))
                .andExpect(status().isNoContent())
                .andExpect(content().string("No data found"));

        verify(priceHistoryService).generateCsv(periodOfTimeDTO);
    }
}