package com.microservices.user.utils;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.time.LocalDateTime;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)  // Exclude nulls globally for this class
@JsonIgnoreProperties(ignoreUnknown = true) // Ignore extra JSON fields during deserialization
public class JacksonDto {

    @JsonProperty("user_id")  // Rename field in JSON
    private Long id;

    private String name;

    @JsonIgnore  // Do not serialize/deserialize this field at all
    private String password;

    @JsonInclude(JsonInclude.Include.NON_EMPTY) // Exclude if list is null or empty
    private List<String> roles;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")  // Format date output
    private LocalDateTime registeredAt;

    @JsonView(Views.Public.class)
    private String email;

    @JsonView(Views.Admin.class)
    private String internalNotes;

    @JsonSerialize(using = CustomStatusSerializer.class) // Use custom serializer
    private String status;

    // Default constructor needed for Jackson
    public JacksonDto() {}

    // All args constructor
    public JacksonDto(Long id, String name, String password, List<String> roles, LocalDateTime registeredAt,
                      String email, String internalNotes, String status) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.roles = roles;
        this.registeredAt = registeredAt;
        this.email = email;
        this.internalNotes = internalNotes;
        this.status = status;
    }

    // Getters and setters...
    public static void main(String[] args) throws JsonProcessingException {
        JacksonDto user = new JacksonDto(
                1001L,
                "Kalyan",
                "secret123",
                List.of("USER", "ADMIN"),
                LocalDateTime.of(2025, 10, 1, 15, 30),
                "kalyan@example.com",
                "VIP client",
                "active"
        );

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule()); // ✅ Register the module
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // optional: format as ISO string
        mapper.enable(SerializationFeature.INDENT_OUTPUT); // pretty print

        System.out.println(mapper.writeValueAsString(user));
    }
}
