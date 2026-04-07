package com.senla.task1.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.senla.task1.dto.MechanicDTO;
import com.senla.task1.dto.MechanicSortDTO;
import com.senla.task1.service.MechanicService;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@ExtendWith(MockitoExtension.class)
class MechanicControllerTest {

    private MockMvc mockMvc;

    @Mock
    private MechanicService mechanicService;

    @InjectMocks
    private MechanicController mechanicController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mechanicController = new MechanicController(mechanicService);
        mockMvc = MockMvcBuilders.standaloneSetup(mechanicController).build();
    }

    @Test
    void addMechanic_ReturnHttpStatusCreated() throws Exception {
        MechanicDTO mechanicDTO = new MechanicDTO(null, "testName", "testSurname",
                10.0, null);
        doNothing().when(mechanicService).addMechanic(mechanicDTO.name(), mechanicDTO.surname(),
                mechanicDTO.experienceYears());
        mockMvc.perform(post("/mechanic/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mechanicDTO)))
                .andExpect(status().isCreated());
    }

    @Test
    void findAllMechanic_ReturnHttpStatusOk() throws Exception {
        List<MechanicDTO> mechanicList = List.of(new MechanicDTO(1, "name1", "surname1", 10.0, true),
                new MechanicDTO(2, "name2", "surname2", 5.0, false));
        doReturn(mechanicList).when(mechanicService).findAllMechanicDTO();

        mockMvc.perform(get("/mechanic/"))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    var responseList = objectMapper.readValue(jsonResponse, objectMapper.getTypeFactory().constructCollectionType(List.class, MechanicDTO.class));
                    assertEquals(mechanicList, responseList);
                });
    }

    @Test
    void removeMechanicById_ReturnHttpStatusNoContent() throws Exception {
        doNothing().when(mechanicService).removeMechanicById(1);
        mockMvc.perform(delete("/mechanic/{id}", 1))
                .andExpect(status().isNoContent());
    }

    @Test
    void showSortedMechanic_SortByAlphabetReturnHttpStatusOk() throws Exception {
        MechanicSortDTO mechanicDTO = new MechanicSortDTO("true", null, true);
        List<MechanicDTO> mechanicList = List.of(new MechanicDTO(1, "name1", "surname1", 10.0, true),
                new MechanicDTO(2, "name2", "surname2", 5.0, false));

        when(mechanicService.showSortedMechanicByAlphabet(mechanicDTO.flag())).thenReturn(mechanicList);

        mockMvc.perform(get("/mechanic/sort")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mechanicDTO)))
                .andExpect(status().isOk());
    }

    @Test
    void showSortedMechanic_SortByBusyReturnHttpStatusOk() throws Exception {
        MechanicSortDTO mechanicDTO = new MechanicSortDTO(null, true, true);
        List<MechanicDTO> mechanicList = List.of(new MechanicDTO(1, "name1", "surname1", 10.0, true),
                new MechanicDTO(2, "name2", "surname2", 5.0, false));

        when(mechanicService.showSortedMechanicByBusy()).thenReturn(mechanicList);

        mockMvc.perform(get("/mechanic/sort")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mechanicDTO)))
                .andExpect(status().isOk());
    }

    @Test
    void importMechanicFromCSV_ReturnHttpStatusOk() throws Exception {
        String fileName = "test.csv";
        doNothing().when(mechanicService).importFromCSV(fileName);
        mockMvc.perform(post("/mechanic/import")
                .param("fileName", fileName))
                .andExpect(status().isOk());
    }

    @Test
    void exportMechanicToCSV_ReturnHttpStatusOkAndFile() throws Exception {
        String csv = """
                id;name;surname;experienceYears;isBusy
                1;name1;surname1;10.0;false
                2;name2;surname2;5.0;true
                """;
        doReturn(csv).when(mechanicService).exportToCSV();
        mockMvc.perform(get("/mechanic/export"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"mechanic.csv\""))
                .andExpect(content().contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(content().string(csv));
    }
}