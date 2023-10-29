package com.example.inventory_service.service;

import com.example.inventory_service.domain.User;
import com.example.inventory_service.dto.UserRegistrationDto;
import com.example.inventory_service.exception.DataAlreadyExistsException;
import com.example.inventory_service.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private static final String USER_NAME = "user-name";
    private static final String USER_MAIL = "user@mail.com";
    private static final String USER_PWD = "user_password";
    private static final String USER_PWD_ENCODED = "user_password_encoded";
    private static final User USER = new User();
    private static final UserRegistrationDto REGISTRATION_DTO = new UserRegistrationDto(USER_NAME, USER_MAIL, USER_PWD, false);

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRepository userRepository;

    @Captor
    private ArgumentCaptor<User> userArgumentCaptor;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(passwordEncoder, userRepository);
    }

    @Test
    void load_by_user_name_user_exists() {
        when(userRepository.findByEmailIgnoreCase(USER_MAIL))
                .thenReturn(USER);
        assertEquals(USER, userService.loadUserByUsername(USER_MAIL));
        verify(userRepository).findByEmailIgnoreCase(USER_MAIL);
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void load_by_user_name_user_not_exists_throws_UsernameNotFoundException() {
        Exception e = assertThrows(UsernameNotFoundException.class,
                () -> userService.loadUserByUsername(USER_MAIL));
        assertTrue(e.getMessage().contains(USER_MAIL));
        verify(userRepository).findByEmailIgnoreCase(USER_MAIL);
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void register_new_user() {
        when(userRepository.existsByEmailIgnoreCase(USER_MAIL))
                .thenReturn(false);
        when(passwordEncoder.encode(USER_PWD))
                .thenReturn(USER_PWD_ENCODED);
        userService.registerNewUser(REGISTRATION_DTO);
        verify(userRepository).existsByEmailIgnoreCase(USER_MAIL);
        verify(userRepository).save(userArgumentCaptor.capture());
        verify(passwordEncoder).encode(USER_PWD);
        verifyNoMoreInteractions(userRepository, passwordEncoder);
        User savedUser = userArgumentCaptor.getValue();
        assertEquals(USER_NAME, savedUser.getName());
        assertEquals(USER_MAIL, savedUser.getEmail());
        assertEquals(USER_PWD_ENCODED, savedUser.getPassword());
        assertFalse(savedUser.isAdmin());
    }

    @Test
    void register_new_user_throws_DataAlreadyExistsException_when_user_exists_with_given_mail() {
        when(userRepository.existsByEmailIgnoreCase(USER_MAIL))
                .thenReturn(true);
        Exception e = assertThrows(DataAlreadyExistsException.class,
                () -> userService.registerNewUser(REGISTRATION_DTO));
        assertTrue(e.getMessage().contains(USER_MAIL));
        verify(userRepository).existsByEmailIgnoreCase(USER_MAIL);
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(passwordEncoder);
    }

    static {
        USER.setName(USER_NAME);
        USER.setEmail(USER_MAIL);
        USER.setPassword(USER_PWD);
    }
}
