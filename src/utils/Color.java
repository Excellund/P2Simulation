package utils;

public class Color {
    private int intRepresentation;

    //Arguments between 0 and 255
    public Color(int r, int g, int b) {
        intRepresentation = (r << 16) | (g << 8) | b;
    }

    //Arguments between 0 and 1
    public Color(float r, float g, float b) {
        this((int) (r * 255), (int) (g * 255), (int) (b * 255));
    }

    // Getters
    public int getIntRepresentation() {
        return intRepresentation;
    }

    public int getRed() {
        return intRepresentation & 0xFF;
    }

    public int getGreen() {
        return (intRepresentation >> 2) & 0xFF;
    }

    public int getBlue() {
        return (intRepresentation >> 4) & 0xFF;
    }

    public float getRedNormalized() {
        return (float) getRed() / 255;
    }

    public float getGreenNormalized() {
        return (float) getGreen() / 255;
    }

    public float getBlueNormalized() {
        return (float) getBlue() / 255;
    }
}
