// src/main/java/com/example/restaurant/ui/HomeView.java
package com.example.restaurant.ui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route(value = "home", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@PageTitle("Home | Kinto Restaurant")
@AnonymousAllowed
public class HomeView extends VerticalLayout {

    // (ВОТ ИСПРАВЛЕННЫЙ КОНСТРУКТОР)
    public HomeView() {
        // 1. Создаем "Первый экран"
        VerticalLayout heroColumn = createHeroSection();
        VerticalLayout aboutColumn = createAboutSection();

        HorizontalLayout splitScreenLayout = new HorizontalLayout(heroColumn, aboutColumn);
        splitScreenLayout.setWidth("100%");
        splitScreenLayout.setHeight("100vh");
        splitScreenLayout.setSpacing(false);

        // (НОВЫЙ, БОЛЕЕ НАДЕЖНЫЙ СПОСОБ ЗАДАТЬ 65/35)
        // Говорим HorizontalLayout, в какой пропорции делить место
        // (6.5 + 3.5 = 10, т.е. 65% и 35%)
        splitScreenLayout.setFlexGrow(6.5, heroColumn);
        splitScreenLayout.setFlexGrow(3.5, aboutColumn);

        // 2. Галерея
        VerticalLayout gallerySection = createGallerySection();

        // 3. Футер
        VerticalLayout footerSection = createFooterSection();

        // 4. Добавляем ВСЕ секции (только ОДИН РАЗ)
        add(splitScreenLayout, gallerySection, footerSection);

        setPadding(false);
        setSpacing(false);
        setWidth("100%");
    }
    // (КОНЕЦ ИСПРАВЛЕННОГО КОНСТРУКТОРА)


    // --- Секция "Hero" (Левая колонка) ---
    private VerticalLayout createHeroSection() {
        Div heroContent = new Div();
        heroContent.addClassName("hero-content");

        H1 title = new H1("MODERN GEORGIAN CUISINE RESTAURANT");
        title.addClassName("hero-title");

        Div buttons = new Div();
        buttons.addClassName("hero-buttons");

        Button menuBtn = new Button("Menu");
        menuBtn.addClassName("btn-menu");
        menuBtn.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("menu")));

        Button bookBtn = new Button("Book a Table");
        bookBtn.addClassName("btn-book");
        bookBtn.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("reservations")));

        buttons.add(bookBtn, menuBtn);
        heroContent.add(title, buttons);

        VerticalLayout heroColumn = new VerticalLayout(heroContent);
        heroColumn.addClassName("hero");
        heroColumn.setHeightFull();
        heroColumn.setJustifyContentMode(JustifyContentMode.CENTER);
        heroColumn.setAlignItems(Alignment.CENTER);
        heroColumn.setPadding(false);
        return heroColumn;
    }

    // --- Секция "About" (Правая колонка) ---
    // --- Секция "About" (Правая колонка) — ОБНОВЛЁННАЯ ---
    private VerticalLayout createAboutSection() {

        Span welcome = new Span("Welcome");
        welcome.addClassName("about-welcome");

        H2 title = new H2("Experience True Khachapuri");
        title.addClassName("about-title");

        Paragraph text = new Paragraph(
                "Nestled under ancient trees, our restaurant offers a culinary journey " +
                        "through Georgia. Renowned for fresh, local ingredients and authentic flavors."
        );
        text.addClassName("about-text");

        Image image = new Image("/images/khachapuri.jpg", "Signature Khachapuri");
        image.addClassName("about-image");

        Span caption = new Span("Signature dish — baked with love");
        caption.addClassName("about-caption");

        Span fb = new Span("f");
        Span tw = new Span("t");
        Span ig = new Span("i");
        HorizontalLayout socialIcons = new HorizontalLayout(fb, tw, ig);
        socialIcons.addClassName("social-icons");

        VerticalLayout aboutColumn = new VerticalLayout(
                welcome, title, text, image, caption, socialIcons
        );
        aboutColumn.addClassName("about-section");
        aboutColumn.setHeightFull();
        aboutColumn.setJustifyContentMode(JustifyContentMode.CENTER);
        aboutColumn.setAlignItems(Alignment.CENTER);
        aboutColumn.setPadding(true);
        aboutColumn.getStyle().set("background", "transparent");

        return aboutColumn;
    }

    // --- Галерея (по твоему рисунку) ---
    private VerticalLayout createGallerySection() {
        VerticalLayout section = new VerticalLayout();
        section.addClassName("gallery-section");

        H2 title = new H2("Our Halls & Veranda");

        Div grid = new Div();
        grid.addClassName("gallery-grid");

        VerticalLayout text1 = new VerticalLayout(
                new H1("Summer Veranda"),
                new Paragraph("Enjoy your meal outdoors, surrounded by greenery and fresh air. Our veranda is the perfect escape from the city hustle, offering a calm and serene dining experience day or night.")
        );
        text1.addClassNames("gallery-item-text", "gallery-item-1");

        VerticalLayout photo1 = createGalleryItem("/images/veranda1.jpg", "Veranda");
        photo1.addClassName("gallery-item-2");

        VerticalLayout photo2 = createGalleryItem("/images/mainhall.jpg", "Main Hall");
        photo2.addClassName("gallery-item-3");

        VerticalLayout text2 = new VerticalLayout(
                new H1("Banquet Hall"),
                new Paragraph("Celebrate your special events with us. Our spacious banquet hall is perfect for weddings, anniversaries, and corporate events, with customizable menus to make your day unforgettable.")
        );
        text2.addClassNames("gallery-item-text", "gallery-item-4");

        VerticalLayout photo3 = createGalleryItem("/images/banquet.jpg", "Banquet Hall");
        photo3.addClassName("gallery-item-5");

        grid.add(text1, photo1, photo2, text2, photo3);

        section.add(title, grid);
        section.setAlignItems(Alignment.CENTER);
        return section;
    }

    private VerticalLayout createGalleryItem(String imageUrl, String altText) {
        VerticalLayout item = new VerticalLayout();
        item.addClassName("gallery-item-photo");
        item.setPadding(false);

        Image img = new Image(imageUrl, altText);
        img.addClassName("gallery-image");

        item.add(img);
        return item;
    }

    // --- Футер ---
    private VerticalLayout createFooterSection() {
        VerticalLayout footer = new VerticalLayout();
        footer.addClassName("footer-section");

        VerticalLayout kintoInfo = new VerticalLayout(
                new H2("Kinto"),
                new Paragraph("Authentic Georgian dining in City Center.")
        );
        kintoInfo.addClassName("footer-column");
        kintoInfo.setPadding(false);

        VerticalLayout hours = new VerticalLayout(
                new H2("Opening Hours"),
                new Span("Monday - Friday: 11:00 AM - 10:00 PM"),
                new Span("Saturday - Sunday: 12:00 PM - 11:00 PM")
        );
        hours.addClassName("footer-column");
        hours.setPadding(false);

        VerticalLayout contact = new VerticalLayout(
                new H2("Contact Information"),
                new Span("+1 (555) 123-4567"),
                new Span("info@kinto.com"),
                new Span("123 Georgian Street, City Center")
        );
        contact.addClassName("footer-column");
        contact.setPadding(false);

        HorizontalLayout columns = new HorizontalLayout(kintoInfo, hours, contact);
        columns.addClassName("footer-columns");

        footer.add(columns);
        footer.setAlignItems(Alignment.CENTER);
        return footer;
    }
}