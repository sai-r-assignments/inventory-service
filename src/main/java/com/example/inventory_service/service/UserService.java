package com.example.inventory_service.service;

import com.example.inventory_service.domain.User;
import com.example.inventory_service.dto.UserRegistrationDto;
import com.example.inventory_service.exception.DataAlreadyExistsException;
import com.example.inventory_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserDetailsService {

    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmailIgnoreCase(username);
        if (user == null) {
            log.error("User not found with given mail {}", username);
            throw new UsernameNotFoundException("User not found with given mail " + username);
        }
        return user;
    }

    @Transactional
    public void registerNewUser(UserRegistrationDto registrationDto) {
        if (userRepository.existsByEmailIgnoreCase(registrationDto.getEmail())) {
            log.error("User with email id {} already exists", registrationDto.getEmail());
            throw new DataAlreadyExistsException("User with email id " + registrationDto.getEmail() + " already exists");
        }
        User user = getUser(registrationDto);
        userRepository.save(user);
    }

    private User getUser(UserRegistrationDto registrationDto) {
        User user = new User();
        user.setName(registrationDto.getName());
        user.setEmail(registrationDto.getEmail());
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        user.setAdmin(registrationDto.isAdmin());
        return user;
    }
}
