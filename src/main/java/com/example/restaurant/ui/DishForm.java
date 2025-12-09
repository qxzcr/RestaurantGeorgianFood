package com.example.restaurant.ui;

import com.example.restaurant.model.Dish;
import com.example.restaurant.model.DishCategory;
import com.example.restaurant.model.DishIngredient;
import com.example.restaurant.model.Ingredient;
import com.example.restaurant.service.ImageUploadService;
import com.example.restaurant.service.InventoryService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon; // <--- ADDED MISSING IMPORT
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.shared.Registration;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class DishForm extends FormLayout {

    private final ImageUploadService imageUploadService;
    private final InventoryService inventoryService;

    H2 title = new H2();
    TextField name = new TextField();
    TextArea description = new TextArea();
    BigDecimalField price = new BigDecimalField();
    ComboBox<DishCategory> category = new ComboBox<>();

    Image previewImage = new Image();
    Upload imageUpload;
    MemoryBuffer buffer = new MemoryBuffer();
    private String currentImageUrl = "";
    private byte[] uploadedImageData;

    // --- RECIPE SECTION ---
    private Grid<DishIngredient> recipeGrid;
    private List<DishIngredient> currentIngredients = new ArrayList<>();
    private ComboBox<Ingredient> ingredientSelector;
    private NumberField quantityField;

    Binder<Dish> binder = new BeanValidationBinder<>(Dish.class);

    Button save = new Button();
    Button delete = new Button();
    Button close = new Button();

    public DishForm(ImageUploadService imageUploadService, InventoryService inventoryService) {
        this.imageUploadService = imageUploadService;
        this.inventoryService = inventoryService;

        addClassName("dish-form");

        // Translations
        title.setText(getTranslation("btn.edit", "Edit Dish"));
        name.setLabel(getTranslation("form.dish.name", "Name"));
        description.setLabel(getTranslation("form.dish.desc", "Description"));
        price.setLabel(getTranslation("form.dish.price", "Price"));
        category.setLabel(getTranslation("form.dish.category", "Category"));

        save.setText(getTranslation("btn.save", "Save"));
        delete.setText(getTranslation("btn.delete", "Delete"));
        close.setText(getTranslation("btn.cancel", "Cancel"));

        category.setItems(DishCategory.values());
        price.setPrefixComponent(new Span("$"));

        setupImageUpload();
        configureRecipeSection();

        binder.bindInstanceFields(this);

        add(
                title,
                name,
                description,
                price,
                category,
                new VerticalLayout(new Span(getTranslation("form.dish.image", "Image")), previewImage, imageUpload),
                createRecipeLayout(),
                createButtonLayout()
        );
    }

    // --- RECIPE LOGIC ---
    private void configureRecipeSection() {
        recipeGrid = new Grid<>(DishIngredient.class, false);
        recipeGrid.addColumn(di -> di.getIngredient().getName()).setHeader("Ingredient");
        recipeGrid.addColumn(di -> di.getQuantity() + " " + di.getIngredient().getUnit()).setHeader("Qty Needed");

        recipeGrid.addComponentColumn(di -> {
            // Use explicit addClickListener instead of constructor lambda
            Button removeBtn = new Button(VaadinIcon.TRASH.create());
            removeBtn.addClickListener(e -> {
                currentIngredients.remove(di);
                recipeGrid.setItems(currentIngredients);
            });
            removeBtn.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_SMALL);
            return removeBtn;
        });
        recipeGrid.setHeight("200px");

        ingredientSelector = new ComboBox<>("Add Ingredient");
        ingredientSelector.setItems(inventoryService.findAllIngredients());
        ingredientSelector.setItemLabelGenerator(Ingredient::getName);
        ingredientSelector.setWidth("60%");

        quantityField = new NumberField("Qty");
        quantityField.setWidth("30%");
    }

    private Component createRecipeLayout() {
        HorizontalLayout controls = new HorizontalLayout(ingredientSelector, quantityField, createAddIngredientBtn());
        controls.setAlignItems(HorizontalLayout.Alignment.END);

        VerticalLayout layout = new VerticalLayout();
        layout.setPadding(false);
        layout.add(new H4("Recipe / Ingredients"), controls, recipeGrid);
        return layout;
    }

    private Button createAddIngredientBtn() {
        // Use explicit addClickListener
        Button btn = new Button(VaadinIcon.PLUS.create());
        btn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btn.addClickListener(e -> {
            Ingredient ing = ingredientSelector.getValue();
            Double qty = quantityField.getValue();
            if (ing != null && qty != null && qty > 0) {
                DishIngredient di = DishIngredient.builder()
                        .ingredient(ing)
                        .quantity(qty)
                        .build();
                // Note: 'dish' is set on save
                currentIngredients.add(di);
                recipeGrid.setItems(currentIngredients);

                ingredientSelector.clear();
                quantityField.clear();
            }
        });
        return btn;
    }

    // --- IMAGE LOGIC ---
    private void setupImageUpload() {
        imageUpload = new Upload(buffer);
        imageUpload.setAcceptedFileTypes("image/jpeg", "image/png", "image/webp");
        imageUpload.setMaxFiles(1);
        previewImage.setVisible(false);
        imageUpload.addSucceededListener(event -> {
            try (InputStream inputStream = buffer.getInputStream()) {
                uploadedImageData = inputStream.readAllBytes();
                StreamResource resource = new StreamResource(event.getFileName(), () -> new ByteArrayInputStream(uploadedImageData));
                previewImage.setSrc(resource);
                previewImage.setVisible(true);
                currentImageUrl = imageUploadService.saveImage(event.getFileName(), new ByteArrayInputStream(uploadedImageData));
            } catch (Exception e) { e.printStackTrace(); }
        });
    }
    private void clearImage() { previewImage.setSrc(""); previewImage.setVisible(false); currentImageUrl = ""; }

    // --- FORM LOGIC ---
    public void setDish(Dish dish) {
        binder.setBean(dish);
        currentIngredients.clear();

        if (dish != null) {
            if (dish.getIngredients() != null) {
                currentIngredients.addAll(dish.getIngredients());
            }
            recipeGrid.setItems(currentIngredients);

            if (dish.getImageUrl() != null && !dish.getImageUrl().isEmpty()) {
                currentImageUrl = dish.getImageUrl();
                previewImage.setSrc(currentImageUrl);
                previewImage.setVisible(true);
            } else { clearImage(); }
            setVisible(true);
            name.focus();
            delete.setVisible(dish.getId() != null);
        } else {
            setVisible(false);
            clearImage();
        }
    }

    private Component createButtonLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addClickShortcut(Key.ENTER);
        close.addClickShortcut(Key.ESCAPE);

        save.addClickListener(event -> validateAndSave());
        delete.addClickListener(event -> fireEvent(new DeleteEvent(this, binder.getBean())));
        close.addClickListener(event -> fireEvent(new CloseEvent(this)));
        return new HorizontalLayout(save, delete, close);
    }

    private void validateAndSave() {
        if (binder.isValid()) {
            Dish dish = binder.getBean();
            dish.setImageUrl(currentImageUrl);

            dish.getIngredients().clear();
            for (DishIngredient di : currentIngredients) {
                di.setDish(dish);
                dish.getIngredients().add(di);
            }

            fireEvent(new SaveEvent(this, dish));
        }
    }

    // Events
    public static abstract class DishFormEvent extends ComponentEvent<DishForm> {
        private final Dish dish;
        protected DishFormEvent(DishForm source, Dish dish) { super(source, false); this.dish = dish; }
        public Dish getDish() { return dish; }
    }
    public static class SaveEvent extends DishFormEvent { SaveEvent(DishForm source, Dish dish) { super(source, dish); } }
    public static class DeleteEvent extends DishFormEvent { DeleteEvent(DishForm source, Dish dish) { super(source, dish); } }
    public static class CloseEvent extends DishFormEvent { CloseEvent(DishForm source) { super(source, null); } }
    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType, ComponentEventListener<T> listener) { return getEventBus().addListener(eventType, listener); }
}