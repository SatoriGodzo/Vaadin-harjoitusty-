package com.example;

import com.vaadin.flow.i18n.I18NProvider;
import org.springframework.stereotype.Component;
import java.util.*;

@Component // Tärkeää: kevät löytää tämän luokan itse ja pakottaa Vaadin käyttämään sitä.
public class CustomI18NProvider implements I18NProvider {

    @Override
    public List<Locale> getProvidedLocales() {
        // Luettelo kielistä, jotka olet luonut resursseihin
        return List.of(new Locale("en"), new Locale("fi"));
    }

    @Override
    public String getTranslation(String key, Locale locale, Object... params) {
        // Jos kieltä ei läpäistä, käytämme oletusarvoisesti englantia.
        if (locale == null) {
            locale = new Locale("en");
        }

        try {
            // Etsitään tiedostoa messages_XX.properties
            ResourceBundle bundle = ResourceBundle.getBundle("messages", locale);
            return bundle.getString(key);
        } catch (MissingResourceException e) {
            // Jos unohdit lisätä avaimen tiedostoon, itse avain palautetaan (kätevä virheenkorjaukseen)
            return "!" + key;
        }
    }
}