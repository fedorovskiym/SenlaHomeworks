package com.senla.ProductService.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.senla.ProductService.dto.ShopDTO;
import com.senla.ProductService.service.ShopService;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ShopControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ShopService shopService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private UUID shopId;
    private UUID cityId;

    private ShopDTO shopDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ShopController controller = new ShopController(shopService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.findAndRegisterModules();

        shopId = UUID.randomUUID();
        cityId = UUID.randomUUID();

        shopDTO = new ShopDTO(shopId, "shop", "imageUrl");
    }

    @Test
    void createShopShouldReturnCreatedShop() throws Exception {
        MockMultipartFile photo = new MockMultipartFile("photo", "logo.png",
                MediaType.MULTIPART_FORM_DATA_VALUE, "content".getBytes());

        MockMultipartFile shopJson = new MockMultipartFile("shopDTO", "shopDTO",
                MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsString(shopDTO).getBytes());

        when(shopService.save(any(ShopDTO.class), any(MockMultipartFile.class))).thenReturn(shopDTO);

        mockMvc.perform(multipart("/shop/")
                        .file(photo)
                        .file(shopJson))
                .andExpect(status().isCreated())
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    ShopDTO response = objectMapper.readValue(jsonResponse, ShopDTO.class);
                    assertEquals(shopDTO.id(), response.id());
                    assertEquals(shopDTO.name(), response.name());
                });

        verify(shopService).save(any(ShopDTO.class), any(MockMultipartFile.class));
    }

    @Test
    void findAllShouldReturnListOfShops() throws Exception {
        List<ShopDTO> shops = List.of(shopDTO);

        when(shopService.findAll()).thenReturn(shops);

        mockMvc.perform(get("/shop/")
                        .param("cityId", cityId.toString()))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    List<ShopDTO> response = objectMapper.readValue(jsonResponse,
                            objectMapper.getTypeFactory().constructCollectionType(List.class, ShopDTO.class));
                    assertEquals(1, response.size());
                    assertEquals(shopDTO.name(), response.get(0).name());
                });

        verify(shopService).findAll();
    }

    @Test
    void findShopByIdShouldReturnShopDTO() throws Exception {
        when(shopService.findById(shopId)).thenReturn(shopDTO);

        mockMvc.perform(get("/shop/{id}", shopId))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    ShopDTO response = objectMapper.readValue(jsonResponse, ShopDTO.class);
                    assertEquals(shopDTO.id(), response.id());
                    assertEquals(shopDTO.name(), response.name());
                });

        verify(shopService).findById(shopId);
    }

    @Test
    void deleteShopShouldReturnNoContent() throws Exception {
        doNothing().when(shopService).delete(shopId);

        mockMvc.perform(delete("/shop/{id}", shopId)).andExpect(status().isNoContent());

        verify(shopService).delete(shopId);
    }

    @Test
    void updateShopShouldReturnUpdatedShop() throws Exception {
        ShopDTO updatedShop = new ShopDTO(shopId, "updatedShop", "imageUrl");

        when(shopService.update(shopId, "updatedShop")).thenReturn(updatedShop);

        mockMvc.perform(patch("/shop/{id}", shopId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("updatedShop"))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    ShopDTO response = objectMapper.readValue(jsonResponse, ShopDTO.class);
                    assertEquals("updatedShop", response.name());
                });

        verify(shopService).update(shopId, "updatedShop");
    }

    @Test
    void updateShopLogoShouldReturnUpdatedShop() throws Exception {
        MockMultipartFile photo = new MockMultipartFile("photo", "logo.png",
                MediaType.MULTIPART_FORM_DATA_VALUE, "content".getBytes());

        ShopDTO updatedShop = new ShopDTO(shopId, "Shop", "newImageUrl");

        when(shopService.updateLogo(any(), any(MockMultipartFile.class))).thenReturn(updatedShop);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/shop/{id}/logo", shopId)
                        .file(photo)
                        .with(request -> {
                            request.setMethod("PATCH");
                            return request;
                        }))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    ShopDTO response = objectMapper.readValue(jsonResponse, ShopDTO.class);
                    assertEquals("newImageUrl", response.logoImageUrl());
                });

        verify(shopService).updateLogo(any(), any(MockMultipartFile.class));
    }
}