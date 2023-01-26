package com.choice.webservice.endpoints;

import com.choice.webservice.entity.Amenity;
import com.choice.webservice.entity.Hotel;
import com.choice.webservice.service.AmenityService;
import com.choice.webservice.service.HotelService;
import com.choice.gs_ws.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import java.util.ArrayList;
import java.util.List;

@Endpoint
@RequiredArgsConstructor
public class HotelEndpoint {
    private static final String NAMESPACE_URI = "http://www.choice.com/hotel-ws";
    private final HotelService hotelService;
    private final AmenityService amenityService;

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getHotelByIdRequest")
    @ResponsePayload
    public GetHotelByIdResponse getHotel(@RequestPayload GetHotelByIdRequest request) {
        GetHotelByIdResponse response = new GetHotelByIdResponse();

        HotelInfo hotelInfo = new HotelInfo();
        Hotel hotel = hotelService.getHotelById(request.getHotelId());
        BeanUtils.copyProperties(hotel, hotelInfo);
        for(Amenity amenity: hotel.getAmenities()) {
            AmenityInfo amenityInfo = new AmenityInfo();
            BeanUtils.copyProperties(amenity, amenityInfo);
            hotelInfo.getAmenityInfo().add(amenityInfo);
        }
        response.setHotelInfo(hotelInfo);
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getAllHotelsRequest")
    @ResponsePayload
    public GetAllHotelsResponse getAllHotels(@RequestPayload GetAllHotelsRequest request) {
        GetAllHotelsResponse response = new GetAllHotelsResponse();

        List<HotelInfo> hotelInfoList = new ArrayList<>();
        List<Hotel> hotelList = hotelService.getAllHotels(request);
        for (Hotel hotel : hotelList) {
            HotelInfo hotelInfo = new HotelInfo();
            BeanUtils.copyProperties(hotel, hotelInfo);
            for(Amenity amenity: hotel.getAmenities()) {
                AmenityInfo amenityInfo = new AmenityInfo();
                BeanUtils.copyProperties(amenity, amenityInfo);
                hotelInfo.getAmenityInfo().add(amenityInfo);
            }
            hotelInfoList.add(hotelInfo);
        }
        response.getHotelInfo().addAll(hotelInfoList);
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getAllHotelsWithFilteringRequest")
    @ResponsePayload
    public GetAllHotelsWithFilteringResponse getAllHotelsWithFiltering(@RequestPayload GetAllHotelsWithFilteringRequest request) {
        GetAllHotelsWithFilteringResponse response = new GetAllHotelsWithFilteringResponse();

        List<HotelInfo> hotelInfoList = new ArrayList<>();
        List<Hotel> hotelList = hotelService.getAllHotelsWithFiltering(request);
        for (Hotel hotel : hotelList) {
            HotelInfo hotelInfo = new HotelInfo();
            BeanUtils.copyProperties(hotel, hotelInfo);
            for(Amenity amenity: hotel.getAmenities()) {
                AmenityInfo amenityInfo = new AmenityInfo();
                BeanUtils.copyProperties(amenity, amenityInfo);
                hotelInfo.getAmenityInfo().add(amenityInfo);
            }
            hotelInfoList.add(hotelInfo);
        }
        response.getHotelInfo().addAll(hotelInfoList);
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "addHotelRequest")
    @ResponsePayload
    public AddHotelResponse addHotel(@RequestPayload AddHotelRequest request) {
        AddHotelResponse response = new AddHotelResponse();
        ServiceStatus serviceStatus = new ServiceStatus();

        Hotel hotel = new Hotel();
        hotel.setName(request.getName());
        hotel.setAddress(request.getAddress());
        hotel.setRating(request.getRating());
        boolean flag = hotelService.addHotel(hotel);
        if (!flag) {
            serviceStatus.setStatusCode("CONFLICT");
            serviceStatus.setMessage("Hotel Already Available on DB");
            response.setServiceStatus(serviceStatus);
        } else {
            HotelInfo hotelInfo = new HotelInfo();
            BeanUtils.copyProperties(hotel, hotelInfo);
            response.setHotelInfo(hotelInfo);
            serviceStatus.setStatusCode("SUCCESS");
            serviceStatus.setMessage("Hotel Added Successfully");
            response.setServiceStatus(serviceStatus);
        }
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "updateHotelRequest")
    @ResponsePayload
    public UpdateHotelResponse updateHotel(@RequestPayload UpdateHotelRequest request) {
        UpdateHotelResponse response = new UpdateHotelResponse();
        ServiceStatus serviceStatus = new ServiceStatus();
        HotelInfo hotelInfo = new HotelInfo();

        Hotel hotel = hotelService.getHotelById(request.getHotelInfo().getHotelId());
        BeanUtils.copyProperties(hotel, hotelInfo);
        for(Amenity amenity: hotel.getAmenities()) {
            AmenityInfo amenityInfo = new AmenityInfo();
            BeanUtils.copyProperties(amenity, amenityInfo);
            hotelInfo.getAmenityInfo().add(amenityInfo);
        }

        HotelInfo reqHotelInfo = request.getHotelInfo();
        hotelInfo.setName(reqHotelInfo.getName());
        hotelInfo.setAddress(reqHotelInfo.getAddress());
        hotelInfo.setRating(reqHotelInfo.getRating());

        BeanUtils.copyProperties(hotelInfo, hotel);
        hotelService.updateHotel(hotel);
        response.setHotelInfo(hotelInfo);

        serviceStatus.setStatusCode("SUCCESS");
        serviceStatus.setMessage("Hotel Updated Successfully");
        response.setServiceStatus(serviceStatus);
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "deleteHotelRequest")
    @ResponsePayload
    public DeleteHotelResponse deleteHotel(@RequestPayload DeleteHotelRequest request) {
        DeleteHotelResponse response = new DeleteHotelResponse();
        ServiceStatus serviceStatus = new ServiceStatus();

        Hotel hotel = hotelService.getHotelById(request.getHotelId());
        if (hotel == null) {
            serviceStatus.setStatusCode("FAIL");
            serviceStatus.setMessage("Hotel Not Found");
        } else {
            Hotel deleteHotel = new Hotel(request.getHotelId(), "default", "default", 1);
            hotelService.updateHotel(deleteHotel);
            hotelService.deleteHotel(hotel);
            serviceStatus.setStatusCode("SUCCESS");
            serviceStatus.setMessage("Hotel Deleted Successfully");
        }
        response.setServiceStatus(serviceStatus);
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "addAmenityRequest")
    @ResponsePayload
    public AddAmenityResponse addAmenity(@RequestPayload AddAmenityRequest request) {
        AddAmenityResponse response = new AddAmenityResponse();
        ServiceStatus serviceStatus = new ServiceStatus();
        AmenityInfo amenityInfo = new AmenityInfo();

        Hotel hotel = hotelService.getHotelById(request.getHotelId());
        if (hotel == null) {
            serviceStatus.setStatusCode("FAIL");
            serviceStatus.setMessage("Hotel Not Found");
            response.setServiceStatus(serviceStatus);
            return response;
        }

        Amenity amenity = amenityService.findAmenity(request.getAmenityId());
        if (amenity == null) {
            serviceStatus.setStatusCode("FAIL");
            serviceStatus.setMessage("Amenity Not Found");
        } else {
            serviceStatus.setStatusCode("SUCCESS");
            serviceStatus.setMessage("Amenity Added Successfully");
            BeanUtils.copyProperties(amenity, amenityInfo);
            hotel.getAmenities().add(amenity);
            hotelService.updateHotel(hotel);
        }
        response.setAmenityInfo(amenityInfo);
        response.setServiceStatus(serviceStatus);
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "deleteAmenityRequest")
    @ResponsePayload
    public DeleteAmenityResponse deleteAmenity(@RequestPayload DeleteAmenityRequest request) {
        DeleteAmenityResponse response = new DeleteAmenityResponse();
        ServiceStatus serviceStatus = new ServiceStatus();

        Hotel hotel = hotelService.getHotelById(request.getHotelId());
        if (hotel == null) {
            serviceStatus.setStatusCode("FAIL");
            serviceStatus.setMessage("Hotel Not Found");
            response.setServiceStatus(serviceStatus);
            return response;
        }

        Amenity amenity = amenityService.findAmenity(request.getAmenityId());
        if (amenity == null) {
            serviceStatus.setStatusCode("FAIL");
            serviceStatus.setMessage("Amenity Not Found");
        } else {
            serviceStatus.setStatusCode("SUCCESS");
            serviceStatus.setMessage("Amenity Deleted Successfully");
            //hotel.deleteAmenityFromSet(amenity);
            hotel.getAmenities().remove(amenity);
            hotelService.updateHotel(hotel);
        }

        response.setServiceStatus(serviceStatus);
        return response;
    }
}
