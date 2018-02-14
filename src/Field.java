public class Field {
    protected Vector position;
    protected int radius;
    protected ColorRGB color;

    public Field(Vector position, int radius, ColorRGB color) {
        this.position = position;
        this.radius = radius;
        this.color = color;
    }

    public boolean collides(Field subject) {
        return Math.sqrt(Math.pow((position.x - subject.position.x), 2) + Math.pow(position.y - subject.position.y, 2)) < radius + subject.radius;
    }

    //access private fields

    public Vector getPosition() {
        return position;
    }

    public void setPosition(Vector position) {
        this.position = position;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public ColorRGB getColor() {
        return color;
    }

    public void setColor(ColorRGB color) {
        this.color = color;
    }
}
