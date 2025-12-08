//// src/main/java/com/example/restaurant/ui/AboutView.java
//package com.example.restaurant.ui;
//
//import com.vaadin.flow.component.html.Div;
//import com.vaadin.flow.component.html.H1;
//import com.vaadin.flow.component.html.Image;
//import com.vaadin.flow.component.html.Paragraph;
//import com.vaadin.flow.component.orderedlayout.VerticalLayout;
//import com.vaadin.flow.router.PageTitle;
//import com.vaadin.flow.router.Route;
//import com.vaadin.flow.server.auth.AnonymousAllowed;
//
//@Route(value = "about", layout = MainLayout.class) // <-- (ИЗМЕНЕНИЕ!)
//@PageTitle("About Us | Kinto") // <-- (ИЗМЕНЕНИЕ!)
//@AnonymousAllowed
//public class AboutView extends VerticalLayout {
//
//    public AboutView() {
//        // (ИЗМЕНЕНИЕ!) Полностью новый макет
//        addClassName("about-view"); // Новый CSS класс
//        setPadding(false);
//        setSpacing(false);
//        setSizeFull();
//
//        // Главный контейнер
//        Div container = new Div();
//        container.addClassName("about-container");
//
//        // Левая часть (Текст)
//        VerticalLayout infoContainer = new VerticalLayout();
//        infoContainer.addClassName("about-info");
//        infoContainer.setPadding(false);
//        infoContainer.setSpacing(true);
//
//        H1 title = new H1("Our Story"); // Заменил "GOBI" на "Our Story"
//        Paragraph p1 = new Paragraph(
//                "Kinto is more than just a restaurant; it's a celebration of Georgian heritage. " +
//                        "We blend centuries-old traditions with modern culinary art."
//        );
//        Paragraph p2 = new Paragraph(
//                "Founded on the principle of 'supra'—a traditional feast—we bring authentic flavors " +
//                        "with a contemporary twist. From Tbilisi to your table, every dish tells a story."
//        );
//        Paragraph p3 = new Paragraph(
//                "We are masters of khinkali, khachapuri, and churchkhela, using only the freshest " +
//                        "local ingredients and heartfelt tradition."
//        );
//
//        infoContainer.add(title, p1, p2, p3);
//
//        // Правая часть (Картинка)
//        Div imageContainer = new Div();
//        imageContainer.addClassName("about-image-container");
//
//        // (ПРИМЕЧАНИЕ: Добавь 'about-interior.jpg' в 'static/images/')
//        Image interiorImage = new Image("/images/about-interior.jpg", "Kinto Restaurant Interior");
//        interiorImage.addClassName("about-image-style");
//        imageContainer.add(interiorImage);
//
//        container.add(infoContainer, imageContainer);
//        add(container);
//    }
//}
package com.example.restaurant.ui;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route(value = "about", layout = MainLayout.class)
@PageTitle("About Us | Kinto")
@AnonymousAllowed
public class AboutView extends VerticalLayout {

    public AboutView() {
        addClassName("about-view");
        setPadding(false);
        setSpacing(false);
        setSizeFull();

        Div container = new Div();
        container.addClassName("about-container");

        VerticalLayout infoContainer = new VerticalLayout();
        infoContainer.addClassName("about-info");
        infoContainer.setPadding(false);
        infoContainer.setSpacing(true);

        // ИСПОЛЬЗУЕМ ПЕРЕВОД (Keys from messages.properties)
        H1 title = new H1(getTranslation("about.title"));
        Paragraph text = new Paragraph(getTranslation("about.text"));

        infoContainer.add(title, text);

        Div imageContainer = new Div();
        imageContainer.addClassName("about-image-container");

        Image interiorImage = new Image("/images/about-interior.jpg", "Kinto Restaurant Interior");
        interiorImage.addClassName("about-image-style");
        imageContainer.add(interiorImage);

        container.add(infoContainer, imageContainer);
        add(container);
    }
}