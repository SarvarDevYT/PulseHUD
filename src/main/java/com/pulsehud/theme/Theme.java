package com.pulsehud.theme;

public class Theme {
    private final String name;
    private final int primaryColor;
    private final int secondaryColor;
    private final int glowColor;
    private final int panelBgColor;
    private final int textPrimaryColor;

    public Theme(String name, int primaryColor, int secondaryColor, int glowColor, int panelBgColor, int textPrimaryColor) {
        this.name = name;
        this.primaryColor = primaryColor;
        this.secondaryColor = secondaryColor;
        this.glowColor = glowColor;
        this.panelBgColor = panelBgColor;
        this.textPrimaryColor = textPrimaryColor;
    }

    public String getName() {
        return name;
    }

    public int getPrimaryColor() {
        return primaryColor;
    }

    public int getSecondaryColor() {
        return secondaryColor;
    }

    public int getGlowColor() {
        return glowColor;
    }

    public int getPanelBgColor() {
        return panelBgColor;
    }

    public int getTextPrimaryColor() {
        return textPrimaryColor;
    }

    // Blend helper for transitions
    public static int blend(int color1, int color2, float ratio) {
        if (ratio <= 0.0f) return color1;
        if (ratio >= 1.0f) return color2;

        int a1 = (color1 >> 24) & 0xFF;
        int r1 = (color1 >> 16) & 0xFF;
        int g1 = (color1 >> 8) & 0xFF;
        int b1 = color1 & 0xFF;

        int a2 = (color2 >> 24) & 0xFF;
        int r2 = (color2 >> 16) & 0xFF;
        int g2 = (color2 >> 8) & 0xFF;
        int b2 = color2 & 0xFF;

        int a = (int) (a1 + (a2 - a1) * ratio);
        int r = (int) (r1 + (r2 - r1) * ratio);
        int g = (int) (g1 + (g2 - g1) * ratio);
        int b = (int) (b1 + (b2 - b1) * ratio);

        return (a << 24) | (r << 16) | (g << 8) | b;
    }
}
