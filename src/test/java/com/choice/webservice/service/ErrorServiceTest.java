package com.choice.webservice.service;

import com.choice.gs_ws.ServiceStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ErrorServiceTest {
    @Autowired
    private ErrorService errorService;
    @Test
    void checkHotelCompleted() {
        ServiceStatus serviceStatus = errorService.checkHotel("Gandara", "Kino 404", 5);
        assertEquals("CHECKED", serviceStatus.getStatusCode());
    }

    @Test
    void checkHotelBadRequestByNoBody() {
        ServiceStatus serviceStatus = errorService.checkHotel(null, null, null);
        assertEquals("BAD_REQUEST", serviceStatus.getStatusCode());
        assertEquals("Hotel has no body. Please fill!", serviceStatus.getMessage());
    }

    @Test
    void checkHotelBadRequestByNameWithNoBody() {
        ServiceStatus serviceStatus = errorService.checkHotel("", "Kino 404", 5);
        assertEquals("BAD_REQUEST", serviceStatus.getStatusCode());
        assertEquals("Hotel has no Name. Please fill!", serviceStatus.getMessage());
    }

    @Test
    void checkHotelBadRequestByAddressWithNoBody() {
        ServiceStatus serviceStatus = errorService.checkHotel("Gandara", "", 5);
        assertEquals("BAD_REQUEST", serviceStatus.getStatusCode());
        assertEquals("Hotel has no Address. Please fill!", serviceStatus.getMessage());
    }

    @Test
    void checkHotelBadRequestByRatingWithNoBody() {
        ServiceStatus serviceStatus = errorService.checkHotel("Gandara", "Kino 404", null);
        assertEquals("BAD_REQUEST", serviceStatus.getStatusCode());
        assertEquals("Hotel has no Rating. Please fill!", serviceStatus.getMessage());
    }
    @Test
    void checkHotelBadRequestByRatingLessThan0orMoreThan5() {
        ServiceStatus serviceStatus = errorService.checkHotel("Gandara", "Kino 404", 9);
        assertEquals("BAD_REQUEST", serviceStatus.getStatusCode());
        assertEquals("Hotel Rating should be between 0 and 5", serviceStatus.getMessage());
    }

    @Test
    void checkPaginationCompleted() {
        ServiceStatus serviceStatus = errorService.checkPagination(5, 5);
        assertEquals("CHECKED", serviceStatus.getStatusCode());
    }

    @Test
    void checkPaginationBadRequestWithNoBody() {
        ServiceStatus serviceStatus = errorService.checkPagination(null, null);
        assertEquals("BAD_REQUEST", serviceStatus.getStatusCode());
        assertEquals("No pagination provided.", serviceStatus.getMessage());
    }

    @Test
    void checkPaginationBadRequestWithPageIndexLessThanZero() {
        ServiceStatus serviceStatus = errorService.checkPagination(-1, 5);
        assertEquals("BAD_REQUEST", serviceStatus.getStatusCode());
        assertEquals("Page index must not be less than zero", serviceStatus.getMessage());
    }

    @Test
    void checkPaginationBadRequestWithPageSizeLessThanOne() {
        ServiceStatus serviceStatus = errorService.checkPagination(5, 0);
        assertEquals("BAD_REQUEST", serviceStatus.getStatusCode());
        assertEquals("Page size must not be less than one", serviceStatus.getMessage());
    }
}