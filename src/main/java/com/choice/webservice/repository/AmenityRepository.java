package com.choice.webservice.repository;

import com.choice.webservice.entity.Amenity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AmenityRepository extends JpaRepository<Amenity, Long> {
    Amenity findByAmenityId(Long id);
}
