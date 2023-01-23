package com.choice.webservice.service;

import com.choice.webservice.entity.Hotel;
import com.choice.webservice.repository.HotelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HotelService {

    private final HotelRepository hotelRepository;

    public Hotel getHotelById(long hotelId) {
        return hotelRepository.findByHotelId(hotelId);
    }

    public List<Hotel> getAllHotels() {
        List<Hotel> hotelList = new ArrayList<>();
        hotelRepository.findAll().forEach(hotelList::add);
        return hotelList;
    }

    public boolean addHotel(Hotel hotel) {
        List<Hotel> hotelList = hotelRepository.findByNameAndAddressAndRating(hotel.getName(), hotel.getAddress(), hotel.getRating());
        if (hotelList.size() > 0) return false;
        hotelRepository.save(hotel);
        return true;
    }

    public void updateHotel(Hotel hotel) {
        hotelRepository.save(hotel);
    }

    public void deleteHotel(Hotel hotel) {
        hotelRepository.delete(hotel);
    }
}
