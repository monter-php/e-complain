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
import java.util.Optional;

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
        when(jpaClientRepository.findByEmail(testClient.email())).thenReturn(Optional.empty());
        when(jpaComplainRepository.save(any(ComplainEntity.class))).thenReturn(savedComplainEntity);

        // Act
        Complain result = complainRepository.saveComplain(testComplain);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getComplainId());
        assertEquals(testComplain.getMessage(), result.getMessage());
        assertEquals(testComplain.getCreationDate(), result.getCreationDate());
        assertEquals(testComplain.getClient().firstName(), result.getClient().firstName());
        assertEquals(testComplain.getClient().lastName(), result.getClient().lastName());
        assertEquals(testComplain.getClient().email(), result.getClient().email());
        assertEquals(testComplain.getCountry(), result.getCountry());
        assertEquals(testComplain.getCounter(), result.getCounter());
        assertEquals(testComplain.getStatus(), result.getStatus());
        
        // Verify interactions
        verify(jpaClientRepository).findByEmail(testClient.email());
        verify(jpaComplainRepository).save(any(ComplainEntity.class));
    }

    @Test
    void saveComplain_shouldUseExistingClient_whenClientExists() {
        // Arrange
        when(jpaClientRepository.findByEmail(testClient.email())).thenReturn(Optional.of(testClientEntity));
        when(jpaComplainRepository.save(any(ComplainEntity.class))).thenReturn(savedComplainEntity);

        // Act
        Complain result = complainRepository.saveComplain(testComplain);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getComplainId());
        assertEquals(testComplain.getMessage(), result.getMessage());
        assertEquals(testComplain.getCreationDate(), result.getCreationDate());
        assertEquals(testComplain.getClient().firstName(), result.getClient().firstName());
        assertEquals(testComplain.getClient().lastName(), result.getClient().lastName());
        assertEquals(testComplain.getClient().email(), result.getClient().email());
        assertEquals(testComplain.getCountry(), result.getCountry());
        assertEquals(testComplain.getCounter(), result.getCounter());
        assertEquals(testComplain.getStatus(), result.getStatus());
        
        // Verify interactions
        verify(jpaClientRepository).findByEmail(testClient.email());
        verify(jpaComplainRepository).save(any(ComplainEntity.class));
        
        // Verify we didn't create a new client entity
        verify(jpaClientRepository, never()).save(any(ClientEntity.class));
    }

    @Test
    void saveComplain_shouldMapEntityToDomainModel_whenSavingComplain() {
        // Arrange
        when(jpaClientRepository.findByEmail(testClient.email())).thenReturn(Optional.of(testClientEntity));
        when(jpaComplainRepository.save(any(ComplainEntity.class))).thenReturn(savedComplainEntity);

        // Act
        Complain result = complainRepository.saveComplain(testComplain);

        // Assert
        assertNotNull(result);
        assertEquals(savedComplainEntity.getId().intValue(), result.getComplainId());
        assertEquals(savedComplainEntity.getMessage(), result.getMessage());
        assertEquals(savedComplainEntity.getCreationDate(), result.getCreationDate());
        assertEquals(savedComplainEntity.getClient().getFirstName(), result.getClient().firstName());
        assertEquals(savedComplainEntity.getClient().getLastName(), result.getClient().lastName());
        assertEquals(savedComplainEntity.getClient().getEmail(), result.getClient().email());
        assertEquals(savedComplainEntity.getCountry(), result.getCountry());
        assertEquals(savedComplainEntity.getCounter(), result.getCounter());
        assertEquals(ComplainStatus.SUBMITTED.getValue(), result.getStatus());
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
                
        when(jpaClientRepository.findByEmail(testClient.email())).thenReturn(Optional.of(testClientEntity));
        when(jpaComplainRepository.save(any(ComplainEntity.class))).thenReturn(entityWithNullId);

        // Act
        Complain result = complainRepository.saveComplain(testComplain);

        // Assert
        assertNotNull(result);
        assertNull(result.getComplainId()); // ID should be null since entity ID is null
        assertEquals(testComplain.getMessage(), result.getMessage());
        assertEquals(testComplain.getCreationDate(), result.getCreationDate());
        assertEquals(testComplain.getClient().firstName(), result.getClient().firstName());
        assertEquals(testComplain.getClient().lastName(), result.getClient().lastName());
        assertEquals(testComplain.getClient().email(), result.getClient().email());
        assertEquals(testComplain.getCountry(), result.getCountry());
        assertEquals(testComplain.getCounter(), result.getCounter());
        assertEquals(testComplain.getStatus(), result.getStatus());
    }
}
