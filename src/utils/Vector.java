package utils;

import java.util.Objects;

public class Vector {
    // Fields:
    public int x, y;

    // Constructor:
    public Vector(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public static Vector add(Vector a, Vector b) {
        return new Vector(a.x + b.x, a.y + b.y);
    }

    public static Vector subtract(Vector a, Vector b) {
        return new Vector(a.x - b.x, a.y - b.y);
    }

    @Override
    public String toString() {
        return "Vector { x: " + x + ", y: " + y + " }";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Vector)) return false;
        Vector vec = (Vector) o;
        return this.x == vec.x && this.y == vec.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
