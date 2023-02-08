package com.choice.webservice.service;

import com.choice.webservice.entity.Amenity;
import com.choice.webservice.entity.Hotel;
import com.choice.webservice.repository.HotelRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import com.choice.gs_ws.*;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class HotelServiceTest {
    @InjectMocks
    private HotelService hotelService;
    @Mock
    private AmenityService amenityService;
    @Mock
    private HotelRepository hotelRepository;
    @Mock
    private ErrorService errorService;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void shouldGetNotFoundByGetHotelByIdResponse() {
        GetHotelByIdRequest request = new GetHotelByIdRequest();
        request.setHotelId(10L);

        when(hotelRepository.findByHotelId(request.getHotelId())).thenReturn(null);
        GetHotelByIdResponse found = hotelService.getHotelById(request);

        assertEquals("NOT_FOUND", found.getServiceStatus().getStatusCode());
        assertEquals("Hotel Not Found", found.getServiceStatus().getMessage());
    }

    @Test
    public void shouldGetHotelByIdResponse() {
        GetHotelByIdRequest request = new GetHotelByIdRequest();
        request.setHotelId(1L);

        Hotel hotel = new Hotel(1L, "Gandara", "Blvd Kino 404", 5);
        Set<Amenity> amenities = new HashSet<>();
        hotel.setAmenities(amenities);
        Amenity amenity = new Amenity();
        amenity.setAmenityId(1L);
        amenity.setName("Gym");
        hotel.getAmenities().add(amenity);

        when(hotelRepository.findByHotelId(hotel.getHotelId())).thenReturn(hotel);
        GetHotelByIdResponse found = hotelService.getHotelById(request);

        assertEquals("SUCCESS", found.getServiceStatus().getStatusCode());
        assertEquals("Hotel Found in DB", found.getServiceStatus().getMessage());
        assertEquals(1L, found.getHotelInfo().getHotelId());
        assertEquals("Gandara", found.getHotelInfo().getName());
        assertEquals("Blvd Kino 404", found.getHotelInfo().getAddress());
        assertEquals(5, found.getHotelInfo().getRating());
        assertEquals(1L, found.getHotelInfo().getAmenityInfo().get(0).getAmenityId());
        assertEquals("Gym", found.getHotelInfo().getAmenityInfo().get(0).getName());
    }

    @Test
    public void shouldThrowDataAccessResourceFailureExceptionWhenGettingHotelByIdResponse() {
        GetHotelByIdRequest request = new GetHotelByIdRequest();
        request.setHotelId(1L);

        when(hotelRepository.findByHotelId(Mockito.anyLong())).thenThrow(DataAccessResourceFailureException.class);
        GetHotelByIdResponse found = hotelService.getHotelById(request);

        assertEquals("INTERNAL_SERVER_ERROR", found.getServiceStatus().getStatusCode());
    }

    @Test
    public void shouldThrowRuntimeExceptionWhenGettingHotelByIdResponse() {
        GetHotelByIdRequest request = new GetHotelByIdRequest();
        request.setHotelId(1L);

        when(hotelRepository.findByHotelId(Mockito.anyLong())).thenThrow(RuntimeException.class);
        GetHotelByIdResponse found = hotelService.getHotelById(request);

        assertEquals("INTERNAL_SERVER_ERROR", found.getServiceStatus().getStatusCode());
        assertEquals("There was an error when getting hotel!", found.getServiceStatus().getMessage());
    }

    @Test
    public void shouldGetAllHotelsWithFilteringResponse() {
        GetAllHotelsWithFilteringRequest request = new GetAllHotelsWithFilteringRequest();
        request.setPageNumber(0);
        request.setPageSize(5);
        request.setNameFilter("a");

        List<Hotel> hotelList = new ArrayList<>();
        Hotel hotel1 = new Hotel (1L, "Gandara", "Blvd Kino 404", 5);
        Hotel hotel2 = new Hotel (2L, "Gandara 2", "Blvd Kino 405", 4);
        Set<Amenity> amenities = new HashSet<>();
        hotel1.setAmenities(amenities);
        hotel2.setAmenities(amenities);
        Amenity amenity = new Amenity();
        amenity.setAmenityId(1L);
        amenity.setName("Gym");
        hotel1.getAmenities().add(amenity);
        hotelList.add(hotel1);
        hotelList.add(hotel2);

        ServiceStatus serviceStatus = new ServiceStatus();
        serviceStatus.setStatusCode("CHECKED");

        when(errorService.checkPagination(request.getPageNumber(), request.getPageSize())).thenReturn(serviceStatus);
        Pageable page = PageRequest.of(request.getPageNumber(), request.getPageSize());
        when(hotelRepository.findByNameContaining(request.getNameFilter(), page)).thenReturn(hotelList);
        GetAllHotelsWithFilteringResponse found = hotelService.getAllHotelsWithFiltering(request);

        assertEquals(1, found.getHotelInfo().stream().toList().get(0).getHotelId());
        assertEquals("Gandara", found.getHotelInfo().stream().toList().get(0).getName());
        assertEquals("Blvd Kino 404", found.getHotelInfo().stream().toList().get(0).getAddress());
        assertEquals(5, found.getHotelInfo().stream().toList().get(0).getRating());
        assertEquals(1L, found.getHotelInfo().get(0).getAmenityInfo().get(0).getAmenityId());
        assertEquals("Gym", found.getHotelInfo().get(0).getAmenityInfo().get(0).getName());

        assertEquals(2, found.getHotelInfo().stream().toList().get(1).getHotelId());
        assertEquals("Gandara 2", found.getHotelInfo().stream().toList().get(1).getName());
        assertEquals("Blvd Kino 405", found.getHotelInfo().stream().toList().get(1).getAddress());
        assertEquals(4, found.getHotelInfo().stream().toList().get(1).getRating());
    }

    @Test
    public void shouldThrowDataAccessResourceFailureExceptionWhenGettingHotelsResponse() {
        GetAllHotelsWithFilteringRequest request = new GetAllHotelsWithFilteringRequest();
        request.setPageNumber(0);
        request.setPageSize(5);
        request.setNameFilter("a");

        ServiceStatus serviceStatus = new ServiceStatus();
        serviceStatus.setStatusCode("CHECKED");

        when(errorService.checkPagination(request.getPageNumber(), request.getPageSize())).thenReturn(serviceStatus);

        when(hotelRepository.findByNameContaining(Mockito.any(), Mockito.any())).thenThrow(DataAccessResourceFailureException.class);
        GetAllHotelsWithFilteringResponse found = hotelService.getAllHotelsWithFiltering(request);

        assertEquals("INTERNAL_SERVER_ERROR", found.getServiceStatus().getStatusCode());
    }

    @Test
    public void shouldThrowRuntimeExceptionWhenGettingHotelsResponse() {
        GetAllHotelsWithFilteringRequest request = new GetAllHotelsWithFilteringRequest();
        request.setPageNumber(0);
        request.setPageSize(5);
        request.setNameFilter("a");

        ServiceStatus serviceStatus = new ServiceStatus();
        serviceStatus.setStatusCode("CHECKED");

        when(errorService.checkPagination(request.getPageNumber(), request.getPageSize())).thenReturn(serviceStatus);

        when(hotelRepository.findByNameContaining(Mockito.any(), Mockito.any())).thenThrow(RuntimeException.class);
        GetAllHotelsWithFilteringResponse found = hotelService.getAllHotelsWithFiltering(request);

        assertEquals("INTERNAL_SERVER_ERROR", found.getServiceStatus().getStatusCode());
        assertEquals("There was an error when getting hotels!", found.getServiceStatus().getMessage());
    }

    @Test
    public void shouldGetCheckHotelResponseByAddHotelResponse() {
        AddHotelRequest request = new AddHotelRequest();
        request.setName("Gandara");
        request.setAddress("Blvd Kino 404");
        request.setRating(5);

        ServiceStatus serviceStatus = new ServiceStatus();
        serviceStatus.setStatusCode("BAD_REQUEST");

        when(errorService.checkHotel(request.getName(), request.getAddress(), request.getRating())).thenReturn(serviceStatus);
        AddHotelResponse found = hotelService.addHotel(request);

        assertEquals("BAD_REQUEST", found.getServiceStatus().getStatusCode());
    }

    @Test
    public void shouldGetBadRequestByAddHotelResponse() {
        AddHotelRequest request = new AddHotelRequest();
        request.setName("Gandara");
        request.setAddress("Blvd Kino 404");
        request.setRating(5);

        List<Hotel> hotelList = new ArrayList<>();
        Hotel hotel = new Hotel (1L, "Gandara", "Blvd Kino 404", 5);
        hotelList.add(hotel);

        ServiceStatus serviceStatus = new ServiceStatus();
        serviceStatus.setStatusCode("CHECKED");

        when(errorService.checkHotel(request.getName(), request.getAddress(), request.getRating())).thenReturn(serviceStatus);
        when(hotelRepository.findByName(request.getName())).thenReturn(hotelList);
        AddHotelResponse found = hotelService.addHotel(request);

        assertEquals("BAD_REQUEST", found.getServiceStatus().getStatusCode());
        assertEquals("Hotel Already Available on DB", found.getServiceStatus().getMessage());
    }

    @Test
    public void shouldAddHotelResponse() {
        AddHotelRequest request = new AddHotelRequest();
        request.setName("Gandara");
        request.setAddress("Blvd Kino 404");
        request.setRating(5);

        Hotel hotel = new Hotel();
        hotel.setHotelId(1L);
        hotel.setName(request.getName());
        hotel.setAddress(request.getAddress());
        hotel.setRating(request.getRating());

        List<Hotel> hotelList = new ArrayList<>();

        ServiceStatus serviceStatus = new ServiceStatus();
        serviceStatus.setStatusCode("CHECKED");

        when(errorService.checkHotel(request.getName(), request.getAddress(), request.getRating())).thenReturn(serviceStatus);
        when(hotelRepository.findByName(request.getName())).thenReturn(hotelList);
        when(hotelRepository.save(Mockito.any())).thenReturn(hotel);
        AddHotelResponse found = hotelService.addHotel(request);

        assertEquals("SUCCESS", found.getServiceStatus().getStatusCode());
        assertEquals("Hotel Added Successfully", found.getServiceStatus().getMessage());
        assertEquals(1L, found.getHotelInfo().getHotelId());
        assertEquals("Gandara", found.getHotelInfo().getName());
        assertEquals("Blvd Kino 404", found.getHotelInfo().getAddress());
        assertEquals(5, found.getHotelInfo().getRating());
    }

    @Test
    public void shouldGetCheckHotelResponseByUpdateHotelResponse() {
        UpdateHotelRequest request = new UpdateHotelRequest();

        HotelInfo hotelInfo = new HotelInfo();
        hotelInfo.setHotelId(1L);
        hotelInfo.setName("Gandara");
        hotelInfo.setAddress("Kino 404");
        hotelInfo.setRating(5);
        request.setHotelInfo(hotelInfo);

        ServiceStatus serviceStatus = new ServiceStatus();
        serviceStatus.setStatusCode("BAD_REQUEST");

        when(errorService.checkHotel(request.getHotelInfo().getName(),
                request.getHotelInfo().getAddress(), request.getHotelInfo().getRating())).thenReturn(serviceStatus);
        UpdateHotelResponse found = hotelService.updateHotel(request);

        assertEquals("BAD_REQUEST", found.getServiceStatus().getStatusCode());
    }

    @Test
    public void shouldGetNotFoundByUpdateHotelResponse() {
        UpdateHotelRequest request = new UpdateHotelRequest();

        HotelInfo hotelInfo = new HotelInfo();
        hotelInfo.setHotelId(10L);
        request.setHotelInfo(hotelInfo);

        ServiceStatus serviceStatus = new ServiceStatus();
        serviceStatus.setStatusCode("CHECKED");

        when(errorService.checkHotel(request.getHotelInfo().getName(),
                request.getHotelInfo().getAddress(), request.getHotelInfo().getRating())).thenReturn(serviceStatus);
        when(hotelRepository.findByHotelId(request.getHotelInfo().getHotelId())).thenReturn(null);
        UpdateHotelResponse found = hotelService.updateHotel(request);

        assertEquals("NOT_FOUND", found.getServiceStatus().getStatusCode());
        assertEquals("Hotel Not Found", found.getServiceStatus().getMessage());
    }

    @Test
    public void shouldGetBadRequestByUpdateHotelResponse() {
        UpdateHotelRequest request = new UpdateHotelRequest();

        HotelInfo hotelInfo = new HotelInfo();
        hotelInfo.setHotelId(1L);
        hotelInfo.setName("Gandara");
        hotelInfo.setAddress("Kino 404");
        hotelInfo.setRating(5);
        request.setHotelInfo(hotelInfo);

        Hotel hotel = new Hotel(1L, "Gandara", "Kino 404", 5);
        Set<Amenity> amenities = new HashSet<>();
        hotel.setAmenities(amenities);

        List<Hotel> hotelList = new ArrayList<>();
        hotelList.add(hotel);

        ServiceStatus serviceStatus = new ServiceStatus();
        serviceStatus.setStatusCode("CHECKED");

        when(errorService.checkHotel(request.getHotelInfo().getName(),
                request.getHotelInfo().getAddress(), request.getHotelInfo().getRating())).thenReturn(serviceStatus);
        when(hotelRepository.findByHotelId(request.getHotelInfo().getHotelId())).thenReturn(hotel);
        when(hotelRepository.findByNameAndAddressAndRating(hotel.getName(), hotel.getAddress(), hotel.getRating())).thenReturn(hotelList);
        UpdateHotelResponse found = hotelService.updateHotel(request);

        assertEquals("BAD_REQUEST", found.getServiceStatus().getStatusCode());
        assertEquals("Hotel Information Already Available on DB", found.getServiceStatus().getMessage());
    }

    @Test
    public void shouldUpdateHotelResponse() {
        UpdateHotelRequest request = new UpdateHotelRequest();

        HotelInfo hotelInfo = new HotelInfo();
        hotelInfo.setHotelId(1L);
        hotelInfo.setName("Gandara");
        hotelInfo.setAddress("Kino 404");
        hotelInfo.setRating(5);
        request.setHotelInfo(hotelInfo);

        Hotel hotel = new Hotel(1L, "Gandara", "Kino 404", 5);
        Set<Amenity> amenities = new HashSet<>();
        hotel.setAmenities(amenities);
        Amenity amenity = new Amenity();
        amenity.setAmenityId(1L);
        amenity.setName("Gym");
        hotel.getAmenities().add(amenity);

        List<Hotel> hotelList = new ArrayList<>();

        ServiceStatus serviceStatus = new ServiceStatus();
        serviceStatus.setStatusCode("CHECKED");

        when(errorService.checkHotel(request.getHotelInfo().getName(),
                request.getHotelInfo().getAddress(), request.getHotelInfo().getRating())).thenReturn(serviceStatus);
        when(hotelRepository.findByHotelId(request.getHotelInfo().getHotelId())).thenReturn(hotel);
        when(hotelRepository.findByNameAndAddressAndRating(hotel.getName(), hotel.getAddress(), hotel.getRating())).thenReturn(hotelList);
        UpdateHotelResponse found = hotelService.updateHotel(request);

        assertEquals("SUCCESS", found.getServiceStatus().getStatusCode());
        assertEquals("Hotel Updated Successfully", found.getServiceStatus().getMessage());
        assertEquals(1L, found.getHotelInfo().getHotelId());
        assertEquals("Gandara", found.getHotelInfo().getName());
        assertEquals("Kino 404", found.getHotelInfo().getAddress());
        assertEquals(5, found.getHotelInfo().getRating());
        assertEquals(1L, found.getHotelInfo().getAmenityInfo().get(0).getAmenityId());
        assertEquals("Gym", found.getHotelInfo().getAmenityInfo().get(0).getName());
    }

    @Test
    public void shouldGetNotFoundByDeleteHotelResponse() {
        DeleteHotelRequest request = new DeleteHotelRequest();
        request.setHotelId(10L);

        when(hotelRepository.findByHotelId(request.getHotelId())).thenReturn(null);
        DeleteHotelResponse found = hotelService.deleteHotel(request);

        assertEquals("NOT_FOUND", found.getServiceStatus().getStatusCode());
        assertEquals("Hotel Not Found", found.getServiceStatus().getMessage());
    }

    @Test
    public void shouldDeleteHotelResponse() {
        DeleteHotelRequest request = new DeleteHotelRequest();
        request.setHotelId(1L);

        Hotel hotel = new Hotel(1L, "Gandara", "Kino 404", 5);
        Set<Amenity> amenities = new HashSet<>();
        hotel.setAmenities(amenities);

        when(hotelRepository.findByHotelId(request.getHotelId())).thenReturn(hotel);
        DeleteHotelResponse found = hotelService.deleteHotel(request);

        assertEquals("SUCCESS", found.getServiceStatus().getStatusCode());
        assertEquals("Hotel Deleted Successfully", found.getServiceStatus().getMessage());
    }

    @Test
    public void shouldThrowDataAccessResourceFailureExceptionWhenDeletingHotelResponse() {
        DeleteHotelRequest request = new DeleteHotelRequest();
        request.setHotelId(1L);

        when(hotelRepository.findByHotelId(Mockito.anyLong())).thenThrow(DataAccessResourceFailureException.class);
        DeleteHotelResponse found = hotelService.deleteHotel(request);

        assertEquals("INTERNAL_SERVER_ERROR", found.getServiceStatus().getStatusCode());
    }

    @Test
    public void shouldThrowRuntimeExceptionWhenDeletingHotelResponse() {
        DeleteHotelRequest request = new DeleteHotelRequest();
        request.setHotelId(1L);

        when(hotelRepository.findByHotelId(Mockito.anyLong())).thenThrow(RuntimeException.class);
        DeleteHotelResponse found = hotelService.deleteHotel(request);

        assertEquals("INTERNAL_SERVER_ERROR", found.getServiceStatus().getStatusCode());
        assertEquals("There was an error when deleting hotel!", found.getServiceStatus().getMessage());
    }

    @Test
    public void shouldGetHotelNotFoundByAddAmenityResponse() {
        AddAmenityRequest request = new AddAmenityRequest();
        request.setHotelId(10L);

        when(hotelRepository.findByHotelId(request.getHotelId())).thenReturn(null);
        AddAmenityResponse found = hotelService.addAmenity(request);

        assertEquals("NOT_FOUND", found.getServiceStatus().getStatusCode());
        assertEquals("Hotel Not Found", found.getServiceStatus().getMessage());
    }

    @Test
    public void shouldGetAmenityNotFoundByAddAmenityResponse() {
        AddAmenityRequest request = new AddAmenityRequest();
        request.setHotelId(1L);
        request.setAmenityId(20L);

        Hotel hotel = new Hotel(1L, "Gandara", "Kino 404", 5);
        Set<Amenity> amenities = new HashSet<>();
        hotel.setAmenities(amenities);

        when(hotelRepository.findByHotelId(request.getHotelId())).thenReturn(hotel);
        when(amenityService.findAmenity(request.getAmenityId())).thenReturn(null);
        AddAmenityResponse found = hotelService.addAmenity(request);

        assertEquals("NOT_FOUND", found.getServiceStatus().getStatusCode());
        assertEquals("Amenity Not Found", found.getServiceStatus().getMessage());
    }

    @Test
    public void shouldGetBadRequestByAddAmenityResponse() {
        AddAmenityRequest request = new AddAmenityRequest();
        request.setHotelId(1L);
        request.setAmenityId(1L);

        Hotel hotel = new Hotel(1L, "Gandara", "Kino 404", 5);
        Set<Amenity> amenities = new HashSet<>();
        hotel.setAmenities(amenities);
        Amenity amenity = new Amenity();
        amenity.setAmenityId(1L);
        amenity.setName("Gym");
        hotel.getAmenities().add(amenity);

        when(hotelRepository.findByHotelId(request.getHotelId())).thenReturn(hotel);
        when(amenityService.findAmenity(request.getAmenityId())).thenReturn(amenity);
        AddAmenityResponse found = hotelService.addAmenity(request);

        assertEquals("BAD_REQUEST", found.getServiceStatus().getStatusCode());
        assertEquals("Amenity Already Available on Hotel", found.getServiceStatus().getMessage());
    }

    @Test
    public void shouldGetAddAmenityResponse() {
        AddAmenityRequest request = new AddAmenityRequest();
        request.setHotelId(1L);
        request.setAmenityId(1L);

        Hotel hotel = new Hotel(1L, "Gandara", "Kino 404", 5);
        Set<Amenity> amenities = new HashSet<>();
        hotel.setAmenities(amenities);
        Amenity amenity = new Amenity();
        amenity.setAmenityId(1L);
        amenity.setName("Gym");

        when(hotelRepository.findByHotelId(request.getHotelId())).thenReturn(hotel);
        when(amenityService.findAmenity(request.getAmenityId())).thenReturn(amenity);
        AddAmenityResponse found = hotelService.addAmenity(request);

        assertEquals("SUCCESS", found.getServiceStatus().getStatusCode());
        assertEquals("Amenity Added Successfully", found.getServiceStatus().getMessage());
        assertEquals(1L, found.getAmenityInfo().getAmenityId());
        assertEquals("Gym", found.getAmenityInfo().getName());
    }

    @Test
    public void shouldGetHotelNotFoundByDeleteAmenityResponse() {
        DeleteAmenityRequest request = new DeleteAmenityRequest();
        request.setHotelId(10L);

        when(hotelRepository.findByHotelId(request.getHotelId())).thenReturn(null);
        DeleteAmenityResponse found = hotelService.deleteAmenity(request);

        assertEquals("NOT_FOUND", found.getServiceStatus().getStatusCode());
        assertEquals("Hotel Not Found", found.getServiceStatus().getMessage());
    }

    @Test
    public void shouldGetAmenityNotFoundByDeleteAmenityResponse() {
        DeleteAmenityRequest request = new DeleteAmenityRequest();
        request.setHotelId(1L);
        request.setAmenityId(20L);

        Hotel hotel = new Hotel(1L, "Gandara", "Kino 404", 5);
        Set<Amenity> amenities = new HashSet<>();
        hotel.setAmenities(amenities);

        when(hotelRepository.findByHotelId(request.getHotelId())).thenReturn(hotel);
        when(amenityService.findAmenity(request.getAmenityId())).thenReturn(null);
        DeleteAmenityResponse found = hotelService.deleteAmenity(request);

        assertEquals("NOT_FOUND", found.getServiceStatus().getStatusCode());
        assertEquals("Amenity Not Found", found.getServiceStatus().getMessage());
    }

    @Test
    public void shouldGetBadRequestByDeleteAmenityResponse() {
        DeleteAmenityRequest request = new DeleteAmenityRequest();
        request.setHotelId(1L);
        request.setAmenityId(1L);

        Hotel hotel = new Hotel(1L, "Gandara", "Kino 404", 5);
        Set<Amenity> amenities = new HashSet<>();
        hotel.setAmenities(amenities);
        Amenity amenity = new Amenity();
        amenity.setAmenityId(1L);
        amenity.setName("Gym");

        when(hotelRepository.findByHotelId(request.getHotelId())).thenReturn(hotel);
        when(amenityService.findAmenity(request.getAmenityId())).thenReturn(amenity);
        DeleteAmenityResponse found = hotelService.deleteAmenity(request);

        assertEquals("BAD_REQUEST", found.getServiceStatus().getStatusCode());
        assertEquals("Amenity Not Available on Hotel", found.getServiceStatus().getMessage());
    }

    @Test
    public void shouldGetDeleteAmenityResponse() {
        DeleteAmenityRequest request = new DeleteAmenityRequest();
        request.setHotelId(1L);
        request.setAmenityId(1L);

        Hotel hotel = new Hotel(1L, "Gandara", "Kino 404", 5);
        Set<Amenity> amenities = new HashSet<>();
        hotel.setAmenities(amenities);
        Amenity amenity = new Amenity();
        amenity.setAmenityId(1L);
        amenity.setName("Gym");
        hotel.getAmenities().add(amenity);

        when(hotelRepository.findByHotelId(request.getHotelId())).thenReturn(hotel);
        when(amenityService.findAmenity(request.getAmenityId())).thenReturn(amenity);
        DeleteAmenityResponse found = hotelService.deleteAmenity(request);

        assertEquals("SUCCESS", found.getServiceStatus().getStatusCode());
        assertEquals("Amenity Deleted Successfully", found.getServiceStatus().getMessage());
    }
}
