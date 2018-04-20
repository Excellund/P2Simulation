package Simulation;

public class Vector {
    // Fields:
    public int x, y;

    // Constructor:
    public Vector(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "Vector { x: " + x + ", y: " + y + " }";
    }
}
