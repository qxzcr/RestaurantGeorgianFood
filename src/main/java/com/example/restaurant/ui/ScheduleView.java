package com.example.restaurant.ui;

import com.example.restaurant.model.Role;
import com.example.restaurant.model.Shift;
import com.example.restaurant.model.User;
import com.example.restaurant.service.SecurityService;
import com.example.restaurant.service.ShiftService;
import com.example.restaurant.service.UserService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Route(value = "schedule", layout = MainLayout.class)
@PageTitle("Staff Schedule | Kinto")
@RolesAllowed({"ADMIN", "WAITER", "CHEF"})
public class ScheduleView extends VerticalLayout {

    private final ShiftService shiftService;
    private final UserService userService;
    private final SecurityService securityService;

    private Grid<Shift> grid = new Grid<>(Shift.class, false);

    public ScheduleView(ShiftService shiftService, UserService userService, SecurityService securityService) {
        this.shiftService = shiftService;
        this.userService = userService;
        this.securityService = securityService;

        addClassName("schedule-view");
        setSizeFull();

        User currentUser = securityService.getAuthenticatedUser();
        boolean isAdmin = currentUser.getRole() == Role.ADMIN;

        // Header
        HorizontalLayout header = new HorizontalLayout();
        header.setAlignItems(Alignment.CENTER);
        header.add(new H2(isAdmin ? "Staff Schedule Management" : "My Work Schedule"));

        // Only Admin can add shifts
        if (isAdmin) {
            Button addShiftBtn = new Button("Assign Shift", VaadinIcon.PLUS.create(), e -> openShiftDialog());
            addShiftBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            header.add(addShiftBtn);
        }

        configureGrid(isAdmin);
        updateList(isAdmin, currentUser);

        add(header, grid);
    }

    private void configureGrid(boolean isAdmin) {
        grid.setSizeFull();

        // 1. Employee Name (Visible for Admin)
        if (isAdmin) {
            grid.addColumn(shift -> shift.getEmployee().getFullName())
                    .setHeader("Employee")
                    .setSortable(true)
                    .setAutoWidth(true);

            grid.addColumn(shift -> shift.getEmployee().getRole())
                    .setHeader("Role")
                    .setSortable(true)
                    .setWidth("100px");
        }

        // 2. Start Time
        grid.addColumn(shift -> shift.getStartTime().format(DateTimeFormatter.ofPattern("EEE, dd MMM HH:mm")))
                .setHeader("Start Time")
                .setSortable(true)
                .setAutoWidth(true);

        // 3. End Time
        grid.addColumn(shift -> shift.getEndTime().format(DateTimeFormatter.ofPattern("HH:mm")))
                .setHeader("End Time")
                .setAutoWidth(true);

        // 4. Notes
        grid.addColumn(Shift::getNotes).setHeader("Notes");

        // 5. Delete Action (Admin only)
        if (isAdmin) {
            grid.addComponentColumn(shift -> {
                Button deleteBtn = new Button(VaadinIcon.TRASH.create(), e -> {
                    shiftService.deleteShift(shift.getId());
                    updateList(true, null);
                    Notification.show("Shift removed", 3000, Notification.Position.TOP_CENTER);
                });
                deleteBtn.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_SMALL);
                return deleteBtn;
            });
        }
    }

    private void updateList(boolean isAdmin, User currentUser) {
        if (isAdmin) {
            grid.setItems(shiftService.getAllShifts());
        } else {
            grid.setItems(shiftService.getUserShifts(currentUser));
        }
    }

    private void openShiftDialog() {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Assign New Shift");

        // Filter users: Exclude Customers, show only staff
        List<User> staff = userService.findAllUsers().stream()
                .filter(u -> u.getRole() != Role.CUSTOMER)
                .toList();

        ComboBox<User> employeeSelect = new ComboBox<>("Employee");
        employeeSelect.setItems(staff);
        // Show Name and Role in the dropdown
        employeeSelect.setItemLabelGenerator(u -> u.getFullName() + " (" + u.getRole() + ")");
        employeeSelect.setWidthFull();

        DateTimePicker startPicker = new DateTimePicker("Start Time");
        // Default to tomorrow 09:00
        startPicker.setValue(java.time.LocalDateTime.now().plusDays(1).withHour(9).withMinute(0));
        startPicker.setStep(java.time.Duration.ofMinutes(30));

        DateTimePicker endPicker = new DateTimePicker("End Time");
        // Default to tomorrow 17:00
        endPicker.setValue(java.time.LocalDateTime.now().plusDays(1).withHour(17).withMinute(0));
        endPicker.setStep(java.time.Duration.ofMinutes(30));

        TextField notesField = new TextField("Notes");
        notesField.setPlaceholder("e.g. Morning Shift, Main Hall");
        notesField.setWidthFull();

        Button save = new Button("Assign", e -> {
            if (employeeSelect.getValue() == null || startPicker.getValue() == null || endPicker.getValue() == null) {
                Notification.show("Please fill all fields", 3000, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }

            try {
                shiftService.createShift(
                        employeeSelect.getValue(),
                        startPicker.getValue(),
                        endPicker.getValue(),
                        notesField.getValue()
                );
                updateList(true, null);
                dialog.close();
                Notification.show("Shift assigned successfully!", 3000, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            } catch (Exception ex) {
                Notification.show("Error: " + ex.getMessage(), 5000, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        save.setWidthFull();

        Button cancel = new Button("Cancel", e -> dialog.close());
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        cancel.setWidthFull();

        VerticalLayout layout = new VerticalLayout(employeeSelect, startPicker, endPicker, notesField, save, cancel);
        dialog.add(layout);
        dialog.open();
    }
}