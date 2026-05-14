package com.senla.ProductService.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.senla.ProductService.dto.productCategory.ProductCategoryDTO;
import com.senla.ProductService.dto.productCategory.ProductCategoryUpdateDTO;
import com.senla.ProductService.service.ProductCategoryService;
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
class ProductCategoryControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProductCategoryService productCategoryService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private ProductCategoryDTO productCategoryDTO;
    private ProductCategoryUpdateDTO updateDTO;

    private UUID categoryId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ProductCategoryController controller = new ProductCategoryController(productCategoryService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.findAndRegisterModules();

        categoryId = UUID.randomUUID();

        productCategoryDTO = new ProductCategoryDTO(categoryId, "category",
                "description", "imageUrl");

        updateDTO = new ProductCategoryUpdateDTO("updatedCategory", null);
    }

    @Test
    void findAllShouldReturnListOfCategories() throws Exception {
        List<ProductCategoryDTO> categories = List.of(productCategoryDTO);

        when(productCategoryService.findAll()).thenReturn(categories);

        mockMvc.perform(get("/category/"))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    List<ProductCategoryDTO> response = objectMapper.readValue(
                            jsonResponse, objectMapper.getTypeFactory().constructCollectionType(
                                    List.class, ProductCategoryDTO.class));
                    assertEquals(1, response.size());
                    assertEquals(productCategoryDTO.name(),
                            response.get(0).name());
                });

        verify(productCategoryService).findAll();
    }

    @Test
    void findByIdShouldReturnProductCategoryDTO() throws Exception {
        when(productCategoryService.findById(categoryId)).thenReturn(productCategoryDTO);

        mockMvc.perform(get("/category/{id}", categoryId))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    ProductCategoryDTO response = objectMapper.readValue(jsonResponse, ProductCategoryDTO.class);
                    assertEquals(productCategoryDTO.id(), response.id());
                    assertEquals(productCategoryDTO.name(), response.name());
                });

        verify(productCategoryService).findById(categoryId);
    }

    @Test
    void createCategoryShouldReturnCreatedCategory() throws Exception {
        MockMultipartFile photo = new MockMultipartFile("photo", "image.png",
                MediaType.MULTIPART_FORM_DATA_VALUE, "content".getBytes());

        MockMultipartFile categoryJson = new MockMultipartFile("productCategoryDTO", "productCategoryDTO",
                MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsString(productCategoryDTO).getBytes());

        when(productCategoryService.save(any(ProductCategoryDTO.class), any(MockMultipartFile.class)))
                .thenReturn(productCategoryDTO);

        mockMvc.perform(multipart("/category/")
                        .file(photo)
                        .file(categoryJson))
                .andExpect(status().isCreated())
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    ProductCategoryDTO response = objectMapper.readValue(jsonResponse, ProductCategoryDTO.class);
                    assertEquals(productCategoryDTO.name(), response.name());
                });

        verify(productCategoryService).save(any(ProductCategoryDTO.class), any(MockMultipartFile.class));
    }

    @Test
    void updateCategoryShouldReturnUpdatedCategory() throws Exception {
        ProductCategoryDTO updatedCategory = new ProductCategoryDTO(categoryId, "updatedCategory",
                "description", "imageUrl");

        when(productCategoryService.update(any(), any(ProductCategoryUpdateDTO.class)))
                .thenReturn(updatedCategory);

        mockMvc.perform(patch("/category/{id}", categoryId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    ProductCategoryDTO response = objectMapper.readValue(jsonResponse, ProductCategoryDTO.class);
                    assertEquals(updateDTO.name(), response.name());
                });

        verify(productCategoryService).update(any(), any(ProductCategoryUpdateDTO.class));
    }

    @Test
    void updateCategoryImageShouldReturnUpdatedCategory() throws Exception {
        MockMultipartFile photo = new MockMultipartFile("photo", "image.png",
                MediaType.MULTIPART_FORM_DATA_VALUE, "content".getBytes());

        ProductCategoryDTO updatedCategory = new ProductCategoryDTO(categoryId, "category",
                "description", "newImageUrl");

        when(productCategoryService.updateImage(any(), any(MockMultipartFile.class))).thenReturn(updatedCategory);

        mockMvc.perform(MockMvcRequestBuilders.multipart(
                                "/category/{id}/logo", categoryId)
                        .file(photo)
                        .with(request -> {
                            request.setMethod("PATCH");
                            return request;
                        }))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    ProductCategoryDTO response = objectMapper.readValue(jsonResponse, ProductCategoryDTO.class);
                    assertEquals("newImageUrl", response.imageUrl());
                });

        verify(productCategoryService).updateImage(any(), any(MockMultipartFile.class));
    }

    @Test
    void deleteCategoryShouldReturnNoContent() throws Exception {
        doNothing().when(productCategoryService).deleteById(categoryId);

        mockMvc.perform(delete("/category/{id}", categoryId)).andExpect(status().isNoContent());

        verify(productCategoryService).deleteById(categoryId);
    }
}