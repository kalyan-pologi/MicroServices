package com.microservices.user.config;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JwtAuthRequest {
    private String email;
    private String password;
}
