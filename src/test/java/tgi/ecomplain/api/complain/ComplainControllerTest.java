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
import org.springframework.test.web.servlet.MvcResult;
import com.jayway.jsonpath.JsonPath;
import tgi.ecomplain.api.complain.dto.ClientResponse;
import tgi.ecomplain.api.complain.dto.ComplainDetailResponse;
import tgi.ecomplain.api.complain.dto.ComplainRequest;
import tgi.ecomplain.api.complain.dto.ComplainResponse;
import tgi.ecomplain.api.complain.dto.SearchByEmailRequest;
import tgi.ecomplain.api.complain.dto.PatchComplainRequest;
import tgi.ecomplain.application.EcomplainApplication;
import tgi.ecomplain.domain.complain.ComplainNotFoundException;
import tgi.ecomplain.domain.complain.ComplainService;
import tgi.ecomplain.domain.complain.ComplainStatus;
import tgi.ecomplain.domain.complain.model.Client;
import tgi.ecomplain.domain.complain.model.Complain;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
        ComplainRequest request = new ComplainRequest("PID123", "Test Complain", "john.doe@example.com", "John", "Doe");
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
        ComplainRequest request = new ComplainRequest("PID123", "", "", "John", "Doe"); // Invalid: message and email are blank

        mockMvc.perform(post("/api/v1/complains")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("must not be blank"))
                .andExpect(jsonPath("$.email").value("must not be blank"));
    }

    @Test
    void getComplainsByEmail_shouldReturnOk_whenComplainsFound() throws Exception {
        SearchByEmailRequest searchByEmailRequest = new SearchByEmailRequest("john.doe@example.com");
        List<Complain> complains = Collections.singletonList(Complain.builder().complainId(1L).build());
        List<ComplainResponse> responseList = Collections.singletonList(new ComplainResponse(1L, "SUBMITTED", 1));

        when(complainService.getComplainsByEmail(any(String.class))).thenReturn(complains);
        when(complainMapper.toComplainResponseList(anyList())).thenReturn(responseList);

        mockMvc.perform(post("/api/v1/complains/by-email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(searchByEmailRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].complainId").value(1L));
    }

    @Test
    void getComplainsByEmail_shouldReturnOk_whenNoComplainsFound() throws Exception {
        SearchByEmailRequest searchByEmailRequest = new SearchByEmailRequest("no.complains@example.com");

        when(complainService.getComplainsByEmail(any(String.class))).thenReturn(Collections.emptyList());
        when(complainMapper.toComplainResponseList(anyList())).thenReturn(Collections.emptyList());

        mockMvc.perform(post("/api/v1/complains/by-email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(searchByEmailRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getComplainsByEmail_shouldReturnBadRequest_whenEmailIsMissing() throws Exception {
        SearchByEmailRequest searchByEmailRequest = new SearchByEmailRequest(null); // Missing email

        mockMvc.perform(post("/api/v1/complains/by-email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(searchByEmailRequest)))
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

        mockMvc.perform(put("/api/v1/complains/{complainId}", complainId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patchRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createComplain_shouldIncrementCounter_whenComplainExistsForSameProductAndEmail() throws Exception {
        ComplainRequest request = new ComplainRequest("PROD_XYZ", "Initial complain message", "duplicate@example.com", "John", "Doe");

        // First call: new complain
        Complain complainFirstCall = Complain.builder()
                .productId("PROD_XYZ")
                .complainId(1L)
                .status(ComplainStatus.SUBMITTED.getValue())
                .counter(1)
                .build();
        ComplainResponse responseFirstCall = new ComplainResponse(1L, ComplainStatus.SUBMITTED.getValue(), 1);

        // Second call: existing complain, counter incremented
        Complain complainSecondCall = Complain.builder()
                .productId("PROD_XYZ")
                .complainId(1L) // Same ID
                .status(ComplainStatus.SUBMITTED.getValue())
                .counter(2) // Incremented counter
                .build();
        ComplainResponse responseSecondCall = new ComplainResponse(1L, ComplainStatus.SUBMITTED.getValue(), 2);

        // Mock service to return different Complain objects on sequential calls
        when(complainService.createComplain(any(ComplainRequest.class), anyString()))
                .thenReturn(complainFirstCall, complainSecondCall);

        // Mock mapper to map these Complain objects to their respective ComplainResponse objects
        when(complainMapper.toComplainResponse(complainFirstCall)).thenReturn(responseFirstCall);
        when(complainMapper.toComplainResponse(complainSecondCall)).thenReturn(responseSecondCall);

        // Perform first POST request
        mockMvc.perform(post("/api/v1/complains")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.complainId").value(1L))
                .andExpect(jsonPath("$.status").value(ComplainStatus.SUBMITTED.getValue()))
                .andExpect(jsonPath("$.counter").value(1));

        // Perform second POST request with the same details
        // (productId and email are the key for identifying existing complain)
        ComplainRequest sameDetailsRequest = new ComplainRequest("PROD_XYZ", "Another message for same complain", "duplicate@example.com", "John", "Doe"); // IP or message can be different

        mockMvc.perform(post("/api/v1/complains")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sameDetailsRequest))) // Using sameDetailsRequest or original request if only productId/email matter
                .andExpect(status().isCreated()) // Assuming it still returns 201 Created
                .andExpect(header().exists("Location")) // Location should ideally be the same
                .andExpect(jsonPath("$.complainId").value(1L)) // Same complain ID
                .andExpect(jsonPath("$.status").value(ComplainStatus.SUBMITTED.getValue())) // Status might remain same or update
                .andExpect(jsonPath("$.counter").value(2)); // Counter incremented
    }

    @Test
    void getComplainById_shouldReturnOk_whenComplainExists() throws Exception {
        Long complainId = 1L;
        String productId = "PROD123";
        String message = "Test complain message";
        Date creationDate = new Date();
        String country = "Wonderland";
        Client client = Client.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .build();

        Complain complain = Complain.builder()
                .complainId(complainId)
                .productId(productId)
                .message(message)
                .creationDate(creationDate)
                .status(ComplainStatus.SUBMITTED.getValue())
                .client(client)
                .country(country)
                .counter(1)
                .build();

        ClientResponse clientResponse = new ClientResponse(client.firstName(), client.lastName(), client.email());
        ComplainDetailResponse detailResponse = new ComplainDetailResponse(
                complainId,
                productId,
                message,
                creationDate,
                ComplainStatus.SUBMITTED.getValue(),
                clientResponse,
                country,
                1
        );

        when(complainService.getComplainById(complainId)).thenReturn(complain);
        when(complainMapper.toComplainDetailsResponse(complain)).thenReturn(detailResponse);

        MvcResult result = mockMvc.perform(get("/api/v1/complains/{complainId}", complainId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.complainId").value(complainId))
                .andExpect(jsonPath("$.productId").value(productId))
                .andExpect(jsonPath("$.message").value(message))
                .andExpect(jsonPath("$.status").value(ComplainStatus.SUBMITTED.getValue()))
                .andExpect(jsonPath("$.client.firstName").value(client.firstName()))
                .andExpect(jsonPath("$.client.lastName").value(client.lastName()))
                .andExpect(jsonPath("$.client.email").value(client.email()))
                .andExpect(jsonPath("$.country").value(country))
                .andExpect(jsonPath("$.counter").value(1))
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        String actualCreationDateString = JsonPath.read(responseBody, "$.creationDate");

        Instant creationInstant = creationDate.toInstant();
        OffsetDateTime expectedODT = creationInstant.atOffset(ZoneOffset.UTC);
        OffsetDateTime actualODT = OffsetDateTime.parse(actualCreationDateString);

        assertEquals(expectedODT, actualODT);
    }

    @Test
    void getComplainById_shouldReturnNotFound_whenComplainDoesNotExist() throws Exception {
        Long nonExistentComplainId = 99L;
        when(complainService.getComplainById(nonExistentComplainId)).thenThrow(new ComplainNotFoundException(nonExistentComplainId));

        mockMvc.perform(get("/api/v1/complains/{complainId}", nonExistentComplainId))
                .andExpect(status().isNotFound());
    }
}
