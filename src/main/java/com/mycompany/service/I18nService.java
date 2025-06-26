package com.mycompany.service;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 *
 * @author ASUS
 */
public class I18nService {
    private static ResourceBundle bundle;

    public static void setLocale(Locale locale) {
        bundle = ResourceBundle.getBundle("messages", locale);
    }

    public static String getString(String key) {
        if (bundle == null) {
            setLocale(Locale.getDefault()); // Fallback to default
        }
        return bundle.getString(key);
    }
}
