//// src/main/java/com/example/restaurant/ui/ChefView.java
//package com.example.restaurant.ui;
//
//import com.vaadin.flow.component.html.Div;
//import com.vaadin.flow.component.html.H1;
//import com.vaadin.flow.component.html.H2; // <-- НОВОЕ
//import com.vaadin.flow.component.html.Image;
//import com.vaadin.flow.component.html.Paragraph;
//import com.vaadin.flow.component.orderedlayout.HorizontalLayout; // <-- НОВОЕ
//import com.vaadin.flow.component.orderedlayout.VerticalLayout;
//import com.vaadin.flow.router.PageTitle;
//import com.vaadin.flow.router.Route;
//import com.vaadin.flow.server.auth.AnonymousAllowed;
//
//@Route(value = "chef", layout = MainLayout.class)
//@PageTitle("Our Chef | Kinto")
//@AnonymousAllowed
//public class ChefView extends VerticalLayout {
//
//    public ChefView() {
//        addClassName("chef-view");
//        setPadding(false);
//        setSpacing(false);
//        setWidthFull();
//
//        // --- Верхняя секция (Инфо о шефе) ---
//        Div container = new Div();
//        container.addClassName("chef-container");
//
//        Div imageContainer = new Div();
//        imageContainer.addClassName("chef-image-container");
//        Image chefImage = new Image("/images/chef.jpg", "Chef Giorgi Maisuradze");
//        chefImage.addClassName("chef-image");
//        imageContainer.add(chefImage);
//
//        VerticalLayout infoContainer = new VerticalLayout();
//        infoContainer.addClassName("chef-info");
//        infoContainer.setPadding(false);
//        infoContainer.setSpacing(true);
//
//        H1 title = new H1("Meet Our Chef");
//        Paragraph name = new Paragraph("Giorgi Maisuradze");
//        name.addClassName("chef-name");
//
//        Paragraph p1 = new Paragraph(
//                "With 15 years of experience in Tbilisi and Paris, " +
//                        "Giorgi brings authentic Georgian soul to every plate."
//        );
//        Paragraph p2 = new Paragraph(
//                "Master of khinkali, khachapuri, and churchkhela. " +
//                        "His philosophy is simple: fresh ingredients, traditional recipes, " +
//                        "and a lot of heart."
//        );
//
//        infoContainer.add(title, name, p1, p2);
//        container.add(imageContainer, infoContainer);
//
//        // --- (НОВАЯ СЕКЦИЯ!) Галерея шефа ---
//        VerticalLayout gallery = createChefGallery();
//
//        add(container, gallery); // Добавляем обе секции
//    }
//
//    /**
//     * (НОВЫЙ МЕТОД!) Создает галерею внизу страницы
//     */
//    private VerticalLayout createChefGallery() {
//        VerticalLayout section = new VerticalLayout();
//        section.addClassName("chef-gallery-section");
//        section.setAlignItems(Alignment.CENTER);
//
//        H2 title = new H2("From Giorgi's Kitchen");
//
//        HorizontalLayout grid = new HorizontalLayout();
//        grid.addClassName("chef-gallery-grid");
//
//        grid.add(
//                createGalleryItem("/images/chef1.jpg", "Giorgi cooking"),
//                createGalleryItem("/images/chef2.jpg", "Giorgi plating"),
//                createGalleryItem("/images/chef3.jpg", "Giorgi with his team")
//        );
//
//        section.add(title, grid);
//        return section;
//    }
//
//    /**
//     * (НОВЫЙ ХЕЛПЕР!) Создает 1 фото для галереи
//     */
//    private Div createGalleryItem(String src, String alt) {
//        Div item = new Div();
//        item.addClassName("chef-gallery-item");
//
//        Image img = new Image(src, alt);
//        img.addClassName("chef-gallery-image");
//
//        item.add(img);
//        return item;
//    }
//}
package com.example.restaurant.ui;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route(value = "chef", layout = MainLayout.class)
@PageTitle("Our Chef | Kinto")
@AnonymousAllowed
public class ChefView extends VerticalLayout {

    public ChefView() {
        addClassName("chef-view");
        setPadding(false);
        setSpacing(false);
        setWidthFull();

        // --- Info Section ---
        Div container = new Div();
        container.addClassName("chef-container");

        Div imageContainer = new Div();
        imageContainer.addClassName("chef-image-container");
        Image chefImage = new Image("/images/chef.jpg", "Chef Giorgi Maisuradze");
        chefImage.addClassName("chef-image");
        imageContainer.add(chefImage);

        VerticalLayout infoContainer = new VerticalLayout();
        infoContainer.addClassName("chef-info");
        infoContainer.setPadding(false);
        infoContainer.setSpacing(true);

        // ИСПОЛЬЗУЕМ ПЕРЕВОД
        H1 title = new H1(getTranslation("chef.title"));
        Paragraph name = new Paragraph(getTranslation("chef.name"));
        name.addClassName("chef-name");

        Paragraph text = new Paragraph(getTranslation("chef.text"));

        infoContainer.add(title, name, text);
        container.add(imageContainer, infoContainer);

        // --- Gallery Section ---
        VerticalLayout gallery = createChefGallery();

        add(container, gallery);
    }

    private VerticalLayout createChefGallery() {
        VerticalLayout section = new VerticalLayout();
        section.addClassName("chef-gallery-section");
        section.setAlignItems(Alignment.CENTER);

        // Можно добавить ключ chef.gallery в messages.properties или оставить текст
        H2 title = new H2("From Giorgi's Kitchen");

        HorizontalLayout grid = new HorizontalLayout();
        grid.addClassName("chef-gallery-grid");

        grid.add(
                createGalleryItem("/images/chef1.jpg", "Giorgi cooking"),
                createGalleryItem("/images/chef2.jpg", "Giorgi plating"),
                createGalleryItem("/images/chef3.jpg", "Giorgi with his team")
        );

        section.add(title, grid);
        return section;
    }

    private Div createGalleryItem(String src, String alt) {
        Div item = new Div();
        item.addClassName("chef-gallery-item");
        Image img = new Image(src, alt);
        img.addClassName("chef-gallery-image");
        item.add(img);
        return item;
    }
}