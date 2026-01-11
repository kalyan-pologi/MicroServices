package com.microservices.user.controllers;

import com.microservices.user.entities.User;
import com.microservices.user.exceptions.ResourceNotFoundException;
import com.microservices.user.services.UserService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    private final AtomicInteger retryCount = new AtomicInteger(1);

    private Logger logger = LoggerFactory.getLogger(UserController.class);

    // get all users
    @GetMapping
    public ResponseEntity<List<User>> getAllUser(){
        logger.info("Get single user handler: UserController");
        List<User> allUsers = userService.getAllUser();
        return ResponseEntity.ok(allUsers);
    }

    // get single user
//    int retryCount = 1;
    @GetMapping("/{userId}")
//    // calling another service so we need to use circuit breaker to handle fault tolerance
    @CircuitBreaker(name = "ratingHotelBeaker", fallbackMethod = "ratingHotelFallback")
    @Retry(name = "ratingHotelService", fallbackMethod = "ratingHotelFallback" )
    @RateLimiter(name = "userRateLimiter")
//    // if the service is taking more time then we can use time limiter to get the response.
//    // here we are using 2 second as a time limit. if the service is taking more than 2 second then it will throw exception
//    @TimeLimiter(name = "userTimeLimiter", fallbackMethod = "ratingHotelFallback")
    public ResponseEntity<User> getSingleUser(@PathVariable String userId){
        logger.info("Retry Count: {}", retryCount);
        retryCount.incrementAndGet();
        User user = userService.getUser(userId);
        logger.info("Get single user handler: UserController");
        retryCount.set(1);
        return ResponseEntity.ok(user);
    }

    // fallback method for circuit breaker
    public ResponseEntity<User> ratingHotelFallback(String userId, Exception ex){
        log.error("Fallback is executed because service is down :", userId, ex.getMessage());
        User fallBackUser = User.builder()
                .email("fallback@gmail.com")
                .name("service unavailable or down")
                .about("unable to fetch data right now. External service is down")
                .userId(userId)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(fallBackUser);
    }

    // create user
    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody User user){
        log.info("CReating new user with email: {}", user.getEmail());
        User savedUser = userService.saveUser(user);
        log.info("user crreated successfully with id: {}", savedUser.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    // update user
    @PutMapping("/{userId}")
    public ResponseEntity<User> updateUser(@PathVariable String userId, @Valid @RequestBody User userDetails) {
        log.info("Updating user with id: {}", userId);
        User updatedUser = userService.updateUser(userId, userDetails);
        return ResponseEntity.ok(updatedUser);
    }

    // delete user
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable String userId) {
        log.info("Deleting user with id: {}", userId);
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

//    @PostMapping("/s3/upload")
//    public ResponseEntity<String> s3UploadFile(@RequestParam("file") MultipartFile file) throws IOException {
//        userService.uploadFile(file);
//        return ResponseEntity.ok("File uploaded successfully");
//    }
//
//    @GetMapping("/s3/download/{filename}")
//    public ResponseEntity<byte[]> s3DownloadFile(@PathVariable String filename){
//        byte[] data = userService.downloadFile(filename);
//        return ResponseEntity.ok()
//                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
//                .body(data);
//    }
}
