package com.example.restaurant.config;

import com.vaadin.flow.i18n.I18NProvider;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

@Component
public class TranslationProvider implements I18NProvider {

    public static final String BUNDLE_PREFIX = "messages";

    @Override
    public List<Locale> getProvidedLocales() {
        // Supported languages for the application
        return List.of(new Locale("en"), new Locale("pl"));
    }

    @Override
    public String getTranslation(String key, Locale locale, Object... params) {
        if (key == null) {
            // Null key should not break the UI
            return "";
        }

        final ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_PREFIX, locale);

        String value;
        try {
            value = bundle.getString(key);
        } catch (final Exception e) {
            // If translation is missing, return the key wrapped in markers to highlight the issue
            return "!" + key + "!";
        }

        // Apply formatting parameters if provided
        if (params.length > 0) {
            value = String.format(value, params);
        }

        return value;
    }
}