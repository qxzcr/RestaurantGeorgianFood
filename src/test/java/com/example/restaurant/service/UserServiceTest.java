package com.example.restaurant.unit;

import com.example.restaurant.model.User;
import com.example.restaurant.repository.UserRepository;
import com.example.restaurant.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @InjectMocks private UserService userService;

    @Test
    @DisplayName("Save: Should encode password before saving")
    void shouldSaveUser() {
        User user = new User();
        user.setPassword("rawPass");

        when(passwordEncoder.encode("rawPass")).thenReturn("encodedPass");
        when(userRepository.save(any(User.class))).thenReturn(user);

        userService.saveUser(user, "rawPass");

        verify(passwordEncoder).encode("rawPass");
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Find: Should return User if exists")
    void shouldFindByEmail() {
        User user = new User();
        user.setEmail("test@test.com");
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));

        User result = userService.findByEmail("test@test.com");

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("test@test.com");
    }

    @Test
    @DisplayName("Find: Should Throw Exception if user not found (Updated logic)")
    void shouldThrow_WhenEmailNotFound() {
        when(userRepository.findByEmail("ne@tu.com")).thenReturn(Optional.empty());

        // Мы изменили логику ранее, теперь сервис кидает RuntimeException
        assertThrows(RuntimeException.class, () -> userService.findByEmail("ne@tu.com"));
    }
}