package com.choice.webservice.service;

import com.choice.gs_ws.GetAllHotelsRequest;
import com.choice.gs_ws.GetAllHotelsWithFilteringRequest;
import com.choice.webservice.entity.Hotel;
import com.choice.webservice.repository.HotelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    public List<Hotel> getAllHotels(GetAllHotelsRequest request) {
        List<Hotel> hotelList = new ArrayList<>();
        Pageable page =
                PageRequest.of(request.getPaginationInfo().getPageNumber(), request.getPaginationInfo().getPageSize());
        hotelRepository.findAll(page).forEach(hotelList::add);
        return hotelList;
    }

    public List<Hotel> getAllHotelsWithFiltering(GetAllHotelsWithFilteringRequest request) {
        List<Hotel> hotelList = new ArrayList<>();
        Pageable page =
                PageRequest.of(request.getPaginationInfo().getPageNumber(), request.getPaginationInfo().getPageSize());
        hotelRepository.findByNameContaining(request.getNameFilter(), page).forEach(hotelList::add);
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
