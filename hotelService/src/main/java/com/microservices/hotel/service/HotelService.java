package com.microservices.hotel.service;

import com.microservices.hotel.entities.Hotel;
import org.springframework.stereotype.Service;

import java.util.List;

public interface HotelService {

    Hotel create(Hotel hotel);

    List<Hotel> getAll();

    Hotel getHotel(String id);

}
