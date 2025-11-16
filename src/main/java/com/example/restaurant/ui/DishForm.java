// src/main/java/com/example/restaurant/ui/DishForm.java
package com.example.restaurant.ui;

import com.example.restaurant.model.Dish;
import com.example.restaurant.model.DishCategory;
import com.example.restaurant.service.ImageUploadService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.server.StreamResource; // <-- (FIX 1) NEW IMPORT
import com.vaadin.flow.shared.Registration;

import java.io.ByteArrayInputStream; // <-- (FIX 2) NEW IMPORT
import java.io.InputStream;

public class DishForm extends FormLayout {

    // --- Services ---
    private final ImageUploadService imageUploadService;

    // --- Fields ---
    H2 title = new H2("Edit Dish");
    TextField name = new TextField("Dish Name");
    TextArea description = new TextArea("Description");
    BigDecimalField price = new BigDecimalField("Price");
    ComboBox<DishCategory> category = new ComboBox<>("Category");

    Image previewImage = new Image();
    Upload imageUpload;
    MemoryBuffer buffer = new MemoryBuffer();

    private String currentImageUrl = "";
    private byte[] uploadedImageData; // (FIX 3) Store the raw image data for the preview

    Binder<Dish> binder = new BeanValidationBinder<>(Dish.class);

    // --- Buttons ---
    Button save = new Button("Save");
    Button delete = new Button("Delete");
    Button close = new Button("Cancel");

    public DishForm(ImageUploadService imageUploadService) {
        this.imageUploadService = imageUploadService;
        addClassName("dish-form");

        category.setItems(DishCategory.values());
        price.setPrefixComponent(new Span("$"));

        setupImageUpload();

        binder.bindInstanceFields(this);

        add(
                title,
                name,
                description,
                price,
                category,
                new VerticalLayout(new Span("Dish Image"), previewImage, imageUpload),
                createButtonLayout()
        );
    }

    private void setupImageUpload() {
        imageUpload = new Upload(buffer);
        imageUpload.addClassName("dish-image-upload");
        imageUpload.setAcceptedFileTypes("image/jpeg", "image/png", "image/webp");
        imageUpload.setMaxFiles(1);
        imageUpload.setDropLabel(new Span("Drag & Drop image here"));

        previewImage.addClassName("dish-preview-image");
        previewImage.setVisible(false);

        imageUpload.addSucceededListener(event -> {
            // (HERE IS THE FIX!)
            // 1. Get the image data from the buffer
            try (InputStream inputStream = buffer.getInputStream()) {
                // Read the bytes from the stream and store them
                uploadedImageData = inputStream.readAllBytes();

                // 2. Create a StreamResource for the preview
                StreamResource resource = new StreamResource(event.getFileName(),
                        () -> new ByteArrayInputStream(uploadedImageData));

                // 3. Set the resource as the image 'src'
                previewImage.setSrc(resource);
                previewImage.setVisible(true);
            } catch (Exception e) {
                Notification.show("Error reading image preview: " + e.getMessage(), 3000, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }

            // 4. Save the file to disk and get the URL
            try {
                // We use a NEW InputStream from the stored byte array
                currentImageUrl = imageUploadService.saveImage(event.getFileName(), new ByteArrayInputStream(uploadedImageData));

                Notification.show("Image uploaded!", 2000, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            } catch (Exception e) {
                Notification.show("Error saving image file: " + e.getMessage(), 3000, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });

        imageUpload.addFileRejectedListener(event -> {
            Notification.show(event.getErrorMessage(), 3000, Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            clearImage();
        });

        imageUpload.addFailedListener(event -> clearImage());
        imageUpload.getElement().addEventListener("file-remove", e -> clearImage());
    }

    private void clearImage() {
        previewImage.setSrc("");
        previewImage.setVisible(false);
        currentImageUrl = "";
        uploadedImageData = null;
    }

    public void setDish(Dish dish) {
        binder.setBean(dish);

        if (dish != null) {
            if (dish.getImageUrl() != null && !dish.getImageUrl().isEmpty()) {
                currentImageUrl = dish.getImageUrl();
                previewImage.setSrc(currentImageUrl); // Set from existing URL
                previewImage.setVisible(true);
            } else {
                clearImage();
            }
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

        binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));
        return new HorizontalLayout(save, delete, close);
    }

    private void validateAndSave() {
        if (binder.isValid()) {
            Dish dish = binder.getBean();
            // Manually set the image URL
            dish.setImageUrl(currentImageUrl);
            fireEvent(new SaveEvent(this, dish));
        }
    }

    // --- Custom Events (unchanged) ---
    public static abstract class DishFormEvent extends ComponentEvent<DishForm> {
        private final Dish dish;
        protected DishFormEvent(DishForm source, Dish dish) {
            super(source, false);
            this.dish = dish;
        }
        public Dish getDish() {
            return dish;
        }
    }
    public static class SaveEvent extends DishFormEvent {
        SaveEvent(DishForm source, Dish dish) {
            super(source, dish);
        }
    }
    public static class DeleteEvent extends DishFormEvent {
        DeleteEvent(DishForm source, Dish dish) {
            super(source, dish);
        }
    }
    public static class CloseEvent extends DishFormEvent {
        CloseEvent(DishForm source) {
            super(source, null);
        }
    }
    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
                                                                  ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }
}