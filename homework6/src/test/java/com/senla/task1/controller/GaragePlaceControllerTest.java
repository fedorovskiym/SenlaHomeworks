package com.senla.task1.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.senla.task1.dto.GaragePlaceDTO;
import com.senla.task1.models.GaragePlace;
import com.senla.task1.service.GaragePlaceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(MockitoExtension.class)
public class GaragePlaceControllerTest {

    private MockMvc mockMvc;

    @Mock
    private GaragePlaceService garagePlaceService;

    @InjectMocks
    private GaragePlaceController garagePlaceController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        garagePlaceController = new GaragePlaceController(garagePlaceService);
        mockMvc = MockMvcBuilders.standaloneSetup(garagePlaceController).build();
    }

    @Test
    public void showFreeGaragePlaces_ReturnsResponseEntity() throws Exception {
        List<GaragePlaceDTO> garagePlaces = List.of(new GaragePlaceDTO(1, 1, true), new GaragePlaceDTO(3, 3, true));
        doReturn(garagePlaces).when(garagePlaceService).findFreeGaragePlaces();

        mockMvc.perform(get("/garage_place/free"))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    var responseList = objectMapper.readValue(jsonResponse, objectMapper.getTypeFactory().constructCollectionType(List.class, GaragePlaceDTO.class));
                    assertEquals(garagePlaces, responseList);
                });
    }

    @Test
    public void deleteGaragePlace_ReturnHttpStatusNoContent() throws Exception {
        GaragePlace garagePlace = new GaragePlace(1, 1, true);
        doNothing().when(garagePlaceService).removeGaragePlace(garagePlace.getId());
        mockMvc.perform(delete("/garage_place/{id}", garagePlace.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    public void createGaragePlace_ReturnHttpStatusCreated() throws Exception {
        GaragePlaceDTO garagePlaceDTO = new GaragePlaceDTO(null, 1, null);
        doNothing().when(garagePlaceService).addGaragePlace(garagePlaceDTO.placeNumber());
        mockMvc.perform(post("/garage_place/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(garagePlaceDTO)))
                .andExpect(status().isCreated());
    }

    @Test
    public void importGaragePlacesFromCsv_ReturnHttpStatusOk() throws Exception {
        String fileName = "test.csv";
        doNothing().when(garagePlaceService).importFromCSV(fileName);
        mockMvc.perform(post("/garage_place/import")
                        .param("fileName", fileName))
                .andExpect(status().isOk());
    }

    @Test
    public void exportGaragePlacesToCsv_ReturnHttpStatusOkAndFile() throws Exception {
        String csvFile = """
                id;placeNumber;isEmpty
                1;1;true
                """;
        doReturn(csvFile).when(garagePlaceService).exportToCSV();
        mockMvc.perform(get("/garage_place/export"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"places.csv\""))
                .andExpect(content().contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(content().string(csvFile));
    }
}
