package com.example.restaurant.ui;

import com.example.restaurant.model.Review;
import com.example.restaurant.service.ReviewService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

@Route(value = "reviews-admin", layout = MainLayout.class)
@PageTitle("Review Moderation | Kinto")
@RolesAllowed({"ADMIN", "MANAGER"})
public class ReviewManagementView extends VerticalLayout {

    private final ReviewService reviewService;
    private Grid<Review> grid;

    public ReviewManagementView(ReviewService reviewService) {
        this.reviewService = reviewService;
        setSizeFull();
        add(new H1("Customer Reviews"));

        grid = new Grid<>(Review.class, false);
        grid.addColumn(r -> r.getDish().getName()).setHeader("Dish");
        grid.addColumn(r -> r.getUser().getEmail()).setHeader("User");
        grid.addColumn(Review::getRating).setHeader("Rating");
        grid.addColumn(Review::getComment).setHeader("Comment");

        grid.addComponentColumn(review -> {
            Button deleteBtn = new Button(VaadinIcon.TRASH.create(), e -> {
                reviewService.deleteReview(review.getId());
                refresh();
                Notification.show("Review deleted");
            });
            deleteBtn.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_SMALL);
            return deleteBtn;
        }).setHeader("Actions");

        add(grid);
        refresh();
    }

    private void refresh() {
        // You might need to add findAll() to ReviewService if it's missing
        // grid.setItems(reviewService.getAllReviews());
    }
}