package tgi.ecomplain.infrastructure.complain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tgi.ecomplain.domain.complain.model.Client;
import tgi.ecomplain.domain.complain.model.Complain;
import tgi.ecomplain.domain.complain.ComplainStatus;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ComplainRepositoryImplTest {

    @Mock
    private JpaComplainRepository jpaComplainRepository;

    @Mock
    private JpaClientRepository jpaClientRepository;

    @InjectMocks
    private ComplainRepositoryImpl complainRepository;

    private Client testClient;
    private Complain testComplain;
    private ClientEntity testClientEntity;
    private ComplainEntity savedComplainEntity;
    private Date testDate;

    @BeforeEach
    void setUp() {
        // Initialize test data
        testDate = new Date();
        
        // Domain models
        testClient = Client.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .build();
        
        testComplain = Complain.builder()
                .message("Test complaint message")
                .creationDate(testDate)
                .client(testClient)
                .country("US")
                .counter(1)
                .status(ComplainStatus.SUBMITTED.getValue())
                .build();
        
        // JPA entities
        testClientEntity = ClientEntity.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .build();
        
        savedComplainEntity = ComplainEntity.builder()
                .id(1L)
                .message("Test complaint message")
                .creationDate(testDate)
                .client(testClientEntity)
                .country("US")
                .counter(1)
                .status(ComplainStatus.SUBMITTED)
                .build();
    }

    @Test
    void saveComplain_shouldCreateNewClient_whenClientDoesNotExist() {
        // Arrange
        when(jpaClientRepository.findByEmail(testClient.email())).thenReturn(null);
        when(jpaComplainRepository.save(any(ComplainEntity.class))).thenReturn(savedComplainEntity);

        // Act
        Complain result = complainRepository.saveComplain(testComplain);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.complainId());
        assertEquals(testComplain.message(), result.message());
        assertEquals(testComplain.creationDate(), result.creationDate());
        assertEquals(testComplain.client().firstName(), result.client().firstName());
        assertEquals(testComplain.client().lastName(), result.client().lastName());
        assertEquals(testComplain.client().email(), result.client().email());
        assertEquals(testComplain.country(), result.country());
        assertEquals(testComplain.counter(), result.counter());
        assertEquals(testComplain.status(), result.status());
        
        // Verify interactions
        verify(jpaClientRepository).findByEmail(testClient.email());
        verify(jpaComplainRepository).save(any(ComplainEntity.class));
    }

    @Test
    void saveComplain_shouldUseExistingClient_whenClientExists() {
        // Arrange
        when(jpaClientRepository.findByEmail(testClient.email())).thenReturn(testClientEntity);
        when(jpaComplainRepository.save(any(ComplainEntity.class))).thenReturn(savedComplainEntity);

        // Act
        Complain result = complainRepository.saveComplain(testComplain);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.complainId());
        assertEquals(testComplain.message(), result.message());
        assertEquals(testComplain.creationDate(), result.creationDate());
        assertEquals(testComplain.client().firstName(), result.client().firstName());
        assertEquals(testComplain.client().lastName(), result.client().lastName());
        assertEquals(testComplain.client().email(), result.client().email());
        assertEquals(testComplain.country(), result.country());
        assertEquals(testComplain.counter(), result.counter());
        assertEquals(testComplain.status(), result.status());
        
        // Verify interactions
        verify(jpaClientRepository).findByEmail(testClient.email());
        verify(jpaComplainRepository).save(any(ComplainEntity.class));
        
        // Verify we didn't create a new client entity
        verify(jpaClientRepository, never()).save(any(ClientEntity.class));
    }

    @Test
    void saveComplain_shouldMapEntityToDomainModel_whenSavingComplain() {
        // Arrange
        when(jpaClientRepository.findByEmail(testClient.email())).thenReturn(testClientEntity);
        when(jpaComplainRepository.save(any(ComplainEntity.class))).thenReturn(savedComplainEntity);

        // Act
        Complain result = complainRepository.saveComplain(testComplain);

        // Assert
        assertNotNull(result);
        assertEquals(savedComplainEntity.getId().intValue(), result.complainId());
        assertEquals(savedComplainEntity.getMessage(), result.message());
        assertEquals(savedComplainEntity.getCreationDate(), result.creationDate());
        assertEquals(savedComplainEntity.getClient().getFirstName(), result.client().firstName());
        assertEquals(savedComplainEntity.getClient().getLastName(), result.client().lastName());
        assertEquals(savedComplainEntity.getClient().getEmail(), result.client().email());
        assertEquals(savedComplainEntity.getCountry(), result.country());
        assertEquals(savedComplainEntity.getCounter(), result.counter());
        assertEquals(ComplainStatus.SUBMITTED.getValue(), result.status());
    }
    
    @Test
    void saveComplain_shouldHandleNullComplainId_whenMappingFromEntity() {
        // Arrange
        // Create a saved entity with null ID to simulate a case where ID might not be set
        ComplainEntity entityWithNullId = ComplainEntity.builder()
                .id(null)
                .message("Test complaint message")
                .creationDate(testDate)
                .client(testClientEntity)
                .country("US")
                .counter(1)
                .status(ComplainStatus.SUBMITTED)
                .build();
                
        when(jpaClientRepository.findByEmail(testClient.email())).thenReturn(testClientEntity);
        when(jpaComplainRepository.save(any(ComplainEntity.class))).thenReturn(entityWithNullId);

        // Act
        Complain result = complainRepository.saveComplain(testComplain);

        // Assert
        assertNotNull(result);
        assertNull(result.complainId()); // ID should be null since entity ID is null
        assertEquals(testComplain.message(), result.message());
        assertEquals(testComplain.creationDate(), result.creationDate());
        assertEquals(testComplain.client().firstName(), result.client().firstName());
        assertEquals(testComplain.client().lastName(), result.client().lastName());
        assertEquals(testComplain.client().email(), result.client().email());
        assertEquals(testComplain.country(), result.country());
        assertEquals(testComplain.counter(), result.counter());
        assertEquals(testComplain.status(), result.status());
    }
}
