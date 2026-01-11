package com.microservices.user.controllers;


import com.microservices.user.config.CustomerUserDetailServiceImpl;
import com.microservices.user.config.JwtAuthRequest;
import com.microservices.user.config.JwtAuthResponse;
import com.microservices.user.config.JwtTokenHelper;
import com.microservices.user.entities.User;
import com.microservices.user.services.impl.UserServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins="*")
@RestController
@RequestMapping("/auth")
public class AuthController{
    @Autowired
    private JwtTokenHelper jwtTokenHelper;
    @Autowired
    private CustomerUserDetailServiceImpl customUserDetailService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserServiceImpl userServiceImpl;
    @PostMapping("/login")
    public ResponseEntity<JwtAuthResponse> createToken(@Valid @RequestBody JwtAuthRequest jwtAuthRequest) throws Exception {
        try{
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(jwtAuthRequest.getEmail(),jwtAuthRequest.getPassword()));
        }
        catch (BadCredentialsException e) {
            throw new Exception("username or password Incorrect");
        }
        catch (InternalAuthenticationServiceException e){
            throw new Exception("Email id not found, please register!!");
        }
        UserDetails userDetails = customUserDetailService.loadUserByUsername(jwtAuthRequest.getEmail());
        String jwt = jwtTokenHelper.generateToken(userDetails);
        JwtAuthResponse jwtAuthResponse = new JwtAuthResponse();
        jwtAuthResponse.setJwt(jwt);
        jwtAuthResponse.setName(userDetails.getUsername());

        return new ResponseEntity<>(jwtAuthResponse, HttpStatus.OK);
    }
    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@Valid  @RequestBody User user) throws Exception {
        User registeredUser = this.userServiceImpl.registerNewUser(user);
        return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
    }
}
