package SimulationPackage;

import GFX.ColorRGB;
import VectorPackage.Vector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class FieldTest {
    private Field entity01 = null;
    private Field entity02 = null;

    @BeforeEach
    public void beforeEach() {
        entity01 = new Field(new Vector(0, 0), 2, new ColorRGB(0, 0, 0));
        entity02 = new Field(new Vector(0, 0), 3, new ColorRGB(0, 0, 0));
    }

    @Test
    public void getPosition() {
        Vector pos = entity01.getPosition();
        assertTrue(pos.x == 0 && pos.y == 0);
    }

    @Test
    public void getRadius() {
        assertEquals(2, entity01.getRadius());
    }

    @Test
    public void getColor() {
        ColorRGB testColor = new ColorRGB(0, 0, 0);
        assertTrue(entity01.getColor().equals(testColor));
    }

    @Test
    public void setPosition() {
        Vector newPos = new Vector(0, 0);
        entity01.setPosition(newPos);
        assertTrue(entity01.getPosition().equals(newPos));
    }

    @Test
    public void setRadius() {
        entity01.setRadius(5);
        assertEquals(5, entity01.getRadius());
    }

    @Test
    public void setColor() {
        ColorRGB testColor = new ColorRGB(10, 50, 20);
        entity01.setColor(testColor);
        assertTrue(entity01.getColor().equals(testColor));
    }

    @Test
    public void setCollidingTrue() {
        assertTrue(entity01.isColliding(entity02));
    }

    @Test
    public void setCollidingFalse() {
        Vector newPos = new Vector(10, 10);
        entity02.setPosition(newPos);
        assertFalse(entity01.isColliding(entity02));
    }
}
