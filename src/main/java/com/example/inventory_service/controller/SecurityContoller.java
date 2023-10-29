package com.example.inventory_service.controller;

import com.example.inventory_service.domain.User;
import com.example.inventory_service.dto.LoginRequestDto;
import com.example.inventory_service.dto.LoginResponseDto;
import com.example.inventory_service.dto.UserRegistrationDto;
import com.example.inventory_service.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class SecurityContoller {

    private final UserService userService;

    private final JwtEncoder encoder;

    private final AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public String registerUser(@RequestBody @Valid UserRegistrationDto userRegistrationDto) {
        log.info("Registering new user with mail {} isAdmin {}", userRegistrationDto.getEmail(), userRegistrationDto.isAdmin());
        userService.registerNewUser(userRegistrationDto);
        return "User registered successfully";
    }

    @PostMapping("/signin")
    public LoginResponseDto authenticateUser(@Valid @RequestBody LoginRequestDto loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        User user = (User) authentication.getPrincipal();
        String token = getToken(user);
        return new LoginResponseDto(token);
    }

    private String getToken(User user) {
        Instant now = Instant.now();
        long expiry = 300L;
        String scope = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiry))
                .subject(user.getUsername())
                .claim("scope", scope)
                .build();
        return this.encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }


}
