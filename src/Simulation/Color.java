package Simulation;

public class Color {
    private int intRepresentation;

    public Color(int r, int g, int b) {
        intRepresentation = (r << 16) | (g << 8) | b;
    }


    // Getters
    public int getIntRepresentation() {
        return intRepresentation;
    }

    public int getRed() {
        return intRepresentation & 0xFF0000;
    }

    public int getGreen() {
        return intRepresentation & 0x00FF00;
    }

    public int getBlue() {
        return intRepresentation & 0x0000FF;
    }
}
