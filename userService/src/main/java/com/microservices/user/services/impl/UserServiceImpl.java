package com.microservices.user.services.impl;

import com.microservices.user.entities.Hotel;
import com.microservices.user.entities.Rating;
import com.microservices.user.entities.User;
import com.microservices.user.exceptions.ResourceNotFoundException;
import com.microservices.user.external.services.HotelService;
import com.microservices.user.repositories.UserRepository;
import com.microservices.user.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    // We need to create bean in MyConfig class
    @Autowired
    private RestTemplate restTemplate;

    // Use to implement with feign client
    @Autowired
    private HotelService hotelService;

    private Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Override
    public User saveUser(User user) {
        String randomUserId = UUID.randomUUID().toString();
        user.setUserId(randomUserId);
        return userRepository.save(user);
    }

    @Override
    public List<User> getAllUser()
    {
//        Implement RATING SERVICE call using rest template
        return userRepository.findAll();
    }

    @Override
    public User getUser(String userId) {
        // get user from database
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User with given Id is not found on server " + userId));
        // fetch rating of above user from RATING SERVICE
        Rating[] ratingsOfUser = restTemplate.getForObject("http://RATING-SERVICE/ratings/users/" + user.getUserId(), Rating[].class);
//        Rating[] ratingsOfUser = restTemplate.getForObject("http://localhost:8083/ratings/users/" + user.getUserId(), Rating[].class);
        logger.info("{}", (Object) ratingsOfUser);

        assert ratingsOfUser != null;
        List<Rating> ratings = Arrays.stream(ratingsOfUser).toList();
        List<Rating> ratingList = ratings.stream().map(rating -> {
            // API call to HOTEL SERVICE to get the hotel
            // http://localhost:8082/hotels/d34e534b-c585-42ce-978e-1d67827fb573
//            ResponseEntity<Hotel> forEntity = restTemplate.getForEntity("http://HOTEL-SERVICE/hotels/"+rating.getHotelId(), Hotel.class);
//            ResponseEntity<Hotel> forEntity = restTemplate.getForEntity("http://localhost:8082/hotels/"+rating.getHotelId(), Hotel.class);
//            Hotel hotel = forEntity.getBody();
//            logger.info("response status code: {}", forEntity.getStatusCode());

            // Using feign client
            Hotel hotel = hotelService.getHotel(rating.getHotelId());
            // set the hotel to rating
            rating.setHotel(hotel);
            // return the rating
            return rating;
        }).collect(Collectors.toList());
        user.setRatings(ratingList);
        return user;
    }
}
