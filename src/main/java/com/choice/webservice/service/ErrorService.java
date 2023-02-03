package com.choice.webservice.service;

import com.choice.gs_ws.ServiceStatus;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ErrorService {
    public ServiceStatus checkHotel(String name, String address, Integer rating) {
        ServiceStatus serviceStatus = new ServiceStatus();
        serviceStatus.setStatusCode("CHECKED");
        try {
            if (name == null && address == null && rating == null)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Hotel has no body. Please fill!");
            else if (name == null || name.length() < 1)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Hotel has no Name. Please fill!");
            else if (address == null || address.length() < 1)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Hotel has no Address. Please fill!");
            else if (rating == null)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Hotel has no Rating. Please fill!");

            if (rating < 0 || rating > 5)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Hotel Rating should be between 0 and 5");
        } catch (ResponseStatusException e) {
            serviceStatus.setStatusCode("BAD_REQUEST");
            serviceStatus.setMessage(e.getReason());
        }
        return serviceStatus;
    }
}
