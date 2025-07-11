package com.microservices.hotel.controllers;

import com.microservices.hotel.entities.Hotel;
import com.microservices.hotel.service.HotelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/hotels")
public class HotelController {

    @Autowired
    private HotelService hotelService;

    @PostMapping
    public ResponseEntity<Hotel> createHotel(@RequestBody Hotel hotel){
        Hotel hotel1 = hotelService.create(hotel);
        return ResponseEntity.status(HttpStatus.CREATED).body(hotel1);
    }

    @GetMapping("/{hotelId}")
    public ResponseEntity<Hotel> getHotel(@PathVariable String hotelId){
        Hotel hotel = hotelService.getHotel(hotelId);
        return ResponseEntity.status(HttpStatus.OK).body(hotel);
    }

    @GetMapping
    public ResponseEntity<List<Hotel>> getAllHotels(){
        List<Hotel> hotels = hotelService.getAll();
        return ResponseEntity.ok(hotels);
    }

}
