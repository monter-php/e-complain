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
class IPAPIGeoIpServiceTest {

    private static final String TEST_IP = "192.168.1.1";
    private static final String API_URL = "http://ip-api.com/json/{ip}?fields=country,query";

    @Mock
    private RestTemplate restTemplate;

    @Mock 
    private GeoIpDataMapper geoIpDataMapper;

    @InjectMocks
    private IPAPIGeoIpService IPAPIGeoIpService;

    private GeoIpResponse mockResponse;
    private GeoIpData mockGeoIpData; 

    @BeforeEach
    void setUp() {
        mockResponse = new GeoIpResponse(
                TEST_IP,
                "United States"
        );
        mockGeoIpData = new GeoIpData(TEST_IP, "United States");
    }

    @Test
    void getGeoIpData_shouldReturnGeoIpData_whenApiReturnsValidResponse() {
        when(restTemplate.getForObject(eq(API_URL), eq(GeoIpResponse.class), eq(TEST_IP)))
                .thenReturn(mockResponse);
        when(geoIpDataMapper.map(mockResponse)).thenReturn(mockGeoIpData);

        Optional<GeoIpData> result = IPAPIGeoIpService.getGeoIpData(TEST_IP);

        assertTrue(result.isPresent());
        GeoIpData geoIpData = result.get();
        assertEquals(TEST_IP, geoIpData.ip());
        assertEquals("United States", geoIpData.countryName());
    }

    @Test
    void getGeoIpData_shouldReturnEmpty_whenApiReturnsNull() {
        when(restTemplate.getForObject(eq(API_URL), eq(GeoIpResponse.class), eq(TEST_IP)))
                .thenReturn(null);

        Optional<GeoIpData> result = IPAPIGeoIpService.getGeoIpData(TEST_IP);

        assertFalse(result.isPresent());
    }

    @Test
    void getGeoIpData_shouldReturnEmpty_whenMappedDataIsNull() {
        when(restTemplate.getForObject(eq(API_URL), eq(GeoIpResponse.class), eq(TEST_IP)))
                .thenReturn(mockResponse); 
        when(geoIpDataMapper.map(mockResponse)).thenReturn(null);

        Optional<GeoIpData> result = IPAPIGeoIpService.getGeoIpData(TEST_IP);

        assertFalse(result.isPresent());
    }
    
    @Test
    void getGeoIpData_shouldReturnEmpty_whenApiThrowsClientException() {
        when(restTemplate.getForObject(eq(API_URL), eq(GeoIpResponse.class), eq(TEST_IP)))
                .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad Request"));

        Optional<GeoIpData> result = IPAPIGeoIpService.getGeoIpData(TEST_IP);

        assertFalse(result.isPresent());
    }

    @Test
    void getGeoIpData_shouldReturnEmpty_whenApiThrowsServerException() {
        when(restTemplate.getForObject(eq(API_URL), eq(GeoIpResponse.class), eq(TEST_IP)))
                .thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error"));

        Optional<GeoIpData> result = IPAPIGeoIpService.getGeoIpData(TEST_IP);

        assertFalse(result.isPresent());
    }
    
    @Test
    void getGeoIpData_shouldHandleEmptyIpAddress() {
        String emptyIp = "";
        GeoIpResponse localMockResponse = new GeoIpResponse(
            emptyIp, "CountryForEmpty"
        );
        GeoIpData localMockGeoData = new GeoIpData(emptyIp, "CountryForEmpty");

        when(restTemplate.getForObject(eq(API_URL), eq(GeoIpResponse.class), eq(emptyIp)))
                .thenReturn(localMockResponse);
        when(geoIpDataMapper.map(localMockResponse)).thenReturn(localMockGeoData);

        Optional<GeoIpData> result = IPAPIGeoIpService.getGeoIpData(emptyIp);

        assertTrue(result.isPresent());
        assertEquals(emptyIp, result.get().ip());
        assertEquals("CountryForEmpty", result.get().countryName());
    }
    
    @Test
    void getGeoIpData_shouldHandleNullIpAddress() {
        assertThrows(IllegalArgumentException.class, () -> {
            IPAPIGeoIpService.getGeoIpData(null);
        });
    }
}
