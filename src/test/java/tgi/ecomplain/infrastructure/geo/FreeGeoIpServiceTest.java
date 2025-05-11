package tgi.ecomplain.infrastructure.geo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpStatus;
import tgi.ecomplain.domain.geo.GeoIpData;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FreeGeoIpServiceTest {

    private static final String TEST_IP = "192.168.1.1";
    private static final String API_URL = "https://freegeoip.io/json/{ip}";

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private FreeGeoIpService freeGeoIpService;

    private GeoIpResponse mockResponse;
    private GeoIpData mockGeoIpData;

    @BeforeEach
    void setUp() {
        mockResponse = new GeoIpResponse(
                TEST_IP,
                "United States",
                "California",
                "San Francisco",
                "94105",
                37.7749,
                -122.4194,
                "America/Los_Angeles"
        );
    }

    @Test
    void getGeoIpData_shouldReturnGeoIpData_whenApiReturnsValidResponse() {
        // Arrange
        when(restTemplate.getForObject(eq(API_URL), eq(GeoIpResponse.class), eq(TEST_IP)))
                .thenReturn(mockResponse);


        // Act
        Optional<GeoIpData> result = freeGeoIpService.getGeoIpData(TEST_IP);

        // Assert
        assertTrue(result.isPresent());
        GeoIpData geoIpData = result.get();
        // Using ReflectionTestUtils to get private fields since there are no getters
        assertEquals(TEST_IP, geoIpData.ip());
        assertEquals("United States", geoIpData.countryName());
    }

    @Test
    void getGeoIpData_shouldReturnEmpty_whenApiReturnsNull() {
        // Arrange
        when(restTemplate.getForObject(eq(API_URL), eq(GeoIpResponse.class), eq(TEST_IP)))
                .thenReturn(null);

        // Act
        Optional<GeoIpData> result = freeGeoIpService.getGeoIpData(TEST_IP);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void getGeoIpData_shouldReturnEmpty_whenMappedDataIsNull() {
        // Arrange
        when(restTemplate.getForObject(eq(API_URL), eq(GeoIpResponse.class), eq(TEST_IP)))
                .thenReturn(null);


        // Act
        Optional<GeoIpData> result = freeGeoIpService.getGeoIpData(TEST_IP);

        // Assert
        assertFalse(result.isPresent());
    }
    
    @Test
    void getGeoIpData_shouldReturnEmpty_whenApiThrowsClientException() {
        // Arrange
        when(restTemplate.getForObject(eq(API_URL), eq(GeoIpResponse.class), eq(TEST_IP)))
                .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad Request"));

        // Act
        Optional<GeoIpData> result = freeGeoIpService.getGeoIpData(TEST_IP);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void getGeoIpData_shouldReturnEmpty_whenApiThrowsServerException() {
        // Arrange
        when(restTemplate.getForObject(eq(API_URL), eq(GeoIpResponse.class), eq(TEST_IP)))
                .thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error"));

        // Act
        Optional<GeoIpData> result = freeGeoIpService.getGeoIpData(TEST_IP);

        // Assert
        assertFalse(result.isPresent());
    }
    
    @Test
    void getGeoIpData_shouldHandleEmptyIpAddress() {
        // Arrange
        String emptyIp = "";
        when(restTemplate.getForObject(eq(API_URL), eq(GeoIpResponse.class), eq(emptyIp)))
                .thenReturn(mockResponse);

        // Act
        Optional<GeoIpData> result = freeGeoIpService.getGeoIpData(emptyIp);

        // Assert
        assertTrue(result.isPresent());
    }
    
    @Test
    void getGeoIpData_shouldHandleNullIpAddress() {
        // Arrange - this test may fail if the implementation doesn't handle null IPs
        // The test is included to document expected behavior
        
        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            freeGeoIpService.getGeoIpData(null);
        });
    }
}
