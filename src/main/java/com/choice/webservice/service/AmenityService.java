package com.choice.webservice.service;

import com.choice.webservice.entity.Amenity;
import com.choice.webservice.repository.AmenityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AmenityService {
    private final AmenityRepository amenityRepository;

    public Amenity findAmenity(Long id) {
        return amenityRepository.findByAmenityId(id);
    }
}
