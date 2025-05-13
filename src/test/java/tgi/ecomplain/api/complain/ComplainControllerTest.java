package tgi.ecomplain.api.complain;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tgi.ecomplain.api.complain.DTO.ComplainRequest;
import tgi.ecomplain.api.complain.DTO.ComplainResponse;
import tgi.ecomplain.api.complain.DTO.EmailRequest;
import tgi.ecomplain.api.complain.DTO.PatchComplainRequest;
import tgi.ecomplain.application.EcomplainApplication;
import tgi.ecomplain.domain.complain.ComplainNotFoundException;
import tgi.ecomplain.domain.complain.ComplainService;
import tgi.ecomplain.domain.complain.ComplainStatus;
import tgi.ecomplain.domain.complain.model.Complain;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ActiveProfiles("test")
@SpringBootTest(classes = EcomplainApplication.class)
@AutoConfigureMockMvc
class ComplainControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ComplainService complainService;

    @MockitoBean
    private ComplainApiMapper complainMapper;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void createComplain_shouldReturnCreated_whenComplainIsCreatedSuccessfully() throws Exception {
        ComplainRequest request = new ComplainRequest("Test Complain", "john.doe@example.com", "John", "Doe", "127.0.0.1");
        Complain createdComplain = Complain.builder().complainId(1L).build();
        ComplainResponse response = new ComplainResponse(1L, "SUBMITTED", 1);

        when(complainService.createComplain(any(ComplainRequest.class), any(String.class))).thenReturn(createdComplain);
        when(complainMapper.toComplainResponse(any(Complain.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/complains")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.complainId").value(1L))
                .andExpect(jsonPath("$.status").value("SUBMITTED"));
    }

    @Test
    void createComplain_shouldReturnBadRequest_whenRequestIsInvalid() throws Exception {
        ComplainRequest request = new ComplainRequest("", "", "John", "Doe", "127.0.0.1"); // Invalid: message and email are blank

        mockMvc.perform(post("/api/v1/complains")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("must not be blank"))
                .andExpect(jsonPath("$.email").value("must not be blank"));
    }

    @Test
    void getComplainsByEmail_shouldReturnOk_whenComplainsFound() throws Exception {
        EmailRequest emailRequest = new EmailRequest("john.doe@example.com");
        List<Complain> complains = Collections.singletonList(Complain.builder().complainId(1L).build());
        List<ComplainResponse> responseList = Collections.singletonList(new ComplainResponse(1L, "SUBMITTED", 1));

        when(complainService.getComplainsByEmail(any(String.class))).thenReturn(complains);
        when(complainMapper.toComplainResponseList(anyList())).thenReturn(responseList);

        mockMvc.perform(post("/api/v1/complains/by-email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emailRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].complainId").value(1L));
    }

    @Test
    void getComplainsByEmail_shouldReturnOk_whenNoComplainsFound() throws Exception {
        EmailRequest emailRequest = new EmailRequest("no.complains@example.com");

        when(complainService.getComplainsByEmail(any(String.class))).thenReturn(Collections.emptyList());
        when(complainMapper.toComplainResponseList(anyList())).thenReturn(Collections.emptyList());

        mockMvc.perform(post("/api/v1/complains/by-email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emailRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getComplainsByEmail_shouldReturnBadRequest_whenEmailIsMissing() throws Exception {
        EmailRequest emailRequest = new EmailRequest(null); // Missing email

        mockMvc.perform(post("/api/v1/complains/by-email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emailRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateComplain_shouldReturnOk_whenComplainIsUpdatedSuccessfully() throws Exception {
        Long complainId = 1L;
        PatchComplainRequest patchRequest = new PatchComplainRequest("RESOLVED", null, null);
        Complain updatedComplain = Complain.builder().complainId(complainId).build();
        ComplainResponse response = new ComplainResponse(complainId, "RESOLVED", 1);

        when(complainService.updateComplain(eq(complainId), any(PatchComplainRequest.class))).thenReturn(updatedComplain);
        when(complainMapper.toComplainResponse(any(Complain.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/complains/{complainId}", complainId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patchRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.complainId").value(complainId))
                .andExpect(jsonPath("$.status").value("RESOLVED"));
    }

    @Test
    void updateComplain_shouldReturnNotFound_whenComplainDoesNotExist() throws Exception {
        Long complainId = 99L;
        PatchComplainRequest patchRequest = new PatchComplainRequest(ComplainStatus.IN_PROGRESS.getValue(), "", "");

        when(complainService.updateComplain(eq(complainId), any(PatchComplainRequest.class))).thenThrow(new ComplainNotFoundException(complainId));

        mockMvc.perform(put("/api/v1/complains/{complainId}", complainId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patchRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateComplain_shouldReturnBadRequest_whenPatchRequestIsInvalid() throws Exception {
        Long complainId = 1L;
        PatchComplainRequest patchRequest = new PatchComplainRequest("INVALID_STATUS", null, null); // Invalid status

        // Mocking the service call is not strictly necessary here as validation should happen before the service
        // but it's good practice to define the behavior if the service were called.
        // when(complainService.updateComplain(eq(complainId), any(PatchComplainRequest.class))).thenReturn(null); // Or throw a validation exception if service handles it

        mockMvc.perform(put("/api/v1/complains/{complainId}", complainId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patchRequest)))
                .andExpect(status().isBadRequest());
    }
}
