package com.senla.ProductService.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.senla.ProductService.dto.CityDTO;
import com.senla.ProductService.service.CityService;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CityControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CityService cityService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private CityDTO cityDTO;
    private final UUID cityId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        CityController cityController = new CityController(cityService);
        mockMvc = MockMvcBuilders.standaloneSetup(cityController).build();

        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.findAndRegisterModules();

        cityDTO = new CityDTO(cityId, "city");
    }

    @Test
    void findAllShouldReturnListOfCitiesWithPagination() throws Exception {
        List<CityDTO> cities = List.of(cityDTO);

        when(cityService.findAllWithPagination(1, 10)).thenReturn(cities);

        mockMvc.perform(get("/city/")
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    List<CityDTO> response = objectMapper.readValue(jsonResponse,
                            objectMapper.getTypeFactory().constructCollectionType(List.class, CityDTO.class));
                    assertEquals(1, response.size());
                    assertEquals("city", response.get(0).name());
                });

        verify(cityService).findAllWithPagination(1, 10);
    }

    @Test
    void createCityShouldReturnCreatedCity() throws Exception {
        when(cityService.saveCity(any(CityDTO.class))).thenReturn(cityDTO);

        mockMvc.perform(post("/city/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cityDTO)))
                .andExpect(status().isCreated())
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    CityDTO response = objectMapper.readValue(jsonResponse, CityDTO.class);
                    assertEquals("city", response.name());
                });

        verify(cityService).saveCity(any(CityDTO.class));
    }

    @Test
    void getCityByIdShouldReturnCityDTO() throws Exception {
        when(cityService.getCityById(cityId)).thenReturn(cityDTO);

        mockMvc.perform(get("/city/{id}", cityId))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    CityDTO response = objectMapper.readValue(jsonResponse, CityDTO.class);
                    assertEquals(cityId, response.id());
                    assertEquals("city", response.name());
                });

        verify(cityService).getCityById(cityId);
    }

    @Test
    void updateCityShouldReturnUpdatedCity() throws Exception {
        CityDTO updatedCity = new CityDTO(cityId, "newCity");

        when(cityService.update(any(), any(CityDTO.class))).thenReturn(updatedCity);

        mockMvc.perform(patch("/city/{id}", cityId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedCity)))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    CityDTO response = objectMapper.readValue(jsonResponse, CityDTO.class);
                    assertEquals("newCity", response.name());
                });

        verify(cityService).update(any(), any(CityDTO.class));
    }

    @Test
    void deleteCityShouldReturnNoContent() throws Exception {
        doNothing().when(cityService).delete(cityId);

        mockMvc.perform(delete("/city/{id}", cityId)).andExpect(status().isNoContent());

        verify(cityService).delete(cityId);
    }
}