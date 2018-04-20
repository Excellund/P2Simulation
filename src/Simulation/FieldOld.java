package Simulation;

public class FieldOld {
    // Fields:
    public Vector position; //hvorfor er fields public???? revise.
    public int radius;
    public Color color;
    private int health;

    // Constructor:
    public FieldOld(Vector position, int radius, Color color, int health) {
        this.position = position;
        this.radius = radius;
        this.color = color;
        this.health = health;
    }

    // Methods:
    /*public boolean isColliding(Field subject) {
        return Math.sqrt(Math.pow((position.x - subject.position.x), 2) + Math.pow(position.y - subject.position.y, 2)) < radius + subject.radius;
    }*/

    public void addHealth(int amount) {
        health += amount;

        if (health > 300) {
            health = 300;
        }
    }

    public void subtractHealth(int amount) {
        health -= amount;

        if (health < 0) {
            health = 0;
        }
    }

    // Getters:
    public Vector getPosition() {
        return position;
    }

    public int getRadius() {
        return radius;
    }

    public Color getColor() {
        return color;
    }

    public int getHealth() {
        return health;
    }

    // Setters:
    public void setPosition(Vector position) {
        this.position = position;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
