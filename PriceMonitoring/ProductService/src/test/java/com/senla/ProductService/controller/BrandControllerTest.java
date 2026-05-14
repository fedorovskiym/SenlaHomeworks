package com.senla.ProductService.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.senla.ProductService.dto.brand.BrandDTO;
import com.senla.ProductService.dto.brand.BrandUpdateDTO;
import com.senla.ProductService.model.Brand;
import com.senla.ProductService.service.BrandService;
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
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class BrandControllerTest {

    private MockMvc mockMvc;

    @Mock
    private BrandService brandService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private BrandDTO brandDTO;
    private BrandUpdateDTO brandUpdateDTO;
    private final UUID brandId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        Brand brand = new Brand();
        brand.setName("brand");
        brand.setId(brandId);
        brand.setCountry("country");
        brand.setLogoImageUrl("logoImageUrl");

        MockitoAnnotations.openMocks(this);
        BrandController brandController = new BrandController(brandService);
        mockMvc = MockMvcBuilders.standaloneSetup(brandController).build();

        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.findAndRegisterModules();

        brandDTO = new BrandDTO(brandId, brand.getName(), brand.getCountry(), brand.getLogoImageUrl());
        brandUpdateDTO = new BrandUpdateDTO("updatedBrand", null);
    }

    @Test
    void findAllShouldReturnListOfBrands() throws Exception {
        List<BrandDTO> brands = List.of(brandDTO);

        when(brandService.findAll()).thenReturn(brands);

        mockMvc.perform(get("/brand/"))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    List<BrandDTO> response = objectMapper.readValue(jsonResponse,
                            objectMapper.getTypeFactory().constructCollectionType(List.class, BrandDTO.class));
                    assertEquals(1, response.size());
                    assertEquals(brandDTO.name(), response.get(0).name());
                });

        verify(brandService).findAll();
    }

    @Test
    void findByIdShouldReturnBrandDTO() throws Exception {
        when(brandService.findById(brandId)).thenReturn(brandDTO);

        mockMvc.perform(get("/brand/{id}", brandId))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    BrandDTO response = objectMapper.readValue(jsonResponse, BrandDTO.class);
                    assertEquals(brandDTO.id(), response.id());
                    assertEquals(brandDTO.name(), response.name());
                });

        verify(brandService).findById(brandId);
    }

    @Test
    void saveShouldCreateBrandWithPhoto() throws Exception {
        MockMultipartFile photo = new MockMultipartFile("photo", "logo.png",
                MediaType.MULTIPART_FORM_DATA_VALUE, "content".getBytes());

        MockMultipartFile brandJson = new MockMultipartFile("brandDTO", "logo.png",
                MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsString(brandDTO).getBytes());

        when(brandService.save(any(BrandDTO.class), any(MockMultipartFile.class))).thenReturn(brandDTO);

        mockMvc.perform(multipart("/brand/")
                        .file(brandJson)
                        .file(photo))
                .andExpect(status().isCreated())
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    BrandDTO response = objectMapper.readValue(jsonResponse, BrandDTO.class);
                    assertEquals(brandDTO.name(), response.name());
                });

        verify(brandService).save(any(BrandDTO.class), any(MockMultipartFile.class));
    }

    @Test
    void updateBrandShouldReturnUpdatedBrand() throws Exception {
        BrandDTO updatedBrand = new BrandDTO(brandId, "updatedBrand", null, null);

        when(brandService.update(any(), any(BrandUpdateDTO.class))).thenReturn(updatedBrand);

        mockMvc.perform(patch("/brand/{id}", brandId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(brandUpdateDTO)))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    BrandDTO response = objectMapper.readValue(jsonResponse, BrandDTO.class);
                    assertEquals(brandUpdateDTO.name(), response.name());
                });

        verify(brandService).update(any(), any(BrandUpdateDTO.class));
    }

    @Test
    void updateBrandLogoShouldReturnUpdatedBrand() throws Exception {
        MockMultipartFile photo = new MockMultipartFile("photo", "logo.png",
                MediaType.MULTIPART_FORM_DATA_VALUE, "content".getBytes());

        BrandDTO updatedBrand = new BrandDTO(brandId, "brand", "country", "newImageUrl");

        when(brandService.updateLogo(any(), any(MockMultipartFile.class))).thenReturn(updatedBrand);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/brand/{id}/logo", brandId)
                        .file(photo)
                        .with(request -> {
                            request.setMethod("PATCH");
                            return request;
                        }))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    BrandDTO response = objectMapper.readValue(jsonResponse, BrandDTO.class);
                    assertEquals("newImageUrl", response.logoImageUrl());
                });

        verify(brandService).updateLogo(any(), any(MockMultipartFile.class));
    }

    @Test
    void deleteBrandShouldReturnNoContent() throws Exception {
        doNothing().when(brandService).delete(brandId);

        mockMvc.perform(delete("/brand/{id}", brandId)).andExpect(status().isNoContent());

        verify(brandService).delete(brandId);
    }
}