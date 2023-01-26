package com.choice.webservice.service;

import com.choice.webservice.entity.Amenity;
import com.choice.webservice.repository.AmenityRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AmenityService {
    private AmenityRepository amenityRepository;

    public Amenity findAmenity(Long id) {
        return amenityRepository.findByAmenityId(id);
    }
}
