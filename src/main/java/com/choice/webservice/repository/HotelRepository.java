package com.choice.webservice.repository;

import com.choice.webservice.entity.Hotel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long> {
    Hotel findByHotelId(long hotelId);
    List<Hotel> findByNameAndAddressAndRating(String name, String address, int rating);
    List<Hotel> findByNameContaining(String name, Pageable pageable);

    //@Query(value = "SELECT * FROM hotels", nativeQuery = true)
    //Page<Hotel> findHotelsWithPagination(final Pageable pageable);
}
