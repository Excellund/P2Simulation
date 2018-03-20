package GFX;

import SimulationPackage.Entities.Field;
import SimulationPackage.Simulation;
import VectorPackage.Vector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DisplayTest {

    Display display;
    ArrayList<Field> subjects;

    @BeforeEach
    void beforeEach(){

        display = new Display("Test", 1920, 1080);
        subjects = new ArrayList<>();
    }

    @Disabled
    void drawFrameSubjectSize(){

        int testInt = 50;

        for(int i = 0; i < testInt; i++){

            Field subject = new Field(new Vector(0, 0), 0, new Color(0, 0, 0), 200);
            subjects.add(i, subject);
        }

        //assertEquals(subjects.size(), display.drawFrame(subjects));

    }

    @Disabled
    void closeTest(){

        display.close();
        //assertFalse(display.isRunning());
    }

}
