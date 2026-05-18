package com.senla.ProductService.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.senla.ProductService.dto.ShopBranchDTO;
import com.senla.ProductService.dto.ShopBranchUpdateDTO;
import com.senla.ProductService.service.ShopBranchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ShopBranchControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ShopBranchService shopBranchService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private UUID shopBranchId;
    private UUID shopId;
    private UUID cityId;

    private ShopBranchDTO shopBranchDTO;
    private ShopBranchUpdateDTO shopBranchUpdateDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ShopBranchController controller = new ShopBranchController(shopBranchService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.findAndRegisterModules();

        shopBranchId = UUID.randomUUID();
        shopId = UUID.randomUUID();
        cityId = UUID.randomUUID();

        shopBranchDTO = new ShopBranchDTO(shopBranchId, shopId, cityId,
                "shop", "city", "street",
                1, 1, "imageUrl"
        );

        shopBranchUpdateDTO = new ShopBranchUpdateDTO("updatedStreet", 1, 1);
    }

    @Test
    void createShopBranchShouldReturnCreatedShopBranch() throws Exception {
        when(shopBranchService.save(any(ShopBranchDTO.class))).thenReturn(shopBranchDTO);

        mockMvc.perform(post("/shop_branch/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(shopBranchDTO)))
                .andExpect(status().isCreated())
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    ShopBranchDTO response = objectMapper.readValue(jsonResponse, ShopBranchDTO.class);
                    assertEquals(shopBranchDTO.id(), response.id());
                    assertEquals(shopBranchDTO.street(), response.street());
                });

        verify(shopBranchService).save(any(ShopBranchDTO.class));
    }

    @Test
    void getShopBranchByCityIdAndShopIdShouldReturnListOfShopBranches() throws Exception {
        List<ShopBranchDTO> shopBranches = List.of(shopBranchDTO);

        when(shopBranchService.findAllByCityIdAndShopId(cityId, shopId)).thenReturn(shopBranches);

        mockMvc.perform(get("/shop_branch/")
                        .param("cityId", cityId.toString())
                        .param("shopId", shopId.toString()))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    List<ShopBranchDTO> response = objectMapper.readValue(jsonResponse,
                            objectMapper.getTypeFactory().constructCollectionType(List.class, ShopBranchDTO.class));
                    assertEquals(1, response.size());
                    assertEquals(shopBranchDTO.street(), response.get(0).street());
                });

        verify(shopBranchService).findAllByCityIdAndShopId(cityId, shopId);
    }

    @Test
    void getShopBranchByIdShouldReturnShopBranchDTO() throws Exception {
        when(shopBranchService.findById(shopBranchId)).thenReturn(shopBranchDTO);

        mockMvc.perform(get("/shop_branch/{id}", shopBranchId))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    ShopBranchDTO response = objectMapper.readValue(jsonResponse, ShopBranchDTO.class);
                    assertEquals(shopBranchDTO.id(), response.id());
                    assertEquals(shopBranchDTO.street(), response.street());
                });

        verify(shopBranchService).findById(shopBranchId);
    }

    @Test
    void deleteShopBranchShouldReturnNoContent() throws Exception {
        doNothing().when(shopBranchService).delete(shopBranchId);

        mockMvc.perform(delete("/shop_branch/{id}", shopBranchId)).andExpect(status().isNoContent());

        verify(shopBranchService).delete(shopBranchId);
    }

    @Test
    void updateShopBranchShouldReturnUpdatedShopBranch() throws Exception {
        ShopBranchDTO updatedShopBranch = new ShopBranchDTO(shopBranchId, shopId, cityId,
                "shop", "city", "updatedStreet",
                1, 1, "imageUrl");

        when(shopBranchService.update(any(), any(ShopBranchUpdateDTO.class))).thenReturn(updatedShopBranch);

        mockMvc.perform(patch("/shop_branch/{id}", shopBranchId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(shopBranchUpdateDTO)))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    ShopBranchDTO response = objectMapper.readValue(jsonResponse, ShopBranchDTO.class);
                    assertEquals(shopBranchUpdateDTO.street(), response.street());
                });

        verify(shopBranchService).update(any(), any(ShopBranchUpdateDTO.class));
    }
}