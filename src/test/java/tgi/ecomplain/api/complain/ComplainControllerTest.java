package tgi.ecomplain.api.complain;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tgi.ecomplain.api.complain.DTO.ComplainRequest;
import tgi.ecomplain.api.complain.DTO.ComplainResponse;
import tgi.ecomplain.api.complain.DTO.EmailRequest;
import tgi.ecomplain.api.complain.DTO.PatchComplainRequest;
import tgi.ecomplain.application.EcomplainApplication;
import tgi.ecomplain.domain.complain.ComplainService;
import tgi.ecomplain.domain.complain.ComplainStatus;
import tgi.ecomplain.domain.complain.model.Client;
import tgi.ecomplain.domain.complain.model.Complain;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ComplainController.class)
@ActiveProfiles("test")
@ContextConfiguration(classes = EcomplainApplication.class)
class ComplainControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ComplainService complainService;

    @MockitoBean
    private ComplainApiMapper complainMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createComplain_shouldReturn201_whenRequestIsValid() throws Exception {
        // Arrange
        ComplainRequest request = new ComplainRequest("Test message", "test@example.com", "John", "Doe", "127.0.0.1");
        Client client = Client.builder().email("test@example.com").firstName("John").lastName("Doe").build();
        Complain createdComplain = Complain.builder()
                .complainId(1L)
                .message("Test message")
                .creationDate(new Date())
                .status(ComplainStatus.SUBMITTED.getValue())
                .client(client)
                .country("TestCountry")
                .counter(1)
                .build();
        ComplainResponse complainResponse = new ComplainResponse(1L, ComplainStatus.SUBMITTED.getValue(), 1);

        when(complainService.createComplain(any(ComplainRequest.class), anyString())).thenReturn(createdComplain);
        when(complainMapper.toComplainResponse(any(Complain.class))).thenReturn(complainResponse);

        // Act & Assert
        mockMvc.perform(post("/api/v1/complains")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/v1/complains/1"))
                .andExpect(jsonPath("$.complainId").value(1L))
                .andExpect(jsonPath("$.status").value(ComplainStatus.SUBMITTED.getValue()))
                .andExpect(jsonPath("$.counter").value(1));
    }

    @Test
    void getComplainsByEmail_shouldReturn200WithComplains_whenComplainsExist() throws Exception {
        // Arrange
        EmailRequest emailRequest = new EmailRequest("test@example.com");
        Client client = Client.builder().email("test@example.com").firstName("John").lastName("Doe").build();
        Complain complain = Complain.builder()
                .complainId(1L)
                .message("Test message")
                .creationDate(new Date())
                .status(ComplainStatus.SUBMITTED.getValue())
                .client(client)
                .country("TestCountry")
                .counter(1)
                .build();
        List<Complain> complains = List.of(complain);
        ComplainResponse complainResponse = new ComplainResponse(1L, ComplainStatus.SUBMITTED.getValue(), 1);
        List<ComplainResponse> complainResponses = List.of(complainResponse);

        when(complainService.getComplainsByEmail(emailRequest.email())).thenReturn(complains);
        when(complainMapper.toComplainResponseList(complains)).thenReturn(complainResponses);

        // Act & Assert
        mockMvc.perform(post("/api/v1/complains/by-email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emailRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].complainId").value(1L))
                .andExpect(jsonPath("$[0].status").value(ComplainStatus.SUBMITTED.getValue()))
                .andExpect(jsonPath("$[0].counter").value(1));
    }

    @Test
    void getComplainsByEmail_shouldReturn200WithEmptyList_whenNoComplainsExist() throws Exception {
        // Arrange
        EmailRequest emailRequest = new EmailRequest("test@example.com");

        when(complainService.getComplainsByEmail(emailRequest.email())).thenReturn(Collections.emptyList());
        when(complainMapper.toComplainResponseList(Collections.emptyList())).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(post("/api/v1/complains/by-email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emailRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void updateComplain_shouldReturn200_whenRequestIsValid() throws Exception {
        // Arrange
        Long complainId = 1L;
        PatchComplainRequest patchRequest = PatchComplainRequest.builder().status("IN_PROGRESS").message("Updated message").build();
        Client client = Client.builder().email("test@example.com").firstName("John").lastName("Doe").build();
        Complain updatedComplain = Complain.builder()
                .complainId(complainId)
                .message("Updated message")
                .creationDate(new Date())
                .status(ComplainStatus.IN_PROGRESS.getValue())
                .client(client)
                .country("TestCountry")
                .counter(2)
                .build();
        ComplainResponse complainResponse = new ComplainResponse(complainId, ComplainStatus.IN_PROGRESS.getValue(), 2);

        when(complainService.updateComplain(eq(complainId), any(PatchComplainRequest.class))).thenReturn(updatedComplain);
        when(complainMapper.toComplainResponse(any(Complain.class))).thenReturn(complainResponse);

        // Act & Assert
        mockMvc.perform(put("/api/v1/complains/{complainId}", complainId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patchRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.complainId").value(complainId))
                .andExpect(jsonPath("$.status").value(ComplainStatus.IN_PROGRESS.getValue()))
                .andExpect(jsonPath("$.counter").value(2));
    }
}
