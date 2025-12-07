package com.example.restaurant.service;

import com.example.restaurant.model.Dish;
import com.example.restaurant.repository.DishRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DishServiceTest {

    @Mock
    private DishRepository dishRepository;

    @InjectMocks
    private DishService dishService;

    @Test
    @DisplayName("Should return all dishes")
    void shouldFindAllDishes() {
        Dish d1 = new Dish();
        d1.setName("Khinkali");
        when(dishRepository.findAll()).thenReturn(List.of(d1));

        List<Dish> dishes = dishService.findAllDishes();

        assertThat(dishes).hasSize(1);
        assertThat(dishes.get(0).getName()).isEqualTo("Khinkali");
    }

    @Test
    @DisplayName("Should save dish")
    void shouldSaveDish() {
        Dish d = new Dish();
        d.setPrice(BigDecimal.TEN);

        dishService.saveDish(d);

        verify(dishRepository).save(d);
    }

    @Test
    @DisplayName("Should delete dish")
    void shouldDeleteDish() {
        dishService.deleteDish(5L);
        verify(dishRepository).deleteById(5L);
    }
}