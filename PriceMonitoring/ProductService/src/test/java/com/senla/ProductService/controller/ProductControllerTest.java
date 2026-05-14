package com.senla.ProductService.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.senla.ProductService.dto.product.ProductDTO;
import com.senla.ProductService.dto.product.ProductSearchDTO;
import com.senla.ProductService.dto.product.ProductUpdateDTO;
import com.senla.ProductService.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProductService productService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private ProductDTO productDTO;
    private ProductUpdateDTO productUpdateDTO;
    private ProductSearchDTO productSearchDTO;

    private UUID productId;
    private UUID brandId;
    private UUID categoryId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ProductController productController = new ProductController(productService);
        mockMvc = MockMvcBuilders.standaloneSetup(productController).build();

        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.findAndRegisterModules();

        productId = UUID.randomUUID();
        brandId = UUID.randomUUID();
        categoryId = UUID.randomUUID();

        productDTO = new ProductDTO(productId, "product", "description",
                1.0, "unit", brandId, "brand",
                categoryId, "category", "imageUrl");

        productUpdateDTO = new ProductUpdateDTO("updatedProduct", "updatedDescription", 2.0,
                "unit", brandId, categoryId);

        productSearchDTO = new ProductSearchDTO(1, 10, null,
                null, null, null, null);
    }

    @Test
    void createProductShouldReturnCreatedProduct() throws Exception {
        MockMultipartFile photo = new MockMultipartFile("photo", "image.png",
                MediaType.MULTIPART_FORM_DATA_VALUE, "content".getBytes());

        MockMultipartFile productJson = new MockMultipartFile("productDTO", "productDTO",
                MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsString(productDTO).getBytes());

        when(productService.save(any(ProductDTO.class), any(MockMultipartFile.class))).thenReturn(productDTO);

        mockMvc.perform(multipart("/product/")
                        .file(photo)
                        .file(productJson))
                .andExpect(status().isCreated())
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    ProductDTO response = objectMapper.readValue(jsonResponse, ProductDTO.class);
                    assertEquals(productDTO.name(), response.name());
                    assertEquals(productDTO.description(), response.description());
                });

        verify(productService).save(any(ProductDTO.class), any(MockMultipartFile.class));
    }

    @Test
    void findAllShouldReturnListOfProducts() throws Exception {
        List<ProductDTO> products = List.of(productDTO);

        when(productService.findAll(any(ProductSearchDTO.class))).thenReturn(products);

        mockMvc.perform(get("/product/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productSearchDTO)))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    List<ProductDTO> response = objectMapper.readValue(jsonResponse,
                            objectMapper.getTypeFactory().constructCollectionType(List.class, ProductDTO.class));
                    assertEquals(1, response.size());
                    assertEquals(productDTO.name(), response.get(0).name());
                });

        verify(productService).findAll(any(ProductSearchDTO.class));
    }

    @Test
    void findByIdShouldReturnProductDTO() throws Exception {
        when(productService.findById(productId)).thenReturn(productDTO);

        mockMvc.perform(get("/product/{id}", productId))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    ProductDTO response = objectMapper.readValue(jsonResponse, ProductDTO.class);
                    assertEquals(productDTO.id(), response.id());
                    assertEquals(productDTO.name(), response.name());
                });

        verify(productService).findById(productId);
    }

    @Test
    void updateProductShouldReturnUpdatedProduct() throws Exception {
        ProductDTO updatedProduct = new ProductDTO(productId, "updatedProduct", "updatedDescription",
                2.0, "unit", brandId, "brand",
                categoryId, "category", "imageUrl"
        );

        when(productService.update(any(), any(ProductUpdateDTO.class))).thenReturn(updatedProduct);

        mockMvc.perform(patch("/product/{id}", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productUpdateDTO)))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    ProductDTO response = objectMapper.readValue(jsonResponse, ProductDTO.class);
                    assertEquals(productUpdateDTO.name(), response.name());
                    assertEquals(productUpdateDTO.amount(), response.amount());
                });

        verify(productService).update(any(), any(ProductUpdateDTO.class));
    }

    @Test
    void updateProductImageShouldReturnUpdatedProduct() throws Exception {
        MockMultipartFile photo = new MockMultipartFile("photo", "image.png",
                MediaType.MULTIPART_FORM_DATA_VALUE, "content".getBytes());

        ProductDTO updatedProduct = new ProductDTO(productId, "product", "description",
                1.0, "unit", brandId, "brand",
                categoryId, "category", "newImageUrl");

        when(productService.updateImage(any(), any(MockMultipartFile.class))).thenReturn(updatedProduct);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/product/{id}/logo", productId)
                        .file(photo)
                        .with(request -> {
                            request.setMethod("PATCH");
                            return request;
                        }))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    ProductDTO response = objectMapper.readValue(jsonResponse, ProductDTO.class);
                    assertEquals("newImageUrl", response.imageUrl());
                });

        verify(productService).updateImage(any(), any(MockMultipartFile.class));
    }

    @Test
    void deleteProductShouldReturnNoContent() throws Exception {
        doNothing().when(productService).delete(productId);

        mockMvc.perform(delete("/product/{id}", productId)).andExpect(status().isNoContent());

        verify(productService).delete(productId);
    }

    @Test
    void importProductShouldReturnOk() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "products.csv",
                "text/csv", "content".getBytes());

        doNothing().when(productService).importFromCsv(any(MockMultipartFile.class));

        mockMvc.perform(multipart("/product/import")
                .file(file)).andExpect(status().isOk());

        verify(productService).importFromCsv(any(MockMultipartFile.class));
    }
}