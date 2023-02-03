package com.choice.webservice.endpoints;


import com.choice.webservice.service.ErrorService;
import com.choice.webservice.service.HotelService;
import com.choice.gs_ws.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
@RequiredArgsConstructor
public class HotelEndpoint {
    private static final String NAMESPACE_URI = "http://www.choice.com/hotel-ws";
    private final HotelService hotelService;

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getHotelByIdRequest")
    @ResponsePayload
    public GetHotelByIdResponse getHotel(@RequestPayload GetHotelByIdRequest request) {
        return hotelService.getHotelById(request);
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getAllHotelsWithFilteringRequest")
    @ResponsePayload
    public GetAllHotelsWithFilteringResponse getAllHotelsWithFiltering(@RequestPayload GetAllHotelsWithFilteringRequest request) {
        return hotelService.getAllHotelsWithFiltering(request);
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "addHotelRequest")
    @ResponsePayload
    public AddHotelResponse addHotel(@RequestPayload AddHotelRequest request) {
        return hotelService.addHotel(request);
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "updateHotelRequest")
    @ResponsePayload
    public UpdateHotelResponse updateHotel(@RequestPayload UpdateHotelRequest request) {
        return hotelService.updateHotel(request);
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "deleteHotelRequest")
    @ResponsePayload
    public DeleteHotelResponse deleteHotel(@RequestPayload DeleteHotelRequest request) {
        return hotelService.deleteHotel(request);
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "addAmenityRequest")
    @ResponsePayload
    public AddAmenityResponse addAmenity(@RequestPayload AddAmenityRequest request) {
        return hotelService.addAmenity(request);
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "deleteAmenityRequest")
    @ResponsePayload
    public DeleteAmenityResponse deleteAmenity(@RequestPayload DeleteAmenityRequest request) {
        return hotelService.deleteAmenity(request);
    }
}

