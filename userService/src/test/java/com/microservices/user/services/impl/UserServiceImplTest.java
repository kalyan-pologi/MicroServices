package com.microservices.user.services.impl;

import com.microservices.user.entities.Hotel;
import com.microservices.user.entities.Rating;
import com.microservices.user.entities.User;
import com.microservices.user.exceptions.DuplicateResourceException;
import com.microservices.user.external.services.HotelService;
import com.microservices.user.repositories.UserRepository;
import com.microservices.user.services.UserService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Method;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private RestTemplate restTemplate;
    @Mock private HotelService hotelService;

    @InjectMocks private UserServiceImpl userService;

    private User user;

    @BeforeAll
    static void init(){
        System.out.println("before all");
    }

    @AfterAll
    static void close(){
        System.out.println("After all");
    }

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUserId("1");
        user.setName("testing");
        user.setEmail("testing@gmail.com");
    }

    @AfterEach
    void tearDown() {
        System.out.println("after each");
    }
    @Test
    void saveUser_success_whenEmailUniqueAndNoId() {
        // Arrange
        System.out.println("saving user");

        when(userRepository.existsByEmail("testing@gmail.com")).thenReturn(false);
        when(userRepository.save(user)).thenReturn(user);
        // Act
        User savedUser = userService.saveUser(user);
        // Assert
        assertEquals(user.getUserId(), savedUser.getUserId());
        assertNotNull(savedUser);
        assertEquals(user.getName(), savedUser.getName());
        assertTrue(savedUser.getUserId().equals("1"));
        verify(userRepository, times(1)).existsByEmail("testing@gmail.com");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void saveUser_throwsException_whenUserNameInvalid() {
        // Arrange
        User user = new User();
        user.setEmail("test@example.com");
        user.setName("");  // Invalid name
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class, () -> userService.saveUser(user));
        assertEquals("Invalid name of user", ex.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void validateUserName_reflectionTest() throws Exception {
        Method method = UserServiceImpl.class.getDeclaredMethod("validateUserName", String.class);
        method.setAccessible(true); // Bypass private access
        boolean result = (boolean) method.invoke(userService, "John Doe");
        assertTrue(result);
    }
    @Test
    void validateUserInvalidName_reflectionTest() throws Exception {
        Method method = UserServiceImpl.class.getDeclaredMethod("validateUserName", String.class);
        method.setAccessible(true); // Bypass private access
        boolean result = (boolean) method.invoke(userService, "");
        assertFalse(result);
    }

    @Test
    void saveUser_throwsException_whenEmailAlreadyExists() {
        // Arrange
        User user = new User();
        user.setEmail("duplicate@example.com");
        when(userRepository.existsByEmail("duplicate@example.com")).thenReturn(true);
        // Act & Assert
        DuplicateResourceException ex = assertThrows(DuplicateResourceException.class,
                () -> userService.saveUser(user));
        assertTrue(ex.getMessage().contains("duplicate@example.com"));
        verify(userRepository, times(1)).existsByEmail("duplicate@example.com");
        verify(userRepository, never()).save(any());
    }

    @Test
    void saveUser_success_whenEmailIsNull() {
        // Arrange
        User user = new User();
        user.setName("No Email User");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        // Act
        User savedUser = userService.saveUser(user);
        // Assert
        assertNotNull(savedUser.getUserId());
        assertNull(savedUser.getEmail());
        verify(userRepository, never()).existsByEmail(any());
        verify(userRepository, times(1)).save(any(User.class));
    }
//    @Test
//    void getUser_success_withRatingsAndHotels() {
//        Rating rating = new Rating("r1", "u1", "h1", 5, "Nice stay", null);
//        Rating[] ratings = new Rating[] { rating };
//        Hotel hotel = new Hotel("h1", "Hilton", "NY", "Good hotel");
//
//        when(userRepository.findById("u1")).thenReturn(Optional.of(user));
//        when(restTemplate.getForObject(anyString(), eq(Rating[].class))).thenReturn(ratings);
//        when(hotelService.getHotel("h1")).thenReturn(hotel);
//
//        User result = userService.getUser("u1");
//
//        assertEquals("u1", result.getUserId());
//        assertEquals(1, result.getRatings().size());
//        assertEquals("Hilton", result.getRatings().get(0).getHotel().getName());
//    }

    @Test
    void getAllUser() {
    }

    @Test
    void getUser() {
    }

    @Test
    void updateUser() {
    }

    @Test
    void deleteUser() {

        User user = new User();
        user.setUserId("1");
        user.setName("testing");
        user.setEmail("testing@gmail.com");
        when(userRepository.findById("1")).thenReturn(Optional.of(user));
        doNothing().when(userRepository).delete(user);
        userService.deleteUser("1");
        verify(userRepository, times(1)).findById("1");
        verify(userRepository, times(1)).delete(user);verify(userRepository, times(1)).delete(user);

    }
}