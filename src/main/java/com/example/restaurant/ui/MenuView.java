package com.example.restaurant.ui;

import com.example.restaurant.model.Dish;
import com.example.restaurant.model.DishCategory;
import com.example.restaurant.service.DishService;
import com.example.restaurant.service.ReviewService; // <--- NEW IMPORT
import com.example.restaurant.service.SecurityService; // <--- NEW IMPORT
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import java.util.List;

@Route(value = "menu", layout = MainLayout.class)
@PageTitle("Menu | Kinto")
@AnonymousAllowed
public class MenuView extends VerticalLayout {

    private final DishService dishService;
    private final ReviewService reviewService; // <--- NEW FIELD
    private final SecurityService securityService; // <--- NEW FIELD

    private Div dishGrid;

    public MenuView(DishService dishService, ReviewService reviewService, SecurityService securityService) {
        this.dishService = dishService;
        this.reviewService = reviewService; // <--- ASSIGN
        this.securityService = securityService; // <--- ASSIGN

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

        // --- RATING & REVIEW SECTION ---
        double avgRating = reviewService.getAverageRating(dish.getId());
        String starEmoji = "⭐";
        Span ratingSpan = new Span(starEmoji + " " + String.format("%.1f", avgRating));
        ratingSpan.getStyle().set("color", "#f1c40f").set("font-weight", "bold");

        HorizontalLayout metaLayout = new HorizontalLayout(ratingSpan);
        metaLayout.setAlignItems(Alignment.CENTER);

        // Show review button only if logged in
        if (securityService.getAuthenticatedUser() != null) {
            Button reviewBtn = new Button("Review", e -> openReviewDialog(dish));
            reviewBtn.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY);
            metaLayout.add(reviewBtn);
        }
        // -------------------------------

        Paragraph description = new Paragraph(dish.getDescription());
        description.addClassName("dish-card-description");

        content.add(header, metaLayout, description); // Added metaLayout here
        card.add(content);
        return card;
    }

    private void openReviewDialog(Dish dish) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Rate " + dish.getName());

        IntegerField rateField = new IntegerField("Rating (1-5)");
        rateField.setMin(1);
        rateField.setMax(5);
        rateField.setValue(5);
        rateField.setStepButtonsVisible(true);

        TextArea commentField = new TextArea("Comment");
        commentField.setWidthFull();

        Button saveBtn = new Button("Submit", e -> {
            Integer rating = rateField.getValue();
            if (rating == null || rating < 1 || rating > 5) {
                Notification.show("Please select a valid rating (1-5)");
                return;
            }

            reviewService.addReview(
                    securityService.getAuthenticatedUser(),
                    dish,
                    rating,
                    commentField.getValue()
            );
            dialog.close();
            updateDishGrid(null); // Refresh grid to update rating
            Notification.show("Thanks for your review!");
        });
        saveBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        VerticalLayout layout = new VerticalLayout(rateField, commentField, saveBtn);
        dialog.add(layout);
        dialog.open();
    }
}