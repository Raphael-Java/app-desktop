package gui;

import java.util.prefs.Preferences;

public class AppSettings {

    private static final Preferences prefs = Preferences.userNodeForPackage(AppSettings.class);

    private static final String THEME_KEY = "theme";
    private static final String CURRENCY_KEY = "currency";
    private static final String LANGUAGE_KEY = "language";

    public static String getTheme() {
        return prefs.get(THEME_KEY, "Claro");
    }

    public static void setTheme(String theme) {
        prefs.put(THEME_KEY, theme);
    }

    public static String getCurrency() {
        return prefs.get(CURRENCY_KEY, "Real (R$)");
    }

    public static void setCurrency(String currency) {
        prefs.put(CURRENCY_KEY, currency);
    }

    public static String getLanguage() {
        return prefs.get(LANGUAGE_KEY, "Português (BR)");
    }

    public static void setLanguage(String language) {
        prefs.put(LANGUAGE_KEY, language);
    }
}