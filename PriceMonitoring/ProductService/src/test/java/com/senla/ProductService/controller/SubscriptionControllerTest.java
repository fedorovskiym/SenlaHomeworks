package com.senla.ProductService.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.senla.ProductService.dto.subscription.SubscriptionDTO;
import com.senla.ProductService.dto.subscription.SubscriptionDetailsDTO;
import com.senla.ProductService.service.ProductPriceService;
import com.senla.ProductService.service.SubscriptionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class SubscriptionControllerTest {

    private MockMvc mockMvc;

    @Mock
    private SubscriptionService subscriptionService;

    @Mock
    private ProductPriceService productPriceService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private UUID subscriptionId;

    private SubscriptionDTO subscriptionDTO;
    private SubscriptionDetailsDTO subscriptionDetailsDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SubscriptionController controller =
                new SubscriptionController(subscriptionService, productPriceService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.findAndRegisterModules();

        subscriptionId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        UUID shopBranchId = UUID.randomUUID();

        subscriptionDTO = new SubscriptionDTO(subscriptionId, productId, "product",
                shopBranchId, "shop", "address",
                1.0, 0, LocalDate.now());

        subscriptionDetailsDTO = new SubscriptionDetailsDTO();
        subscriptionDetailsDTO.setId(subscriptionId);
        subscriptionDetailsDTO.setProductId(productId);
        subscriptionDetailsDTO.setProductName("product");
        subscriptionDetailsDTO.setShopBranchId(shopBranchId);
        subscriptionDetailsDTO.setShopName("shop");
        subscriptionDetailsDTO.setShopAddress("address");
        subscriptionDetailsDTO.setPrice(1.0);
        subscriptionDetailsDTO.setDiscountPercent(0);
        subscriptionDetailsDTO.setStartDate(LocalDate.now());
    }

    @Test
    void findAllSubscriptionsShouldReturnListOfSubscriptions() throws Exception {
        List<SubscriptionDTO> subscriptions = List.of(subscriptionDTO);

        when(subscriptionService.findAll()).thenReturn(subscriptions);

        mockMvc.perform(get("/subscription/"))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    List<SubscriptionDTO> response = objectMapper.readValue(jsonResponse,
                            objectMapper.getTypeFactory().constructCollectionType(List.class, SubscriptionDTO.class));
                    assertEquals(1, response.size());
                    assertEquals(subscriptionDTO.productName(), response.get(0).productName());
                });

        verify(subscriptionService).findAll();
    }

    @Test
    void deleteSubscriptionShouldReturnNoContent() throws Exception {
        doNothing().when(subscriptionService).deleteById(subscriptionId);

        mockMvc.perform(delete("/subscription/{id}", subscriptionId)).andExpect(status().isNoContent());

        verify(subscriptionService).deleteById(subscriptionId);
    }

    @Test
    void findSubscriptionByIdShouldReturnSubscriptionDetailsDTO() throws Exception {
        when(productPriceService.findSubscriptionByIdWithDetails(subscriptionId)).thenReturn(subscriptionDetailsDTO);

        mockMvc.perform(get("/subscription/{id}", subscriptionId))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    SubscriptionDetailsDTO response = objectMapper.readValue(jsonResponse, SubscriptionDetailsDTO.class);
                    assertEquals(subscriptionDetailsDTO.getId(), response.getId());
                    assertEquals(subscriptionDetailsDTO.getProductName(), response.getProductName());
                });

        verify(productPriceService).findSubscriptionByIdWithDetails(subscriptionId);
    }
}