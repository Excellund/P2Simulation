package GFX;

import java.util.Objects;

public class ColorRGB {
    public int r, g, b;

    public ColorRGB(int r, int g, int b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ColorRGB colorRGB = (ColorRGB) o;
        return r == colorRGB.r &&
                g == colorRGB.g &&
                b == colorRGB.b;
    }

    @Override
    public int hashCode() {
        return Objects.hash(r, g, b);
    }
}
