package com.microservices.user.services;

import com.microservices.user.entities.User;
import jakarta.validation.Valid;

import java.util.List;

public interface UserService {
    User saveUser(User user);

    List<User> getAllUser();

    User getUser(String userId);

    User updateUser(String userId, @Valid User userDetails);

    void deleteUser(String userId);
}
