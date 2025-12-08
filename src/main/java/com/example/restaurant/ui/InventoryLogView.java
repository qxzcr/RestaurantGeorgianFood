package com.example.restaurant.ui;

import com.example.restaurant.model.InventoryLog;
import com.example.restaurant.repository.InventoryLogRepository;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

import java.time.format.DateTimeFormatter;

@Route(value = "inventory-logs", layout = MainLayout.class)
@PageTitle("Inventory History | Kinto")
@RolesAllowed({"ADMIN", "INVENTORY_MANAGER"})
public class InventoryLogView extends VerticalLayout {

    private final InventoryLogRepository logRepository;

    public InventoryLogView(InventoryLogRepository logRepository) {
        this.logRepository = logRepository;

        setSizeFull();
        add(new H1("Inventory Logs"));

        Grid<InventoryLog> grid = new Grid<>(InventoryLog.class, false);

        grid.addColumn(log -> log.getIngredient().getName()).setHeader("Ingredient");
        grid.addColumn(InventoryLog::getChangeAmount).setHeader("Change Qty");
        grid.addColumn(InventoryLog::getReason).setHeader("Reason");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        grid.addColumn(log -> log.getTimestamp().format(formatter)).setHeader("Time");

        grid.setItems(logRepository.findAllByOrderByTimestampDesc()); // Ensure this method exists in Repo

        add(grid);
    }
}