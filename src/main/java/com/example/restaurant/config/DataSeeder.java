package com.example.restaurant.config;

import com.example.restaurant.model.*;
import com.example.restaurant.repository.DishRepository;
import com.example.restaurant.repository.IngredientRepository;
import com.example.restaurant.repository.SupplierRepository;
import com.example.restaurant.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final DishRepository dishRepository;
    private final UserService userService;
    private final IngredientRepository ingredientRepository;
    private final SupplierRepository supplierRepository;

    @Override
    public void run(String... args) throws Exception {
        // Проверка, чтобы не дублировать данные при перезапуске
        // (Особенно важно для PostgreSQL, так как данные там сохраняются)

        System.out.println(">>> STARTING DATA SEEDING <<<");

        // =============================================================
        // 1. USERS
        // =============================================================
        createUserIfNotFound("admin@kinto.com", "admin", "Admin Kinto", "000000000", Role.ADMIN);
        createUserIfNotFound("waiter@kinto.com", "waiter", "Kate Blossom", "111111111", Role.WAITER);
        createUserIfNotFound("chef@kinto.com", "chef", "Armani Kitaio", "222222222", Role.CHEF);
        createUserIfNotFound("manager@kinto.com", "manager", "George Manager", "333333333", Role.INVENTORY_MANAGER);

        // =============================================================
        // 2. INGREDIENTS
        // =============================================================
        if (ingredientRepository.count() == 0) {
            System.out.println(">>> Seeding Ingredients...");
            ingredientRepository.save(Ingredient.builder().name("Flour").currentStock(50.0).unit("kg").minimumThreshold(10.0).build());
            ingredientRepository.save(Ingredient.builder().name("Cheese").currentStock(20.0).unit("kg").minimumThreshold(5.0).build());
            ingredientRepository.save(Ingredient.builder().name("Pork").currentStock(30.0).unit("kg").minimumThreshold(5.0).build());
            ingredientRepository.save(Ingredient.builder().name("Walnuts").currentStock(15.0).unit("kg").minimumThreshold(2.0).build());
            ingredientRepository.save(Ingredient.builder().name("Eggplant").currentStock(10.0).unit("kg").minimumThreshold(3.0).build());
        }

        // =============================================================
        // 3. SUPPLIERS
        // =============================================================
        if (supplierRepository.count() == 0) {
            System.out.println(">>> Seeding Suppliers...");
            supplierRepository.save(Supplier.builder().name("Georgian Fresh Farms").email("contact@geofresh.ge").phone("+995123456789").build());
            supplierRepository.save(Supplier.builder().name("Caucasus Meat Co.").email("sales@caucasusmeat.ge").phone("+995987654321").build());
        }

        // =============================================================
        // 4. DISHES
        // =============================================================
        if (dishRepository.count() == 0) {
            System.out.println(">>> Seeding Dishes...");
            // Starters
            dishRepository.save(createDish("Khachapuri Imeruli", "Traditional Georgian cheese bread.", "12.50", DishCategory.STARTER, "khachapuri.jpg"));
            dishRepository.save(createDish("Pkhali Assorti", "Walnut and vegetable appetizers.", "10.00", DishCategory.STARTER, "pkhali.jpg"));
            dishRepository.save(createDish("Badrijani Nigvzit", "Eggplant rolls with walnut.", "11.00", DishCategory.STARTER, "badrijani.jpg"));

            // Mains
            dishRepository.save(createDish("Khinkali (5 pcs)", "Dumplings with spiced meat.", "15.00", DishCategory.MAIN_COURSE, "khinkali.jpg"));
            dishRepository.save(createDish("Mtsvadi", "Grilled pork skewers.", "22.00", DishCategory.MAIN_COURSE, "mtsvadi.jpg"));
            dishRepository.save(createDish("Lobio", "Bean stew in clay pot.", "14.00", DishCategory.MAIN_COURSE, "lobio.jpg"));

            // Desserts & Drinks
            dishRepository.save(createDish("Churchkhela", "Grape and walnut candy.", "7.00", DishCategory.DESSERT, "churchkhela.jpg"));
            dishRepository.save(createDish("Saperavi Wine", "Dry red wine.", "9.00", DishCategory.DRINK, "saperavi.jpg"));
        }

        System.out.println(">>> DATA SEEDING COMPLETE <<<");
    }

    private void createUserIfNotFound(String email, String password, String name, String phone, Role role) {
        try {
            userService.findByEmail(email);
        } catch (RuntimeException e) {
            System.out.println(">>> Creating user: " + email);
            User user = User.builder()
                    .email(email)
                    .fullName(name)
                    .phone(phone)
                    .role(role)
                    .build();
            // UserService сам зашифрует пароль, если вы используете метод saveUser или register
            userService.saveUser(user, password);
        }
    }

    private Dish createDish(String name, String desc, String price, DishCategory cat, String img) {
        return Dish.builder()
                .name(name)
                .description(desc)
                .price(new BigDecimal(price))
                .category(cat)
                .imageUrl("/images/dishes/" + img)
                .build();
    }
}