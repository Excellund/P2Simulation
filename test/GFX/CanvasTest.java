package GFX;

import SimulationPackage.Entities.Field;
import VectorPackage.Vector;
import org.junit.jupiter.api.*;

import java.awt.*;
import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.*;

public class CanvasTest {

    Canvas canvas;
    Field subject;

    @BeforeEach
    public void beforeEach() {
        canvas = new Canvas(1920, 1080);
        subject = new Field(new Vector(0, 0), 5, new Color(0, 0,0), 200);
    }

    @Test
    public void paintComponent() {

    }

    @Test
    public void clearColor() {
        Color color = new Color(0, 0, 0);
        canvas.clear(color);
        assertTrue(canvas.getGFX().getColor().equals(color));
    }

    @Test
    public void getScene() {
        BufferedImage scene = new BufferedImage(1920, 1080, BufferedImage.TYPE_INT_RGB);
        boolean condition = (scene.getHeight() == canvas.getScene().getHeight()) && (scene.getWidth() == canvas.getScene().getWidth());
        assertTrue(condition);
    }

    @Disabled
    public void drawFieldColor() {
        canvas.drawField(subject);
        assertEquals(subject.color, canvas.getGFX().getColor());
    }
}
