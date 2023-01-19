package com.choice.webservice.repository;

import com.choice.webservice.entity.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HotelRepository extends CrudRepository<Hotel, Long> {
    Hotel findByHotelId(long hotelId);
    List<Hotel> findByNameAndAddressAndRating(String name, String address, int rating);
}
