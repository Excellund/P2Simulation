package SimulationPackage;

import GFX.ColorRGB;
import VectorPackage.Vector;

public class Field {
    public Vector position;
    public int radius;
    public ColorRGB color;

    public Field(Vector position, int radius, ColorRGB color) {
        this.position = position;
        this.radius = radius;
        this.color = color;
    }

    // Methods:
    public boolean isColliding(Field entity) {
        return Math.sqrt(Math.pow((position.x - entity.position.x), 2) + Math.pow(position.y - entity.position.y, 2)) < radius + entity.radius;
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
