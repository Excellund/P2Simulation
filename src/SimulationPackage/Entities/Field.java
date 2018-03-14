package SimulationPackage.Entities;

import GFX.ColorRGB;
import VectorPackage.Vector;

public class Field {
    // Fields:
    public Vector position;
    public int radius;
    public ColorRGB color;

    // Constructor:
    public Field(Vector position, int radius, ColorRGB color) {
        this.position = position;
        this.radius = radius;
        this.color = color;
    }

    // Methods:
    public boolean isColliding(Field subject) {
        return Math.sqrt(Math.pow((position.x - subject.position.x), 2) + Math.pow(position.y - subject.position.y, 2)) < radius + subject.radius;
    }

    // Getters:

    public Vector getPosition() {
        return position;
    }

    public int getRadius() {
        return radius;
    }

    public ColorRGB getColor() {
        return color;
    }

    // Setters:

    public void setPosition(Vector position) {
        this.position = position;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public void setColor(ColorRGB color) {
        this.color = color;
    }
}
