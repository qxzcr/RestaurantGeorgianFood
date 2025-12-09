package com.example.restaurant.ui;

import com.example.restaurant.model.Ingredient;
import com.example.restaurant.service.InventoryService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

@Route(value = "inventory", layout = MainLayout.class)
@PageTitle("Inventory | Kinto")
@RolesAllowed("ADMIN")
public class InventoryView extends VerticalLayout {

    private final InventoryService inventoryService;
    private final Grid<Ingredient> grid = new Grid<>(Ingredient.class, false);

    public InventoryView(InventoryService inventoryService) {
        this.inventoryService = inventoryService;

        addClassName("inventory-view");
        setSizeFull();
        setPadding(true);

        H1 title = new H1(getTranslation("inventory.title", "Inventory Management"));

        Button addBtn = new Button(getTranslation("btn.add", "Add Ingredient"), VaadinIcon.PLUS.create());
        addBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addBtn.addClickListener(e -> openEditor(new Ingredient()));

        configureGrid();

        add(title, addBtn, grid);
        updateGrid();
    }

    private void configureGrid() {
        grid.addClassName("inventory-grid");
        grid.setSizeFull();
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);

        grid.addColumn(Ingredient::getName).setHeader(getTranslation("form.ingredient.name", "Name")).setSortable(true);

        grid.addColumn(ingredient -> ingredient.getCurrentStock() + " " + ingredient.getUnit())
                .setHeader(getTranslation("form.ingredient.stock", "Stock"))
                .setSortable(true);

        // Using getMinimumThreshold
        grid.addColumn(Ingredient::getMinimumThreshold)
                .setHeader(getTranslation("form.ingredient.min", "Min Level"));

        // Status Column with Badge
        grid.addComponentColumn(ingredient -> {
            Span badge = new Span();
            if (inventoryService.isLowStock(ingredient)) {
                badge.setText(getTranslation("inventory.status.low", "Low Stock"));
                badge.getElement().getThemeList().add("badge error");
            } else {
                badge.setText(getTranslation("inventory.status.ok", "OK"));
                badge.getElement().getThemeList().add("badge success");
            }
            return badge;
        }).setHeader("Status");

        // Actions
        grid.addComponentColumn(ingredient -> {
            Button editBtn = new Button(VaadinIcon.EDIT.create(), e -> openEditor(ingredient));
            editBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

            Button deleteBtn = new Button(VaadinIcon.TRASH.create(), e -> {
                inventoryService.deleteIngredient(ingredient.getId());
                updateGrid();
            });
            deleteBtn.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);

            return new HorizontalLayout(editBtn, deleteBtn);
        });
    }

    private void updateGrid() {
        grid.setItems(inventoryService.findAllIngredients());
    }

    private void openEditor(Ingredient ingredient) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle(ingredient.getId() == null ? "New Ingredient" : "Edit Ingredient");

        TextField name = new TextField("Name");
        TextField unit = new TextField("Unit (kg, l, pcs)");
        NumberField stock = new NumberField("Current Stock");
        NumberField minStock = new NumberField("Min Stock Alert");

        Binder<Ingredient> binder = new Binder<>(Ingredient.class);
        binder.bind(name, Ingredient::getName, Ingredient::setName);
        binder.bind(unit, Ingredient::getUnit, Ingredient::setUnit);
        binder.bind(stock, Ingredient::getCurrentStock, Ingredient::setCurrentStock);

        binder.bind(minStock, Ingredient::getMinimumThreshold, Ingredient::setMinimumThreshold);

        binder.setBean(ingredient);

        FormLayout formLayout = new FormLayout(name, unit, stock, minStock);
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));

        Button saveBtn = new Button(getTranslation("btn.save", "Save"), e -> {
            if (binder.validate().isOk()) {
                inventoryService.saveIngredient(ingredient);
                updateGrid();
                dialog.close();
                Notification.show("Saved", 3000, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            }
        });
        saveBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancelBtn = new Button(getTranslation("btn.cancel", "Cancel"), e -> dialog.close());

        dialog.add(formLayout);
        dialog.getFooter().add(cancelBtn, saveBtn);
        dialog.open();
    }
}