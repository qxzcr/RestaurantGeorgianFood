//// src/main/java/com/example/restaurant/ui/MenuView.java
//package com.example.restaurant.ui;
//
//import com.example.restaurant.model.Dish;
//import com.example.restaurant.model.DishCategory;
//import com.example.restaurant.service.DishService;
//import com.vaadin.flow.component.html.*;
//import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
//import com.vaadin.flow.component.orderedlayout.VerticalLayout;
//import com.vaadin.flow.component.tabs.Tab;
//import com.vaadin.flow.component.tabs.Tabs;
//import com.vaadin.flow.router.PageTitle;
//import com.vaadin.flow.router.Route;
//import com.vaadin.flow.server.auth.AnonymousAllowed;
//
//import java.util.List;
//
//@Route(value = "menu", layout = MainLayout.class)
//@PageTitle("Menu | Kinto")
//@AnonymousAllowed
//public class MenuView extends VerticalLayout {
//
//    private final DishService dishService;
//    private Div dishGrid; // Контейнер для карточек блюд
//
//    public MenuView(DishService dishService) {
//        this.dishService = dishService;
//        addClassName("menu-view");
//        setSpacing(false);
//        setAlignItems(Alignment.CENTER); // Центрируем контент
//
//        add(new H1("Our Menu"));
//
//        // 1. Создаем вкладки-фильтры
//        Tabs filterTabs = createFilterTabs();
//
//        // 2. Создаем контейнер-сетку для блюд
//        dishGrid = new Div();
//        dishGrid.addClassName("menu-grid");
//
//        add(filterTabs, dishGrid);
//
//        // 3. Загружаем "All Dishes" по умолчанию
//        updateDishGrid(null);
//    }
//
//    /**
//     * Создает вкладки для фильтрации
//     */
//    private Tabs createFilterTabs() {
//        Tab allDishes = new Tab("All Dishes");
//        Tab starters = new Tab("Starters");
//        Tab mainCourses = new Tab("Main Courses");
//        Tab desserts = new Tab("Desserts");
//        Tab drinks = new Tab("Drinks");
//
//        Tabs tabs = new Tabs(allDishes, starters, mainCourses, desserts, drinks);
//        tabs.addClassName("menu-filters"); // Стилизация в CSS
//
//        // Логика фильтрации при нажатии
//        tabs.addSelectedChangeListener(event -> {
//            Tab selectedTab = event.getSelectedTab();
//            if (selectedTab.equals(allDishes)) {
//                updateDishGrid(null); // null означает "все категории"
//            } else if (selectedTab.equals(starters)) {
//                updateDishGrid(DishCategory.STARTER);
//            } else if (selectedTab.equals(mainCourses)) {
//                updateDishGrid(DishCategory.MAIN_COURSE);
//            } else if (selectedTab.equals(desserts)) {
//                updateDishGrid(DishCategory.DESSERT);
//            } else if (selectedTab.equals(drinks)) {
//                updateDishGrid(DishCategory.DRINK);
//            }
//        });
//
//        tabs.setSelectedTab(allDishes); // "All Dishes" выбраны по умолчанию
//        return tabs;
//    }
//
//    /**
//     * Очищает и заново заполняет сетку блюд
//     */
//    private void updateDishGrid(DishCategory category) {
//        dishGrid.removeAll(); // Очищаем старые блюда
//
//        List<Dish> dishes;
//        if (category == null) {
//            dishes = dishService.findAllDishes(); // Загружаем все
//        } else {
//            dishes = dishService.findDishesByCategory(category); // Загружаем по категории
//        }
//
//        for (Dish dish : dishes) {
//            dishGrid.add(createDishCard(dish));
//        }
//    }
//
//    /**
//     * (ВОТ ИСПРАВЛЕНИЕ!)
//     * Создает одну карточку блюда, с проверкой на null
//     */
//    private VerticalLayout createDishCard(Dish dish) {
//        VerticalLayout card = new VerticalLayout();
//        card.addClassName("dish-card");
//        card.setPadding(false);
//        card.setSpacing(false);
//
//        // (ВОТ ИСПРАВЛЕНИЕ!)
//        // Проверяем, есть ли у блюда картинка, ПЕРЕД тем как ее создать
//        if (dish.getImageUrl() != null && !dish.getImageUrl().isEmpty()) {
//            Image image = new Image(dish.getImageUrl(), dish.getName());
//            image.addClassName("dish-card-image");
//            card.add(image); // Добавляем картинку
//        }
//
//        // Контейнер для текста (всегда добавляется)
//        VerticalLayout content = new VerticalLayout();
//        content.addClassName("dish-card-content");
//
//        HorizontalLayout header = new HorizontalLayout();
//        header.addClassName("dish-card-header");
//        header.setWidthFull();
//        header.setJustifyContentMode(JustifyContentMode.BETWEEN);
//
//        H3 name = new H3(dish.getName());
//        Span price = new Span(String.format("$%.2f", dish.getPrice()));
//        price.addClassName("dish-card-price");
//
//        header.add(name, price);
//
//        Paragraph description = new Paragraph(dish.getDescription());
//        description.addClassName("dish-card-description");
//
//        content.add(header, description);
//        card.add(content); // Добавляем текстовый контент
//        return card;
//    }
//}
package com.example.restaurant.ui;

import com.example.restaurant.model.Dish;
import com.example.restaurant.model.DishCategory;
import com.example.restaurant.service.DishService;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import java.util.List;

@Route(value = "menu", layout = MainLayout.class)
@PageTitle("Menu | Kinto")
@AnonymousAllowed
public class MenuView extends VerticalLayout {

    private final DishService dishService;
    private Div dishGrid;

    public MenuView(DishService dishService) {
        this.dishService = dishService;
        addClassName("menu-view");
        setSpacing(false);
        setAlignItems(Alignment.CENTER);

        add(new H1(getTranslation("menu.title")));

        Tabs filterTabs = createFilterTabs();

        dishGrid = new Div();
        dishGrid.addClassName("menu-grid");

        add(filterTabs, dishGrid);

        updateDishGrid(null);
    }

    private Tabs createFilterTabs() {
        Tab allDishes = new Tab(getTranslation("menu.all"));
        Tab starters = new Tab(getTranslation("menu.starters"));
        Tab mainCourses = new Tab(getTranslation("menu.main"));
        Tab desserts = new Tab(getTranslation("menu.dessert"));
        Tab drinks = new Tab(getTranslation("menu.drinks"));

        Tabs tabs = new Tabs(allDishes, starters, mainCourses, desserts, drinks);
        tabs.addClassName("menu-filters");

        tabs.addSelectedChangeListener(event -> {
            Tab selectedTab = event.getSelectedTab();
            if (selectedTab.equals(allDishes)) {
                updateDishGrid(null);
            } else if (selectedTab.equals(starters)) {
                updateDishGrid(DishCategory.STARTER);
            } else if (selectedTab.equals(mainCourses)) {
                updateDishGrid(DishCategory.MAIN_COURSE);
            } else if (selectedTab.equals(desserts)) {
                updateDishGrid(DishCategory.DESSERT);
            } else if (selectedTab.equals(drinks)) {
                updateDishGrid(DishCategory.DRINK);
            }
        });

        tabs.setSelectedTab(allDishes);
        return tabs;
    }

    private void updateDishGrid(DishCategory category) {
        dishGrid.removeAll();

        List<Dish> dishes;
        if (category == null) {
            dishes = dishService.findAllDishes();
        } else {
            dishes = dishService.findDishesByCategory(category);
        }

        for (Dish dish : dishes) {
            dishGrid.add(createDishCard(dish));
        }
    }

    private VerticalLayout createDishCard(Dish dish) {
        VerticalLayout card = new VerticalLayout();
        card.addClassName("dish-card");
        card.setPadding(false);
        card.setSpacing(false);

        if (dish.getImageUrl() != null && !dish.getImageUrl().isEmpty()) {
            Image image = new Image(dish.getImageUrl(), dish.getName());
            image.addClassName("dish-card-image");
            card.add(image);
        }

        VerticalLayout content = new VerticalLayout();
        content.addClassName("dish-card-content");

        HorizontalLayout header = new HorizontalLayout();
        header.addClassName("dish-card-header");
        header.setWidthFull();
        header.setJustifyContentMode(JustifyContentMode.BETWEEN);

        H3 name = new H3(dish.getName());
        Span price = new Span(String.format("$%.2f", dish.getPrice()));
        price.addClassName("dish-card-price");

        header.add(name, price);

        Paragraph description = new Paragraph(dish.getDescription());
        description.addClassName("dish-card-description");

        content.add(header, description);
        card.add(content);
        return card;
    }
}