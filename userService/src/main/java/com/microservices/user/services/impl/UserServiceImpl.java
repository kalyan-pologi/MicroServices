package com.microservices.user.services.impl;

import com.microservices.user.entities.Hotel;
import com.microservices.user.entities.Rating;
import com.microservices.user.entities.User;
import com.microservices.user.exceptions.DuplicateResourceException;
import com.microservices.user.exceptions.ResourceNotFoundException;
import com.microservices.user.external.services.HotelService;
import com.microservices.user.repositories.UserRepository;
import com.microservices.user.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
//    @Lazy
    private UserRepository userRepository;

    // We need to create bean in MyConfig class
    @Autowired
    private RestTemplate restTemplate;

    // Use to implement with feign client
    @Autowired
    private HotelService hotelService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ModelMapper modelMapper;

//    @Autowired
//    private S3Client s3Client;

//    @Value("${aws.bucket.name}")
//    private String bucketName;


    private static final String RATING_SERVICE_URL = "http://RATING-SERVICE/ratings/users/";
//    private static final String RATING_SERVICE_URL = "http://localhost:8083/ratings/users/";

    private Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    // Create user
    @Transactional(propagation = Propagation.REQUIRED,
    isolation = Isolation.READ_COMMITTED,
    rollbackFor = Exception.class,
    timeout = 5)
    @Override
    public User saveUser(User user) {
        if(user.getEmail() != null && userRepository.existsByEmail(user.getEmail())){
                throw new DuplicateResourceException("User with given email already exist on server: " + user.getEmail());
        }
        if(user.getUserId() == null || user.getUserId().isEmpty()) {
            user.setUserId(UUID.randomUUID().toString());
        }
        boolean validateUser = validateUserName(user.getName());
        if(validateUser){
            log.info("Saving user with id: {}", user.getUserId());
            return userRepository.save(user);
        }
        else {
            throw new RuntimeException("Invalid name of user");
        }
    }

    private boolean validateUserName(String name){
        return name != null && !name.isEmpty();
    }

    // Get all users
    @Override
    public List<User> getAllUser()
    {
        log.info("Fetching all users");
//        Implement RATING SERVICE call using rest template
        return userRepository.findAll();
    }

    // Get single user with ratings and hotel details
    @Transactional(readOnly = true)
    public User getUser(String userId) {
        logger.info("Fetching user with ID: {}", userId);

        // Get user from database with proper exception handling
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User with given Id is not found on server: " + userId));

        try {
            // Fetch ratings of user from RATING SERVICE
            Rating[] ratingsOfUser = restTemplate.getForObject(RATING_SERVICE_URL + user.getUserId(), Rating[].class
            );

            // Handle null response from rating service
            if (ratingsOfUser == null || ratingsOfUser.length == 0) {
                logger.warn("No ratings found for user: {}", userId);
                user.setRatings(new ArrayList<>());
                return user;
            }

            logger.info("Found {} ratings for user: {}", ratingsOfUser.length, userId);

            // Process ratings and fetch hotel details using Feign Client
            List<Rating> ratingList = Arrays.stream(ratingsOfUser)
                    .map(rating -> {
                        try {
                            // Using Feign Client to get hotel details
                            Hotel hotel = hotelService.getHotel(rating.getHotelId());
                            rating.setHotel(hotel);
                            logger.debug("Hotel details fetched for hotelId: {}", rating.getHotelId());
                        } catch (Exception e) {
                            // Log error but don't fail entire request
                            logger.error("Failed to fetch hotel details for hotelId: {}. Error: {}",
                                    rating.getHotelId(), e.getMessage());
                            // Set null or default hotel object
                            rating.setHotel(null);
                        }
                        return rating;
                    })
                    .collect(Collectors.toList());

            user.setRatings(ratingList);
            logger.info("Successfully fetched user with {} ratings", ratingList.size());

        } catch (Exception e) {
            logger.error("Error fetching ratings for user: {}. Error: {}", userId, e.getMessage());
            // Return user without ratings rather than failing completely
            user.setRatings(new ArrayList<>());
        }
        return user;
    }

   // Update user
    @Transactional(
            propagation = Propagation.REQUIRED,
            isolation = Isolation.READ_COMMITTED,
            rollbackFor = Exception.class
    )
    public User updateUser(String userId, User userDetails) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // Check for duplicate email (excluding current user)
        if (userDetails.getEmail() != null &&
                !user.getEmail().equals(userDetails.getEmail()) &&
                userRepository.existsByEmail(userDetails.getEmail())) {
            throw new DuplicateResourceException("Email " + userDetails.getEmail() + " is already in use");
        }

        user.setName(userDetails.getName());
        user.setEmail(userDetails.getEmail());
        user.setAbout(userDetails.getAbout());

        log.info("Updating user: {}", userId);
        return userRepository.save(user);
    }

    // delete user
    @Transactional(
            propagation = Propagation.REQUIRED,
            rollbackFor = Exception.class
    )
    public void deleteUser(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        log.info("Deleting user: {}", userId);
        userRepository.delete(user);
    }
    @Override
    public User registerNewUser(User user) throws Exception {
        Optional<User> getUser =  userRepository.findByEmail(user.getEmail());
        if(getUser.isPresent()){
            throw new Exception("Email already exits...please login");
        }
        else {
            // encoded the password
            user.setPassword(this.passwordEncoder.encode(user.getPassword()));
            // add roles if needed
            User newUser = this.userRepository.save(user);
            return newUser;
        }
    }

//    @Override
//    public void uploadFile(MultipartFile file) throws IOException {
//        s3Client.putObject(PutObjectRequest.builder()
//                .bucket(bucketName)
//                .key(file.getOriginalFilename())
//                .build(),
//                RequestBody.fromBytes(file.getBytes()));
//    }
//
//    @Override
//    public byte[] downloadFile(String key) {
//        ResponseBytes<GetObjectResponse> objectAsBytes = s3Client.getObjectAsBytes(GetObjectRequest.builder()
//                .bucket(bucketName)
//                .key(key)
//                .build());
//        return objectAsBytes.asByteArray();
//    }

}
