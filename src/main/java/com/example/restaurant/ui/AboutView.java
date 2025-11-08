package com.example.restaurant.ui;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route("about")
@AnonymousAllowed
public class AboutView extends VerticalLayout {

    public AboutView() {
        addClassName("page");
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        setPadding(true);
        setSpacing(true);

        add(
                new H1("About GOBI"),
                new Paragraph("We blend centuries-old Georgian traditions with modern culinary art."),
                new Paragraph("Founded in 2025, GOBI brings authentic flavors with a contemporary twist."),
                new Paragraph("From Tbilisi to your table — every dish tells a story.")
        );
    }
}