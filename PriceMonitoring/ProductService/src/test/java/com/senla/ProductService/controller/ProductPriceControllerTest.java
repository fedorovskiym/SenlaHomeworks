package com.senla.ProductService.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.senla.ProductService.dto.price.ComparePrice;
import com.senla.ProductService.dto.price.CreateUpdateProductPriceDTO;
import com.senla.ProductService.dto.price.ProductPriceDTO;
import com.senla.ProductService.dto.price.ProductPriceSearchDTO;
import com.senla.ProductService.dto.price.UpdateProductPrice;
import com.senla.ProductService.model.enums.PriceStatus;
import com.senla.ProductService.service.ProductPriceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ProductPriceControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProductPriceService productPriceService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private UUID priceId;
    private UUID productId;
    private UUID cityId;
    private UUID shopBranchId;
    private UUID categoryId;

    private ProductPriceDTO productPriceDTO;
    private CreateUpdateProductPriceDTO createUpdateProductPriceDTO;
    private ProductPriceSearchDTO productPriceSearchDTO;
    private UpdateProductPrice updateProductPrice;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ProductPriceController controller = new ProductPriceController(productPriceService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.findAndRegisterModules();

        priceId = UUID.randomUUID();
        productId = UUID.randomUUID();
        cityId = UUID.randomUUID();
        shopBranchId = UUID.randomUUID();
        categoryId = UUID.randomUUID();

        productPriceDTO = new ProductPriceDTO(priceId, productId, "product", "description",
                1.0, "unit", categoryId, "category", shopBranchId,
                "address", "shop", cityId, "city", 1.00, LocalDate.now(),
                0, "ACTUAL");

        createUpdateProductPriceDTO = new CreateUpdateProductPriceDTO(null, shopBranchId, shopBranchId,
                2.00, 0);

        productPriceSearchDTO = new ProductPriceSearchDTO(1, 10, shopBranchId, null,
                true, null, null, PriceStatus.ACTUAL);

        updateProductPrice = new UpdateProductPrice(20.0, 15);
    }

    @Test
    void createProductPriceShouldReturnCreatedProductPrice() throws Exception {
        when(productPriceService.save(any(CreateUpdateProductPriceDTO.class))).thenReturn(productPriceDTO);

        mockMvc.perform(post("/price/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUpdateProductPriceDTO)))
                .andExpect(status().isCreated())
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    ProductPriceDTO response = objectMapper.readValue(jsonResponse, ProductPriceDTO.class);
                    assertEquals(productPriceDTO.price(), response.price());
                    assertEquals(productPriceDTO.productName(), response.productName());
                });

        verify(productPriceService).save(any(CreateUpdateProductPriceDTO.class));
    }

    @Test
    void findByIdShouldReturnProductPriceDTO() throws Exception {
        when(productPriceService.findById(priceId)).thenReturn(productPriceDTO);

        mockMvc.perform(get("/price/{id}", priceId))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    ProductPriceDTO response = objectMapper.readValue(jsonResponse, ProductPriceDTO.class);
                    assertEquals(productPriceDTO.id(), response.id());
                    assertEquals(productPriceDTO.price(), response.price());
                });

        verify(productPriceService).findById(priceId);
    }

    @Test
    void findAllWithPaginationShouldReturnListOfPrices() throws Exception {
        List<ProductPriceDTO> prices = List.of(productPriceDTO);

        when(productPriceService.findAllWithPagination(any(ProductPriceSearchDTO.class))).thenReturn(prices);

        mockMvc.perform(post("/price/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productPriceSearchDTO)))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    List<ProductPriceDTO> response = objectMapper.readValue(jsonResponse, objectMapper.getTypeFactory()
                            .constructCollectionType(List.class, ProductPriceDTO.class));
                    assertEquals(1, response.size());
                    assertEquals(productPriceDTO.price(), response.get(0).price());
                });

        verify(productPriceService).findAllWithPagination(any(ProductPriceSearchDTO.class));
    }

    @Test
    void deleteProductPriceShouldReturnNoContent() throws Exception {
        doNothing().when(productPriceService).delete(priceId);

        mockMvc.perform(delete("/price/{id}", priceId)).andExpect(status().isNoContent());

        verify(productPriceService).delete(priceId);
    }

    @Test
    void updateProductPriceShouldReturnUpdatedProductPrice() throws Exception {
        ProductPriceDTO updatedPrice = new ProductPriceDTO(priceId, productId, "product",
                "description", 1.0, "unit", categoryId,
                "category", shopBranchId, "address",
                "shop", cityId, "city", 2.00, LocalDate.now(), 0, null);

        when(productPriceService.update(any(), any(UpdateProductPrice.class))).thenReturn(updatedPrice);

        mockMvc.perform(patch("/price/{id}", priceId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUpdateProductPriceDTO)))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    ProductPriceDTO response = objectMapper.readValue(jsonResponse, ProductPriceDTO.class);
                    assertEquals(createUpdateProductPriceDTO.getPrice(), response.price());
                    assertEquals(createUpdateProductPriceDTO.getDiscountPercent(), response.discountPercent());
                });

        verify(productPriceService).update(any(), any(UpdateProductPrice.class));
    }

    @Test
    void comparePricesInShopShouldReturnComparePrice() throws Exception {
        ComparePrice comparePrice = mock(ComparePrice.class);

        when(productPriceService.comparePricesInShops(productId, cityId)).thenReturn(comparePrice);

        mockMvc.perform(get("/price/compare")
                        .param("productId", productId.toString())
                        .param("cityId", cityId.toString()))
                .andExpect(status().isOk());

        verify(productPriceService).comparePricesInShops(productId, cityId);
    }

    @Test
    void importPricesShouldReturnOk() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "prices.csv",
                "text/csv", "content".getBytes());

        doNothing().when(productPriceService).importFromCsv(any(MockMultipartFile.class));

        mockMvc.perform(multipart("/price/import").file(file))
                .andExpect(status().isOk());

        verify(productPriceService).importFromCsv(any(MockMultipartFile.class));
    }

    @Test
    void searchProductsShouldReturnListOfPrices() throws Exception {
        List<ProductPriceDTO> prices = List.of(productPriceDTO);

        when(productPriceService.search(any(), any())).thenReturn(prices);

        mockMvc.perform(get("/price/search")
                        .param("cityId", cityId.toString())
                        .param("searchQuery", "query"))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    List<ProductPriceDTO> response = objectMapper.readValue(jsonResponse, objectMapper.getTypeFactory()
                            .constructCollectionType(List.class, ProductPriceDTO.class));
                    assertEquals(1, response.size());
                });

        verify(productPriceService).search(cityId, "query");
    }

    @Test
    void subscribeShouldReturnOk() throws Exception {
        doNothing().when(productPriceService).subscribe(priceId);

        mockMvc.perform(post("/price/{id}", priceId)).andExpect(status().isOk());

        verify(productPriceService).subscribe(priceId);
    }

    @Test
    void createRequestToChangePriceShouldReturnOk() throws Exception {
        doNothing().when(productPriceService).createRequest(any(), any(UpdateProductPrice.class));

        mockMvc.perform(patch("/price/{id}/request", priceId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateProductPrice)))
                .andExpect(status().isOk());

        verify(productPriceService).createRequest(any(), any(UpdateProductPrice.class));
    }

    @Test
    void acceptRequestToChangePriceShouldReturnUpdatedPrice() throws Exception {
        when(productPriceService.acceptRequest(priceId, PriceStatus.ACTUAL)).thenReturn(productPriceDTO);

        mockMvc.perform(patch("/price/{id}/accept", priceId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"ACTUAL\""))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    ProductPriceDTO response = objectMapper.readValue(jsonResponse, ProductPriceDTO.class);
                    assertEquals(productPriceDTO.id(), response.id());
                });

        verify(productPriceService).acceptRequest(priceId, PriceStatus.ACTUAL);
    }
}