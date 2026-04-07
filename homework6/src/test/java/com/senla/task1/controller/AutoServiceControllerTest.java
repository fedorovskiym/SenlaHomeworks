package com.senla.task1.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.senla.task1.dto.AutoServiceRequestDTO;
import com.senla.task1.dto.OrderDTO;
import com.senla.task1.service.AutoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

class AutoServiceControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AutoService autoService;

    @InjectMocks
    private AutoServiceController autoServiceController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        autoServiceController = new AutoServiceController(autoService);
        mockMvc = MockMvcBuilders.standaloneSetup(autoServiceController).build();
    }

    @Test
    void createOrder_ReturnHttpStatusCreatedAndOrderDTO() throws Exception {
        AutoServiceRequestDTO autoServiceRequestDTO = new AutoServiceRequestDTO("testName", 1, 1,
                3000.0, 1, 0);

        OrderDTO orderDTO = new OrderDTO(1, "testName", 1, null, null, 1,
                null, null, null,null,null,null, 3000.0);

        doReturn(orderDTO).when(autoService).createOrder(autoServiceRequestDTO);

        mockMvc.perform(post("/autoservice/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(autoServiceRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    var responseObject = objectMapper.readValue(jsonResponse,
                            objectMapper.getTypeFactory().constructType(OrderDTO.class));
                    assertEquals(orderDTO, responseObject);
                });
    }

    @Test
    void getAvailableSlot_ReturnHttpStatusOkAndCountAvailableSlot() throws Exception {
        Integer year = 2026;
        Integer month = 1;
        Integer day = 1;
        Integer availableSlot = 2;
        doReturn(availableSlot).when(autoService).getAvailableSlot(year, month, day);

        mockMvc.perform(get("/autoservice/available_slot")
                .param("year", year.toString())
                .param("month", month.toString())
                .param("day", day.toString()))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    var responseObject = objectMapper.readValue(jsonResponse,
                            objectMapper.getTypeFactory().constructType(Integer.class));
                    assertEquals(availableSlot, responseObject);
                });
    }

    @Test
    void exportOrdersToCSV_ReturnHttpStatusOkAndCSVFile() throws Exception {
        String csv = """
                id;carName;mechanicId;garagePlaceNumber;status;submissionDateTime;plannedCompletionDateTime;completionDateTime;endDateTime;duration;price
                1;BMW M5;1;1;ACCEPTED;2025-11-16T21:48:27.167842800;;2025-11-16T21:48:27.167842800;2025-11-16T23:18:27.167842800;90;10000,00
                2;Kio Rio ;2;2;WAITING;2025-11-16T21:49:19.368844300;2025-11-16T23:18:27.167842800;;2025-11-17T00:18:27.167842800;60;9999,00
                """;

        doReturn(csv).when(autoService).exportOrdersToCSV();
        mockMvc.perform(get("/autoservice/export"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"order.csv\""))
                .andExpect(content().contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(content().string(csv));
    }

    @Test
    void importOrdersFromCSV() throws Exception {
        String fileName = "test.csv";
        doNothing().when(autoService).importFromCSV(fileName);

        mockMvc.perform(post("/autoservice/import")
                .param("fileName", fileName))
                .andExpect(status().isOk());
    }
}