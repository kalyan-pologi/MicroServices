package com.microservices.rating.repositories;

import com.microservices.rating.entities.Rating;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RatingRepository extends MongoRepository<Rating, String> {

     List<Rating> findByUserId(String userId);

     List<Rating> findByHotelId(String HotelId);
}
