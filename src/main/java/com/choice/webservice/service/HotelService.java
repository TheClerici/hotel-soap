package com.choice.webservice.service;

import com.choice.gs_ws.*;
import com.choice.webservice.entity.Amenity;
import com.choice.webservice.entity.Hotel;
import com.choice.webservice.repository.HotelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HotelService {
    private final HotelRepository hotelRepository;
    private final AmenityService amenityService;
    private final ErrorService errorService;

    public GetHotelByIdResponse getHotelById(GetHotelByIdRequest request) {
        GetHotelByIdResponse response = new GetHotelByIdResponse();
        ServiceStatus serviceStatus = new ServiceStatus();
        HotelInfo hotelInfo = new HotelInfo();

        try {
            Hotel hotel = hotelRepository.findByHotelId(request.getHotelId());
            if (hotel == null) {
                serviceStatus.setStatusCode("NOT_FOUND");
                serviceStatus.setMessage("Hotel Not Found");
            } else {
                hotelInfo.setHotelId(hotel.getHotelId());
                hotelInfo.setName(hotel.getName());
                hotelInfo.setAddress(hotel.getAddress());
                hotelInfo.setRating(hotel.getRating());

                for (Amenity amenity : hotel.getAmenities()) {
                    AmenityInfo amenityInfo = new AmenityInfo();
                    amenityInfo.setAmenityId(amenity.getAmenityId());
                    amenityInfo.setName(amenity.getName());
                    hotelInfo.getAmenityInfo().add(amenityInfo);
                }
                response.setHotelInfo(hotelInfo);

                serviceStatus.setStatusCode("SUCCESS");
                serviceStatus.setMessage("Hotel Found in DB");
            }
        } catch (DataAccessResourceFailureException e) {
            serviceStatus.setStatusCode("INTERNAL_SERVER_ERROR");
            serviceStatus.setMessage(e.getMessage());
        } catch (RuntimeException e) {
            serviceStatus.setStatusCode("INTERNAL_SERVER_ERROR");
            serviceStatus.setMessage("There was an error when getting hotel!");
        }
        response.setServiceStatus(serviceStatus);
        return response;
    }

    public GetAllHotelsWithFilteringResponse getAllHotelsWithFiltering(GetAllHotelsWithFilteringRequest request) {
        GetAllHotelsWithFilteringResponse response = new GetAllHotelsWithFilteringResponse();
        List<HotelInfo> hotelInfoList = new ArrayList<>();
        ServiceStatus serviceStatus = errorService.checkPagination(request.getPageNumber(), request.getPageSize());

        if (!serviceStatus.getStatusCode().equals("CHECKED")) {
            response.setServiceStatus(serviceStatus);
            return response;
        } else {
            serviceStatus.setStatusCode("SUCCESS");
            serviceStatus.setMessage("Pagination received successfully");
        }

        try {
            Pageable page = PageRequest.of(request.getPageNumber(), request.getPageSize());
            List<Hotel> hotelList = hotelRepository.findByNameContaining(request.getNameFilter(), page);
            for (Hotel hotel : hotelList) {
                HotelInfo hotelInfo = new HotelInfo();
                hotelInfo.setHotelId(hotel.getHotelId());
                hotelInfo.setName(hotel.getName());
                hotelInfo.setAddress(hotel.getAddress());
                hotelInfo.setRating(hotel.getRating());

                for (Amenity amenity : hotel.getAmenities()) {
                    AmenityInfo amenityInfo = new AmenityInfo();
                    amenityInfo.setAmenityId(amenity.getAmenityId());
                    amenityInfo.setName(amenity.getName());
                    hotelInfo.getAmenityInfo().add(amenityInfo);
                }
                hotelInfoList.add(hotelInfo);
            }
        } catch (DataAccessResourceFailureException e) {
            serviceStatus.setStatusCode("INTERNAL_SERVER_ERROR");
            serviceStatus.setMessage(e.getMessage());
        } catch (RuntimeException e) {
            serviceStatus.setStatusCode("INTERNAL_SERVER_ERROR");
            serviceStatus.setMessage("There was an error when getting hotels!");
        }
        response.setServiceStatus(serviceStatus);
        response.getHotelInfo().addAll(hotelInfoList);
        return response;
    }

    public AddHotelResponse addHotel(AddHotelRequest request) {
        AddHotelResponse response = new AddHotelResponse();
        ServiceStatus serviceStatus = errorService.checkHotel(request.getName(), request.getAddress(), request.getRating());

        if (!serviceStatus.getStatusCode().equals("CHECKED")) {
            response.setServiceStatus(serviceStatus);
            return response;
        }

        Hotel hotel = new Hotel();
        hotel.setName(request.getName());
        hotel.setAddress(request.getAddress());
        hotel.setRating(request.getRating());

        try {
            List<Hotel> hotelList = hotelRepository.findByName(hotel.getName());
            if (hotelList.size() > 0) {
                serviceStatus.setStatusCode("BAD_REQUEST");
                serviceStatus.setMessage("Hotel Already Available on DB");
            } else {
                Hotel savedHotel = hotelRepository.save(hotel);

                HotelInfo hotelInfo = new HotelInfo();
                hotelInfo.setHotelId(savedHotel.getHotelId());
                hotelInfo.setName(savedHotel.getName());
                hotelInfo.setAddress(savedHotel.getAddress());
                hotelInfo.setRating(savedHotel.getRating());

                response.setHotelInfo(hotelInfo);
                serviceStatus.setStatusCode("SUCCESS");
                serviceStatus.setMessage("Hotel Added Successfully");
            }
        } catch (DataAccessResourceFailureException e) {
            serviceStatus.setStatusCode("INTERNAL_SERVER_ERROR");
            serviceStatus.setMessage(e.getMessage());
        } catch (RuntimeException e) {
            serviceStatus.setStatusCode("INTERNAL_SERVER_ERROR");
            serviceStatus.setMessage("There was an error when creating hotel!");
        }
        response.setServiceStatus(serviceStatus);
        return response;
    }

    public UpdateHotelResponse updateHotel(UpdateHotelRequest request) {
        UpdateHotelResponse response = new UpdateHotelResponse();
        ServiceStatus serviceStatus = errorService.checkHotel(request.getHotelInfo().getName(), request.getHotelInfo().getAddress(), request.getHotelInfo().getRating());

        if (!serviceStatus.getStatusCode().equals("CHECKED")) {
            response.setServiceStatus(serviceStatus);
            return response;
        }

        try {
            Hotel hotel = hotelRepository.findByHotelId(request.getHotelInfo().getHotelId());
            if (hotel == null) {
                serviceStatus.setStatusCode("NOT_FOUND");
                serviceStatus.setMessage("Hotel Not Found");
                response.setServiceStatus(serviceStatus);
                return response;
            }

            hotel.setName(request.getHotelInfo().getName());
            hotel.setAddress(request.getHotelInfo().getAddress());
            hotel.setRating(request.getHotelInfo().getRating());

            HotelInfo hotelInfo = new HotelInfo();
            hotelInfo.setHotelId(hotel.getHotelId());
            hotelInfo.setName(hotel.getName());
            hotelInfo.setAddress(hotel.getAddress());
            hotelInfo.setRating(hotel.getRating());

            for (Amenity amenity : hotel.getAmenities()) {
                AmenityInfo amenityInfo = new AmenityInfo();
                amenityInfo.setAmenityId(amenity.getAmenityId());
                amenityInfo.setName(amenity.getName());
                hotelInfo.getAmenityInfo().add(amenityInfo);
            }

            List<Hotel> hotelList = hotelRepository.findByNameAndAddressAndRating(hotel.getName(), hotel.getAddress(), hotel.getRating());
            if (hotelList.size() > 0) {
                serviceStatus.setStatusCode("BAD_REQUEST");
                serviceStatus.setMessage("Hotel Information Already Available on DB");
            } else {
                response.setHotelInfo(hotelInfo);
                serviceStatus.setStatusCode("SUCCESS");
                serviceStatus.setMessage("Hotel Updated Successfully");
                hotelRepository.save(hotel);
            }
        } catch (DataAccessResourceFailureException e) {
            serviceStatus.setStatusCode("INTERNAL_SERVER_ERROR");
            serviceStatus.setMessage(e.getMessage());
        } catch (RuntimeException e) {
            serviceStatus.setStatusCode("INTERNAL_SERVER_ERROR");
            serviceStatus.setMessage("There was an error when updating hotel!");
        }
        response.setServiceStatus(serviceStatus);
        return response;
    }

    public DeleteHotelResponse deleteHotel(DeleteHotelRequest request) {
        DeleteHotelResponse response = new DeleteHotelResponse();
        ServiceStatus serviceStatus = new ServiceStatus();
        try {
            Hotel hotel = hotelRepository.findByHotelId(request.getHotelId());
            if (hotel == null) {
                serviceStatus.setStatusCode("NOT_FOUND");
                serviceStatus.setMessage("Hotel Not Found");
            } else {
                hotelRepository.deleteById(request.getHotelId());
                serviceStatus.setStatusCode("SUCCESS");
                serviceStatus.setMessage("Hotel Deleted Successfully");
            }
        } catch (DataAccessResourceFailureException e) {
            serviceStatus.setStatusCode("INTERNAL_SERVER_ERROR");
            serviceStatus.setMessage(e.getMessage());
        } catch (RuntimeException e) {
            serviceStatus.setStatusCode("INTERNAL_SERVER_ERROR");
            serviceStatus.setMessage("There was an error when deleting hotel!");
        }
        response.setServiceStatus(serviceStatus);
        return response;
    }

    public AddAmenityResponse addAmenity(AddAmenityRequest request) {
        AddAmenityResponse response = new AddAmenityResponse();
        ServiceStatus serviceStatus = new ServiceStatus();
        try {
            Hotel hotel = hotelRepository.findByHotelId(request.getHotelId());
            if (hotel == null) {
                serviceStatus.setStatusCode("NOT_FOUND");
                serviceStatus.setMessage("Hotel Not Found");
                response.setServiceStatus(serviceStatus);
                return response;
            }

            Amenity amenity = amenityService.findAmenity(request.getAmenityId());
            if (amenity == null) {
                serviceStatus.setStatusCode("NOT_FOUND");
                serviceStatus.setMessage("Amenity Not Found");
            } else {
                for (Amenity amenityInHotel : hotel.getAmenities()) {
                    if (amenityInHotel.getAmenityId().equals(amenity.getAmenityId())) {
                        serviceStatus.setStatusCode("BAD_REQUEST");
                        serviceStatus.setMessage("Amenity Already Available on Hotel");
                        response.setServiceStatus(serviceStatus);
                        return response;
                    }
                }
                AmenityInfo amenityInfo = new AmenityInfo();
                amenityInfo.setAmenityId(amenity.getAmenityId());
                amenityInfo.setName(amenity.getName());
                response.setAmenityInfo(amenityInfo);

                hotel.getAmenities().add(amenity);
                hotelRepository.save(hotel);

                serviceStatus.setStatusCode("SUCCESS");
                serviceStatus.setMessage("Amenity Added Successfully");
            }
        } catch (DataAccessResourceFailureException e) {
            serviceStatus.setStatusCode("INTERNAL_SERVER_ERROR");
            serviceStatus.setMessage(e.getMessage());
        } catch (RuntimeException e) {
            serviceStatus.setStatusCode("INTERNAL_SERVER_ERROR");
            serviceStatus.setMessage("There was an error when adding amenity!");
        }
        response.setServiceStatus(serviceStatus);
        return response;
    }

    public DeleteAmenityResponse deleteAmenity(DeleteAmenityRequest request) {
        DeleteAmenityResponse response = new DeleteAmenityResponse();
        ServiceStatus serviceStatus = new ServiceStatus();
        try {
            Hotel hotel = hotelRepository.findByHotelId(request.getHotelId());
            if (hotel == null) {
                serviceStatus.setStatusCode("NOT_FOUND");
                serviceStatus.setMessage("Hotel Not Found");
                response.setServiceStatus(serviceStatus);
                return response;
            }

            Amenity amenity = amenityService.findAmenity(request.getAmenityId());
            if (amenity == null) {
                serviceStatus.setStatusCode("NOT_FOUND");
                serviceStatus.setMessage("Amenity Not Found");
            } else {
                for (Amenity amenityInHotel : hotel.getAmenities()) {
                    if (amenityInHotel.getAmenityId().equals(amenity.getAmenityId())) {
                        hotel.getAmenities().remove(amenity);
                        hotelRepository.save(hotel);

                        serviceStatus.setStatusCode("SUCCESS");
                        serviceStatus.setMessage("Amenity Deleted Successfully");
                        response.setServiceStatus(serviceStatus);
                        return response;
                    }
                }
                serviceStatus.setStatusCode("BAD_REQUEST");
                serviceStatus.setMessage("Amenity Not Available on Hotel");
            }
        } catch (DataAccessResourceFailureException e) {
            serviceStatus.setStatusCode("INTERNAL_SERVER_ERROR");
            serviceStatus.setMessage(e.getMessage());
        } catch (RuntimeException e) {
            serviceStatus.setStatusCode("INTERNAL_SERVER_ERROR");
            serviceStatus.setMessage("There was an error when deleting amenity!");
        }
        response.setServiceStatus(serviceStatus);
        return response;
    }
}