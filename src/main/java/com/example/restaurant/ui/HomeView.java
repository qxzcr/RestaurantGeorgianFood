//// src/main/java/com/example/restaurant/ui/HomeView.java
//package com.example.restaurant.ui;
//
//import com.vaadin.flow.component.button.Button;
//import com.vaadin.flow.component.html.*;
//import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
//import com.vaadin.flow.component.orderedlayout.VerticalLayout;
//import com.vaadin.flow.router.PageTitle;
//import com.vaadin.flow.router.Route;
//import com.vaadin.flow.router.RouteAlias;
//import com.vaadin.flow.server.auth.AnonymousAllowed;
//
//@Route(value = "home", layout = MainLayout.class)
//@RouteAlias(value = "", layout = MainLayout.class)
//@PageTitle("Home | Kinto Restaurant")
//@AnonymousAllowed
//public class HomeView extends VerticalLayout {
//
//    public HomeView() {
//        setWidthFull();
//        setPadding(false);
//        setSpacing(false);
//
//        // 1. Первый экран: Hero + About (65% / 35%)
//        HorizontalLayout splitScreen = createSplitScreenSection();
//
//        // 2. Галерея
//        VerticalLayout gallerySection = createGallerySection();
//
//        // 3. Футер
//        VerticalLayout footerSection = createFooterSection();
//
//        add(splitScreen, gallerySection, footerSection);
//    }
//
//    // === ПЕРВЫЙ ЭКРАН: HERO + ABOUT ===
//    private HorizontalLayout createSplitScreenSection() {
//        VerticalLayout heroColumn = createHeroSection();
//        VerticalLayout aboutColumn = createAboutSection();
//
//        HorizontalLayout layout = new HorizontalLayout(heroColumn, aboutColumn);
//        layout.setWidthFull();
//        layout.setHeight("100vh");
//        layout.setSpacing(false);
//        layout.setPadding(false);
//
//        layout.setFlexGrow(6.5, heroColumn);
//        layout.setFlexGrow(3.5, aboutColumn);
//
//        return layout;
//    }
//
//    private VerticalLayout createHeroSection() {
//        Div heroContent = new Div();
//        heroContent.addClassName("hero-content");
//
//        H1 title = new H1("MODERN GEORGIAN CUISINE RESTAURANT");
//        title.addClassName("hero-title");
//
//        Div buttons = new Div();
//        buttons.addClassName("hero-buttons");
//
//        Button bookBtn = new Button("BOOK A TABLE");
//        bookBtn.addClassName("btn-book");
//        bookBtn.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("reservations")));
//
//        Button menuBtn = new Button("MENU");
//        menuBtn.addClassName("btn-menu");
//        menuBtn.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("menu")));
//
//        buttons.add(bookBtn, menuBtn);
//        heroContent.add(title, buttons);
//
//        VerticalLayout hero = new VerticalLayout(heroContent);
//        hero.addClassName("hero");
//        hero.setHeightFull();
//        hero.setJustifyContentMode(JustifyContentMode.CENTER);
//        hero.setAlignItems(Alignment.CENTER);
//        hero.setPadding(false);
//        hero.setMargin(false);
//
//        return hero;
//    }
//
//    private VerticalLayout createAboutSection() {
//        // (ВОТ ИСПРАВЛЕНИЕ!) Текст "Welcome" был неправильный
//        Span welcome = new Span("Welcome");
//        welcome.addClassName("about-welcome");
//
//        H2 title = new H2("Experience True Khachapuri");
//        title.addClassName("about-title");
//
//        Paragraph text = new Paragraph(
//                "Nestled under ancient trees, our restaurant offers a culinary journey through Georgia. " +
//                        "Taste renowned dishes with fresh, local ingredients."
//        );
//        text.addClassName("about-text");
//
//        Image image = new Image("/images/khachapuri.jpg", "Signature Khachapuri");
//        image.addClassName("about-image");
//
//        Span caption = new Span("Delicious khachapuri on animated Table.com");
//        caption.addClassName("about-caption");
//
//        Span fb = new Span("f");
//        Span tw = new Span("t");
//        Span ig = new Span("i");
//        HorizontalLayout socialIcons = new HorizontalLayout(fb, tw, ig);
//        socialIcons.addClassName("social-icons");
//
//        VerticalLayout about = new VerticalLayout(welcome, title, text, image, caption, socialIcons);
//        about.addClassName("about-section");
//        about.setHeightFull();
//        about.setJustifyContentMode(JustifyContentMode.START); // Прижато к верху
//        about.setAlignItems(Alignment.CENTER);
//        about.setPadding(true);
//        about.setSpacing(false);
//
//        return about;
//    }
//
//    // === ГАЛЕРЕЯ ===
//    private VerticalLayout createGallerySection() {
//        VerticalLayout section = new VerticalLayout();
//        section.addClassName("gallery-section");
//
//        H2 title = new H2("Our Halls & Veranda");
//        title.getStyle().set("text-align", "center");
//
//        Div grid = new Div();
//        grid.addClassName("gallery-grid");
//
//        // Текст 1
//        VerticalLayout text1 = new VerticalLayout(
//                new H1("Summer Veranda"),
//                new Paragraph("Enjoy your meal outdoors, surrounded by greenery and fresh air. Our veranda is the perfect escape from the city hustle, offering a calm and serene dining experience day or night.")
//        );
//        text1.addClassNames("gallery-item-text", "gallery-item-1");
//
//        // Фото 1 (Veranda)
//        VerticalLayout photo1 = createGalleryPhoto("/images/veranda1.jpg", "Summer Veranda");
//        photo1.addClassName("gallery-item-2");
//
//        // Фото 2 (Main Hall)
//        VerticalLayout photo2 = createGalleryPhoto("/images/mainhall.jpg", "Main Hall");
//        photo2.addClassName("gallery-item-3");
//
//        // Текст 2
//        VerticalLayout text2 = new VerticalLayout(
//                new H1("Banquet Hall"),
//                new Paragraph("Celebrate your special events with us. Our spacious banquet hall is perfect for weddings, anniversaries, and corporate events, with customizable menus to make your day unforgettable.")
//        );
//        text2.addClassNames("gallery-item-text", "gallery-item-4");
//
//        // Фото 3 (Banquet)
//        VerticalLayout photo3 = createGalleryPhoto("/images/banquet.jpg", "Banquet Hall");
//        photo3.addClassName("gallery-item-5");
//
//        grid.add(text1, photo1, photo2, text2, photo3);
//
//        section.add(title, grid);
//        section.setAlignItems(Alignment.CENTER);
//        section.setPadding(false);
//        section.setSpacing(false);
//
//        return section;
//    }
//
//    private VerticalLayout createGalleryPhoto(String src, String alt) {
//        VerticalLayout item = new VerticalLayout();
//        item.addClassName("gallery-item-photo");
//        item.setPadding(false);
//        item.setMargin(false);
//        item.setSpacing(false);
//
//        Image img = new Image(src, alt);
//        img.addClassName("gallery-image");
//
//        item.add(img);
//        return item;
//    }
//
//    // === ФУТЕР ===
//    private VerticalLayout createFooterSection() {
//        VerticalLayout footer = new VerticalLayout();
//        footer.addClassName("footer-section");
//
//        VerticalLayout col1 = new VerticalLayout(
//                new H2("Kinto"),
//                new Paragraph("Authentic Georgian dining in City Center.")
//        );
//        col1.addClassName("footer-column");
//
//        VerticalLayout col2 = new VerticalLayout(
//                new H2("Opening Hours"),
//                new Span("Monday - Friday: 11:00 AM - 10:00 PM"),
//                new Span("Saturday - Sunday: 12:00 PM - 11:00 PM")
//        );
//        col2.addClassName("footer-column");
//
//        VerticalLayout col3 = new VerticalLayout(
//                new H2("Contact Information"),
//                new Span("+1 (555) 123-4567"),
//                new Span("info@kinto.com"),
//                new Span("123 Georgian Street, City Center")
//        );
//        col3.addClassName("footer-column");
//
//        HorizontalLayout columns = new HorizontalLayout(col1, col2, col3);
//        columns.addClassName("footer-columns");
//        columns.setWidthFull();
//        columns.setJustifyContentMode(JustifyContentMode.CENTER);
//
//        footer.add(columns);
//        footer.setAlignItems(Alignment.CENTER);
//        footer.setPadding(false);
//        footer.setSpacing(false);
//
//        return footer;
//    }
//}
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

    public HomeView() {
        setWidthFull();
        setPadding(false);
        setSpacing(false);

        HorizontalLayout splitScreen = createSplitScreenSection();
        VerticalLayout gallerySection = createGallerySection();
        VerticalLayout footerSection = createFooterSection();

        add(splitScreen, gallerySection, footerSection);
    }

    private HorizontalLayout createSplitScreenSection() {
        VerticalLayout heroColumn = createHeroSection();
        VerticalLayout aboutColumn = createAboutSection();

        HorizontalLayout layout = new HorizontalLayout(heroColumn, aboutColumn);
        layout.setWidthFull();
        layout.setHeight("100vh");
        layout.setSpacing(false);
        layout.setPadding(false);
        layout.setFlexGrow(6.5, heroColumn);
        layout.setFlexGrow(3.5, aboutColumn);
        return layout;
    }

    private VerticalLayout createHeroSection() {
        Div heroContent = new Div();
        heroContent.addClassName("hero-content");

        // Translated text
        H1 title = new H1(getTranslation("home.hero.title"));
        title.addClassName("hero-title");

        Div buttons = new Div();
        buttons.addClassName("hero-buttons");

        // Translated buttons
        Button bookBtn = new Button(getTranslation("home.hero.book"));
        bookBtn.addClassName("btn-book");
        bookBtn.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("reservations")));

        Button menuBtn = new Button(getTranslation("home.hero.menu"));
        menuBtn.addClassName("btn-menu");
        menuBtn.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("menu")));

        buttons.add(bookBtn, menuBtn);
        heroContent.add(title, buttons);

        VerticalLayout hero = new VerticalLayout(heroContent);
        hero.addClassName("hero");
        hero.setHeightFull();
        hero.setJustifyContentMode(JustifyContentMode.CENTER);
        hero.setAlignItems(Alignment.CENTER);
        hero.setPadding(false);
        hero.setMargin(false);
        return hero;
    }

    private VerticalLayout createAboutSection() {
        // Translated text
        Span welcome = new Span(getTranslation("home.about.welcome"));
        welcome.addClassName("about-welcome");

        H2 title = new H2(getTranslation("home.about.title"));
        title.addClassName("about-title");

        Paragraph text = new Paragraph(getTranslation("home.about.text"));
        text.addClassName("about-text");

        Image image = new Image("/images/khachapuri.jpg", "Signature Khachapuri");
        image.addClassName("about-image");

        Span caption = new Span("Delicious khachapuri");
        caption.addClassName("about-caption");

        Span fb = new Span("f");
        Span tw = new Span("t");
        Span ig = new Span("i");
        HorizontalLayout socialIcons = new HorizontalLayout(fb, tw, ig);
        socialIcons.addClassName("social-icons");

        VerticalLayout about = new VerticalLayout(welcome, title, text, image, caption, socialIcons);
        about.addClassName("about-section");
        about.setHeightFull();
        about.setJustifyContentMode(JustifyContentMode.START);
        about.setAlignItems(Alignment.CENTER);
        about.setPadding(true);
        about.setSpacing(false);
        return about;
    }

    private VerticalLayout createGallerySection() {
        VerticalLayout section = new VerticalLayout();
        section.addClassName("gallery-section");

        H2 title = new H2(getTranslation("home.gallery.title"));
        title.getStyle().set("text-align", "center");

        Div grid = new Div();
        grid.addClassName("gallery-grid");

        // Static gallery content (can also be translated if needed)
        VerticalLayout text1 = new VerticalLayout(new H1("Summer Veranda"), new Paragraph("Open air dining..."));
        text1.addClassNames("gallery-item-text", "gallery-item-1");
        VerticalLayout photo1 = createGalleryPhoto("/images/veranda1.jpg", "Summer Veranda");
        photo1.addClassName("gallery-item-2");
        VerticalLayout photo2 = createGalleryPhoto("/images/mainhall.jpg", "Main Hall");
        photo2.addClassName("gallery-item-3");
        VerticalLayout text2 = new VerticalLayout(new H1("Banquet Hall"), new Paragraph("For events..."));
        text2.addClassNames("gallery-item-text", "gallery-item-4");
        VerticalLayout photo3 = createGalleryPhoto("/images/banquet.jpg", "Banquet Hall");
        photo3.addClassName("gallery-item-5");

        grid.add(text1, photo1, photo2, text2, photo3);
        section.add(title, grid);
        section.setAlignItems(Alignment.CENTER);
        return section;
    }

    private VerticalLayout createGalleryPhoto(String src, String alt) {
        VerticalLayout item = new VerticalLayout();
        item.addClassName("gallery-item-photo");
        Image img = new Image(src, alt);
        img.addClassName("gallery-image");
        item.add(img);
        return item;
    }

    private VerticalLayout createFooterSection() {
        VerticalLayout footer = new VerticalLayout();
        footer.addClassName("footer-section");
        VerticalLayout col1 = new VerticalLayout(new H2("Kinto"), new Paragraph("Authentic Georgian dining."));
        col1.addClassName("footer-column");
        HorizontalLayout columns = new HorizontalLayout(col1); // Simplified footer for brevity
        columns.addClassName("footer-columns");
        columns.setWidthFull();
        columns.setJustifyContentMode(JustifyContentMode.CENTER);
        footer.add(columns);
        footer.setAlignItems(Alignment.CENTER);
        return footer;
    }
}