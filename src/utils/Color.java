package utils;

public class Color {
    private int intRepresentation;

    private Color() {

    }

    //Creates a color from rgb values between 0-255
    public Color(int r, int g, int b) {
        r = r > 0xff ? 0xff : r; //Limit to 255
        g = g > 0xff ? 0xff : g; //Limit to 255
        b = b > 0xff ? 0xff : b; //Limit to 255

        intRepresentation = (r << 16) | (g << 8) | b;
    }

    //Arguments should be between 0 and 1
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

    //Get red from 0-1
    public float getRedNormalized() {
        return getRed() / 255f;
    }

    //Get green from 0-1
    public float getGreenNormalized() {
        return getGreen() / 255f;
    }

    //Get blue from 0-1
    public float getBlueNormalized() {
        return getBlue() / 255f;
    }

    public static Color getGammaCorrected(Color col, float gamma) {
        return new Color(
                (int) Math.pow(col.getRed(), gamma),
                (int) Math.pow(col.getGreen(), gamma),
                (int) Math.pow(col.getBlue(), gamma)
        );
    }

    public static float getGammaCorrected(float col, float gamma) {
        return (float) Math.pow(col, gamma);
    }
}
