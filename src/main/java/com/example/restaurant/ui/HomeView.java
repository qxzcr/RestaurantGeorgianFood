package com.example.restaurant.ui;

import com.example.restaurant.model.Role;
import com.example.restaurant.model.User;
import com.example.restaurant.service.AttendanceService; // <--- Импорт
import com.example.restaurant.service.SecurityService;   // <--- Импорт
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
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

    private final AttendanceService attendanceService;
    private final SecurityService securityService;

    // Внедряем сервисы через конструктор
    public HomeView(AttendanceService attendanceService, SecurityService securityService) {
        this.attendanceService = attendanceService;
        this.securityService = securityService;

        setWidthFull();
        setPadding(false);
        setSpacing(false);

        // --- 0. Виджет учета времени (Только для персонала) ---
        createAttendanceWidget();

        // 1. Первый экран: Hero + About
        HorizontalLayout splitScreen = createSplitScreenSection();
        // 2. Галерея
        VerticalLayout gallerySection = createGallerySection();
        // 3. Футер
        VerticalLayout footerSection = createFooterSection();

        add(splitScreen, gallerySection, footerSection);
    }

    private void createAttendanceWidget() {
        User user = securityService.getAuthenticatedUser();
        // Показываем только если пользователь вошел и он НЕ обычный клиент
        if (user != null && user.getRole() != Role.CUSTOMER) {
            Div widget = new Div();
            widget.setWidthFull();
            widget.getStyle().set("background-color", "#2c3e50") // Темный фон
                    .set("padding", "15px")
                    .set("text-align", "center")
                    .set("box-shadow", "0 2px 5px rgba(0,0,0,0.2)");

            updateAttendanceButton(widget, user);
            add(widget);
        }
    }

    private void updateAttendanceButton(Div container, User user) {
        container.removeAll();
        boolean isClockedIn = attendanceService.isClockedIn(user);

        Span statusText = new Span(isClockedIn ? "🟢 You are CLOCKED IN" : "🔴 You are CLOCKED OUT");
        statusText.getStyle().set("color", "white")
                .set("font-weight", "bold")
                .set("font-size", "1.2em")
                .set("margin-right", "20px");

        Button actionBtn = new Button(isClockedIn ? "Clock Out" : "Clock In");
        actionBtn.addThemeVariants(isClockedIn ? ButtonVariant.LUMO_ERROR : ButtonVariant.LUMO_SUCCESS);
        actionBtn.addClickListener(e -> {
            if (isClockedIn) {
                attendanceService.clockOut(user);
                Notification.show("Goodbye! Shift ended.").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            } else {
                attendanceService.clockIn(user);
                Notification.show("Welcome! Shift started.").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            }
            updateAttendanceButton(container, user); // Обновляем кнопку
        });

        container.add(statusText, actionBtn);
    }

    // --- ОСТАЛЬНОЙ КОД БЕЗ ИЗМЕНЕНИЙ (Копируем методы из старого файла) ---

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
        H1 title = new H1(getTranslation("home.hero.title"));
        title.addClassName("hero-title");
        Div buttons = new Div();
        buttons.addClassName("hero-buttons");
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
        hero.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        hero.setAlignItems(Alignment.CENTER);
        hero.setPadding(false);
        hero.setMargin(false);
        return hero;
    }

    private VerticalLayout createAboutSection() {
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
        Span fb = new Span("f"); Span tw = new Span("t"); Span ig = new Span("i");
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
        HorizontalLayout columns = new HorizontalLayout(col1);
        columns.addClassName("footer-columns");
        columns.setWidthFull();
        columns.setJustifyContentMode(JustifyContentMode.CENTER);
        footer.add(columns);
        footer.setAlignItems(Alignment.CENTER);
        return footer;
    }
}