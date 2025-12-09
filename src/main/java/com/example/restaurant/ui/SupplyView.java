package com.example.restaurant.ui;

import com.example.restaurant.model.*;
import com.example.restaurant.service.InventoryService;
import com.example.restaurant.service.SupplyService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

@Route(value = "supply", layout = MainLayout.class)
@PageTitle("Supply Manager | Kinto")
@RolesAllowed({"ADMIN", "INVENTORY_MANAGER"})
public class SupplyView extends VerticalLayout {

    private final SupplyService supplyService;
    private final InventoryService inventoryService;

    private Grid<Supplier> supplierGrid;
    private Grid<SupplyOrder> orderGrid;

    public SupplyView(SupplyService supplyService, InventoryService inventoryService) {
        this.supplyService = supplyService;
        this.inventoryService = inventoryService;

        addClassName("admin-view");
        setSizeFull();

        add(new H1("Supply Chain Management"));

        // --- SUPPLIERS SECTION ---
        add(new H2("Suppliers"));
        Button addSupplierBtn = new Button("New Supplier", VaadinIcon.PLUS.create(), e -> openSupplierDialog(new Supplier()));

        supplierGrid = new Grid<>(Supplier.class, false);
        supplierGrid.addColumn(Supplier::getName).setHeader("Name");
        supplierGrid.addColumn(Supplier::getEmail).setHeader("Email");
        supplierGrid.addColumn(Supplier::getPhone).setHeader("Phone");

        supplierGrid.addComponentColumn(supplier -> {
            Button editBtn = new Button(VaadinIcon.EDIT.create(), e -> openSupplierDialog(supplier));
            editBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

            Button deleteBtn = new Button(VaadinIcon.TRASH.create(), e -> {
                supplyService.deleteSupplier(supplier.getId());
                refresh();
                Notification.show("Supplier deleted");
            });
            deleteBtn.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);

            return new HorizontalLayout(editBtn, deleteBtn);
        }).setHeader("Actions");

        add(addSupplierBtn, supplierGrid);

        // --- ORDERS SECTION ---
        add(new H2("Supply Orders"));
        Button addOrderBtn = new Button("New Order", VaadinIcon.CART.create(), e -> openOrderDialog());

        orderGrid = new Grid<>(SupplyOrder.class, false);
        orderGrid.addColumn(o -> o.getSupplier().getName()).setHeader("Supplier");
        orderGrid.addColumn(o -> o.getIngredient().getName()).setHeader("Ingredient");
        orderGrid.addColumn(SupplyOrder::getQuantity).setHeader("Qty");

        orderGrid.addComponentColumn(o -> {
            String status = o.getStatus().name();
            Span badge = new Span(status);
            if ("RECEIVED".equals(status)) badge.getElement().getThemeList().add("badge success");
            else if ("CANCELLED".equals(status)) badge.getElement().getThemeList().add("badge error");
            else if ("SENT".equals(status)) badge.getElement().getThemeList().add("badge primary");
            else badge.getElement().getThemeList().add("badge contrast");
            return badge;
        }).setHeader("Status");

        // --- ОБНОВЛЕННАЯ КОЛОНКА ДЕЙСТВИЙ ---
        orderGrid.addComponentColumn(order -> {
            HorizontalLayout actions = new HorizontalLayout();

            // 1. Отправить (если создан)
            if (order.getStatus() == SupplyStatus.CREATED) {
                Button sendBtn = new Button("Send", VaadinIcon.PAPERPLANE.create(), e -> {
                    supplyService.sendOrder(order.getId());
                    refresh();
                    Notification.show("Order Sent!");
                });
                sendBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL);
                actions.add(sendBtn);
            }

            // 2. Принять (если отправлен)
            if (order.getStatus() == SupplyStatus.SENT) {
                Button receiveBtn = new Button("Receive", e -> {
                    supplyService.markReceived(order.getId());
                    refresh();
                    Notification.show("Stock updated!");
                });
                receiveBtn.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_SMALL);
                actions.add(receiveBtn);
            }

            // 3. Отменить (если не принят и не отменен)
            if (order.getStatus() != SupplyStatus.RECEIVED && order.getStatus() != SupplyStatus.CANCELLED) {
                Button cancelBtn = new Button(VaadinIcon.BAN.create(), e -> {
                    supplyService.cancelOrder(order.getId());
                    refresh();
                    Notification.show("Order Cancelled");
                });
                cancelBtn.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_SMALL);
                actions.add(cancelBtn);
            }

            // 4. Удалить (всегда)
            Button deleteBtn = new Button(VaadinIcon.TRASH.create(), e -> {
                supplyService.deleteOrder(order.getId());
                refresh();
                Notification.show("Order deleted");
            });
            deleteBtn.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);
            actions.add(deleteBtn);

            return actions;
        }).setHeader("Actions").setAutoWidth(true);

        add(addOrderBtn, orderGrid);
        refresh();
    }

    private void refresh() {
        supplierGrid.setItems(supplyService.findAllSuppliers());
        orderGrid.setItems(supplyService.findAllOrders());
    }

    private void openSupplierDialog(Supplier supplier) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle(supplier.getId() == null ? "Add Supplier" : "Edit Supplier");
        TextField name = new TextField("Name"); name.setValue(supplier.getName() == null ? "" : supplier.getName());
        TextField email = new TextField("Email"); email.setValue(supplier.getEmail() == null ? "" : supplier.getEmail());
        TextField phone = new TextField("Phone"); phone.setValue(supplier.getPhone() == null ? "" : supplier.getPhone());

        Button save = new Button("Save", e -> {
            supplier.setName(name.getValue());
            supplier.setEmail(email.getValue());
            supplier.setPhone(phone.getValue());
            if (supplier.getId() == null) supplyService.saveSupplier(supplier);
            else supplyService.updateSupplier(supplier.getId(), supplier);
            refresh();
            dialog.close();
        });
        Button cancel = new Button("Cancel", e -> dialog.close());
        dialog.add(new VerticalLayout(name, email, phone));
        dialog.getFooter().add(cancel, save);
        dialog.open();
    }

    private void openOrderDialog() {
        Dialog dialog = new Dialog("Create Supply Order");
        ComboBox<Supplier> supplierSelect = new ComboBox<>("Supplier");
        supplierSelect.setItems(supplyService.findAllSuppliers());
        supplierSelect.setItemLabelGenerator(Supplier::getName);
        ComboBox<Ingredient> ingredientSelect = new ComboBox<>("Ingredient");
        ingredientSelect.setItems(inventoryService.findAllIngredients());
        ingredientSelect.setItemLabelGenerator(Ingredient::getName);
        NumberField qty = new NumberField("Quantity");

        Button save = new Button("Create Order", e -> {
            if (supplierSelect.getValue() != null && ingredientSelect.getValue() != null && qty.getValue() != null) {
                supplyService.createOrder(supplierSelect.getValue(), ingredientSelect.getValue(), qty.getValue());
                refresh();
                dialog.close();
            }
        });
        dialog.add(new VerticalLayout(supplierSelect, ingredientSelect, qty, save));
        dialog.open();
    }
}