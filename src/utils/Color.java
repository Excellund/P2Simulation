package utils;

public class Color {
    private int intRepresentation;

    //Creates a color from rgb values between 0-255
    public Color(int r, int g, int b) {
        intRepresentation = (r << 16) | (g << 8) | b;
    }

    //Arguments between 0 and 1
    //Creates a color from rgb values between 0-1
    public Color(float r, float g, float b) {
        this((int) (r * 255), (int) (g * 255), (int) (b * 255));
    }

    // Getters
    public int getIntRepresentation() {
        return intRepresentation;
    }

    public int getRed() {
        return (intRepresentation >> 16) & 0xFF;
    }

    public int getGreen() {
        return (intRepresentation >> 8) & 0xFF;
    }

    public int getBlue() {
        return (intRepresentation) & 0xFF;
    }

    public float getRedNormalized() {
        return getRed() / 255f;
    }

    public float getGreenNormalized() {
        return getGreen() / 255f;
    }

    public float getBlueNormalized() {
        return getBlue() / 255f;
    }
}
