package simulation;

import javafx.scene.canvas.Canvas;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritablePixelFormat;

import java.nio.IntBuffer;

public class Engine implements Runnable {
    private Simulation simulation;
    private Canvas canvas;
    private PixelWriter pixelWriter;
    private boolean isRunning = true;
    private int timeStepsPerFrame;

    public Engine(Simulation simulation, Canvas canvas, int timeStepsPerFrame) {
        this.simulation = simulation;
        this.canvas = canvas;
        this.timeStepsPerFrame = timeStepsPerFrame;

        pixelWriter = canvas.getGraphicsContext2D().getPixelWriter();
    }

    private void drawFrame() {
        drawTiles();
    }

    private void drawTiles() {
        WritablePixelFormat<IntBuffer> pixelFormat = PixelFormat.getIntArgbPreInstance();

        Tile[][] tiles = simulation.getSpace().getTiles();

        int[] newPixels = new int[(int) (canvas.getWidth() * canvas.getHeight())];
        for (int y = 0; y < tiles.length; ++y) {
            for (int x = 0; x < tiles[0].length; ++x) {
                if (tiles[y][x].getSubjects().size() != 0 && tiles[y][x].getSubjects().get(0) != null) {
                    newPixels[x + y * (int) canvas.getWidth()] = tiles[y][x].getSubjects().get(0).getColor().getIntRepresentation();
                } else {
                    double green = (((double) tiles[y][x].getMuDensity() / 1000000) * 80);

                    green = Math.pow(green / 255.0, 0.8) * 255;
                    newPixels[x + y * (int) canvas.getWidth()] = ((int) green << 8);
                }
            }
        }

        pixelWriter.setPixels(0, 0, (int) canvas.getWidth(), (int) canvas.getHeight(), pixelFormat, newPixels, 0, (int) canvas.getWidth());
    }

    @Override
    public void run() {
        while (isRunning) {
            for (int i = 0; i < timeStepsPerFrame; i++) {
                simulation.timeStep();
            }

            drawFrame();
        }
    }

    public void stop() {
        isRunning = false;
    }


    // Getters/Setters

    public int getTimeStepsPerFrame() {
        return timeStepsPerFrame;
    }

    public void setTimeStepsPerFrame(int timeStepsPerFrame) {
        this.timeStepsPerFrame = timeStepsPerFrame;
    }
}
