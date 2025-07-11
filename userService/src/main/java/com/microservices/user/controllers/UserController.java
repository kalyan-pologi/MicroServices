package com.microservices.user.controllers;

import com.microservices.user.entities.User;
import com.microservices.user.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {


    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<User>> getAllUser(){
        List<User> allUsers = userService.getAllUser();
        return ResponseEntity.ok(allUsers);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<User> getSingleUser(@PathVariable String userId){
        User user = userService.getUser(userId);
        return ResponseEntity.ok(user);
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user){
        User user1 = userService.saveUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(user1);
    }


}
