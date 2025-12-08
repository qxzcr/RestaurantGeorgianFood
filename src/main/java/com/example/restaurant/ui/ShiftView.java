package com.example.restaurant.ui;

import com.example.restaurant.model.Shift;
import com.example.restaurant.model.User;
import com.example.restaurant.service.ShiftService;
import com.example.restaurant.service.UserService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant; // Добавлено для красивых полосок
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

import java.time.format.DateTimeFormatter;

@Route(value = "shifts", layout = MainLayout.class)
@PageTitle("Staff Schedule | Kinto")
@RolesAllowed({"ADMIN", "MANAGER"})
public class ShiftView extends VerticalLayout {

    private final ShiftService shiftService;
    private final UserService userService;

    private Grid<Shift> shiftGrid;

    public ShiftView(ShiftService shiftService, UserService userService) {
        this.shiftService = shiftService;
        this.userService = userService;

        setSizeFull();
        add(new H1("Staff Schedule Management"));

        // Кнопка добавления
        Button addShiftBtn = new Button("Add Shift", VaadinIcon.PLUS.create(), e -> openShiftDialog(new Shift()));
        addShiftBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        // Настройка таблицы
        shiftGrid = new Grid<>(Shift.class, false);
        shiftGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES); // Полосатые строки для удобства

        // Колонки данных (добавлено setAutoWidth для авто-подбора ширины)
        shiftGrid.addColumn(shift -> shift.getEmployee().getName())
                .setHeader("Employee")
                .setAutoWidth(true);

        shiftGrid.addColumn(shift -> shift.getEmployee().getRole())
                .setHeader("Role")
                .setAutoWidth(true);

        // Красивый формат даты
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE, dd MMM HH:mm");

        shiftGrid.addColumn(s -> s.getStartTime() != null ? formatter.format(s.getStartTime()) : "")
                .setHeader("Start Time")
                .setAutoWidth(true);

        shiftGrid.addColumn(s -> s.getEndTime() != null ? formatter.format(s.getEndTime()) : "")
                .setHeader("End Time")
                .setAutoWidth(true);

        shiftGrid.addColumn(Shift::getNotes)
                .setHeader("Notes")
                .setAutoWidth(true);

        // КОЛОНКА ДЕЙСТВИЙ (Edit / Delete)
        // setFlexGrow(0) и setWidth не дают колонке сжиматься
        shiftGrid.addComponentColumn(shift -> {
            // Кнопка Редактировать
            Button editBtn = new Button(VaadinIcon.EDIT.create(), e -> openShiftDialog(shift));
            editBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

            // Кнопка Удалить
            Button deleteBtn = new Button(VaadinIcon.TRASH.create(), e -> {
                shiftService.deleteShift(shift.getId());
                refresh();
                Notification.show("Shift deleted");
            });
            deleteBtn.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);

            return new HorizontalLayout(editBtn, deleteBtn);
        }).setHeader("Actions").setWidth("150px").setFlexGrow(0); // Фиксированная ширина для кнопок

        add(addShiftBtn, shiftGrid);
        refresh();
    }

    private void refresh() {
        shiftGrid.setItems(shiftService.getAllShifts());
    }

    // Диалог для Создания и Редактирования
    private void openShiftDialog(Shift shift) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle(shift.getId() == null ? "Create Shift" : "Edit Shift");

        ComboBox<User> employeeSelect = new ComboBox<>("Employee");
        employeeSelect.setItems(userService.findAll());
        employeeSelect.setItemLabelGenerator(u -> u.getName() + " (" + u.getRole() + ")");

        // Заполняем поле сотрудника, если редактируем
        if (shift.getEmployee() != null) {
            employeeSelect.setValue(shift.getEmployee());
        }

        DateTimePicker startPicker = new DateTimePicker("Start Time");
        if (shift.getStartTime() != null) startPicker.setValue(shift.getStartTime());

        DateTimePicker endPicker = new DateTimePicker("End Time");
        if (shift.getEndTime() != null) endPicker.setValue(shift.getEndTime());

        TextArea notes = new TextArea("Notes");
        if (shift.getNotes() != null) notes.setValue(shift.getNotes());

        Button save = new Button("Save", e -> {
            try {
                if (shift.getId() == null) {
                    // Создание новой смены
                    shiftService.createShift(
                            employeeSelect.getValue(),
                            startPicker.getValue(),
                            endPicker.getValue(),
                            notes.getValue()
                    );
                } else {
                    // ОБНОВЛЕНИЕ существующей смены
                    shiftService.updateShift(
                            shift.getId(),
                            startPicker.getValue(),
                            endPicker.getValue(),
                            notes.getValue()
                    );
                }
                refresh();
                dialog.close();
                Notification.show("Saved successfully");
            } catch (Exception ex) {
                Notification.show("Error: " + ex.getMessage());
            }
        });
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancel = new Button("Cancel", e -> dialog.close());
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        VerticalLayout dialogLayout = new VerticalLayout(employeeSelect, startPicker, endPicker, notes);
        dialogLayout.setSpacing(true);
        dialogLayout.setPadding(false);

        dialog.add(dialogLayout);
        dialog.getFooter().add(cancel, save);
        dialog.open();
    }
}