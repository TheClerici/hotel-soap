package com.choice.webservice.endpoints;

import com.choice.webservice.entity.Hotel;
import com.choice.webservice.service.HotelService;
import com.choiceWS.gs_ws.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import java.util.ArrayList;
import java.util.List;

@Endpoint
public class HotelEndpoint {
    private static final String NAMESPACE_URI = "http://www.choice.com/hotel-ws";
    @Autowired
    private final HotelService hotelService;

    public HotelEndpoint(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getHotelByIdRequest")
    @ResponsePayload
    public GetHotelByIdResponse getHotel(@RequestPayload GetHotelByIdRequest request) {
        GetHotelByIdResponse response = new GetHotelByIdResponse();
        HotelInfo hotelInfo = new HotelInfo();
        BeanUtils.copyProperties(hotelService.getHotelById(request.getHotelId()), hotelInfo);
        response.setHotelInfo(hotelInfo);
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getAllHotelsRequest")
    @ResponsePayload
    public GetAllHotelsResponse getAllHotels() {
        GetAllHotelsResponse response = new GetAllHotelsResponse();
        List<HotelInfo> hotelInfoList = new ArrayList<>();
        List<Hotel> hotelList = hotelService.getAllHotels();
        for (Hotel hotel : hotelList) {
            HotelInfo hotelInfo = new HotelInfo();
            BeanUtils.copyProperties(hotel, hotelInfo);
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
        Hotel hotel = new Hotel();
        BeanUtils.copyProperties(request.getHotelInfo(), hotel);
        hotelService.updateHotel(hotel);
        serviceStatus.setStatusCode("SUCCESS");
        serviceStatus.setMessage("Hotel Updated Successfully");
        response.setServiceStatus(serviceStatus);
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "deleteHotelRequest")
    @ResponsePayload
    public DeleteHotelResponse deleteHotel(@RequestPayload DeleteHotelRequest request) {
        Hotel hotel = hotelService.getHotelById(request.getHotelId());
        ServiceStatus serviceStatus = new ServiceStatus();
        if (hotel == null) {
            serviceStatus.setStatusCode("FAIL");
            serviceStatus.setMessage("Hotel Not Available");
        } else {
            hotelService.deleteHotel(hotel);
            serviceStatus.setStatusCode("SUCCESS");
            serviceStatus.setMessage("Hotel Deleted Successfully");
        }
        DeleteHotelResponse response = new DeleteHotelResponse();
        response.setServiceStatus(serviceStatus);
        return response;
    }
}
