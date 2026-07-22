package config;

import atlantafx.base.theme.PrimerLight;
import javafx.application.Application;
import javafx.scene.text.Font;

public class ThemeManager {

    public static void applyTheme() {
        loadFont();
        // تم روشن AtlantaFX
        Application.setUserAgentStylesheet(
                new PrimerLight().getUserAgentStylesheet()
        );
    }

    private static void loadFont() {
        try {
            Font.loadFont(
                    ThemeManager.class.getResourceAsStream("/fonts/Vazirmatn.ttf"),
                    14
            );
            System.out.println("Font loaded successfully!");
        } catch (Exception e) {
            System.err.println("Failed to load font: " + e.getMessage());
        }
    }
    // برای تم تاریک (در آینده)
    // public static void applyDarkTheme() {
    //     Application.setUserAgentStylesheet(
    //         new PrimerDark().getUserAgentStylesheet()
    //     );
    // }
}