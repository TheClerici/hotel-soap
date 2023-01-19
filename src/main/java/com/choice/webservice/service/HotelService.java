package com.choice.webservice.service;

import com.choice.webservice.entity.Hotel;
import com.choice.webservice.repository.HotelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class HotelService {

    @Autowired
    private final HotelRepository hotelRepository;

    public HotelService(HotelRepository hotelRepository) {
        this.hotelRepository = hotelRepository;
    }

    public Hotel getHotelById(long hotelId) {
        return hotelRepository.findByHotelId(hotelId);
    }

    public List<Hotel> getAllHotels() {
        List<Hotel> hotelList = new ArrayList<>();
        hotelRepository.findAll().forEach(hotelList::add);
        return hotelList;
    }

    public synchronized boolean addHotel(Hotel hotel) {
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
