package simulation;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritablePixelFormat;
import javafx.scene.paint.Color;
import simulation.fields.Fish;
import ui.ContentBox;
import ui.DragListener;
import utils.Vector;

import java.nio.IntBuffer;
import java.util.ListIterator;

import static utils.VectorTransformer.*;

public class Engine implements Runnable {
    private Simulation simulation;
    private Canvas canvas;
    private AnimationTimer animationTimer;
    private PixelWriter pixelWriter;
    private boolean isRunning = true;
    private volatile boolean isPaused = false;
    private DataCollector dataCollector;
    private boolean isProcessing = false;

    private final double[][] vesselShape = {
            {0.0, -5.0, -20.0, -20.0, -5.0, 0.0},
            {0.0, 2.5, 2.5, -2.5, -2.5, 0.0}
    };

    private final double[][] netShape = {
            {-20, -60, -90, -120, -90, -60, -20},
            {3, 25, 10, 0, -10, -25, -3}
    };

    private final double[][] nettingPoints = {
            {-58, -63, -58, -63, -90, -93, -90, -90.8, -95.8, -91.1, -99.6, -91.3, -103.3, -91.4, -106.6, -91.4, -109.7, -91.3, -112.7, -91, -115.7, -90.8, -118, -90.8, -118, -91.1, -115.7, -91.3, -112.7, -91.4, -109.7, -91.4, -106.6, -91.3, -103.3, -91.1, -99.6, -90.8, -95.8},
            {26, 24, -26, -24, 10, 0, -10, -7, -8, -5, -6.8, -3, -5.6, -1, -4.5, 1, -3.4, 3, -2.4, 5, -1.4, 7, -0.6, -7, 0.6, -5, 1.4, -3, 2.4, -1, 3.4, 1, 4.5, 3, 5.6, 5, 6.8, 7, 8}
    };

    public Engine(Simulation simulation, Canvas canvas) {
        this.simulation = simulation;
        this.canvas = canvas;

        pixelWriter = canvas.getGraphicsContext2D().getPixelWriter();
        dataCollector = new DataCollector();

        animationTimer = new AnimationTimer() {
            long last = 0;

            @Override
            public void handle(long now) {
                //creates a graphical representation of the Simulation
                //if the specified time matches the target fps
                if (now > last + 1E9 / Settings.TARGET_FPS) {
                    drawFrame();
                    dataCollector.showLatestValues();

                    last = now;
                }
            }
        };
    }

    private void drawFrame() {
        //creates a graphical representation of the simulation
        drawTiles();
        drawVessels();
    }

    private void drawTiles() {
        //creates a graphical representation of the SimulationSpace
        //and draws it onto the UI's canvas
        WritablePixelFormat<IntBuffer> pixelFormat = PixelFormat.getIntArgbPreInstance();

        Tile[][] tiles = simulation.getSpace().getTiles();

        int[] newPixels = new int[(int) (canvas.getWidth() * canvas.getHeight())];

        for (int y = 0; y < tiles.length; ++y) {
            for (int x = 0; x < tiles[0].length; ++x) {
                synchronized (tiles[y][x].getFields()) {
                    utils.Color color;

                    if (tiles[y][x].getFields().size() != 0 && tiles[y][x].getFields().get(0) != null) {
                        //if the current tile contains at least one field, use the first one
                        color = tiles[y][x].getFields().get(0).getColor();
                        color = utils.Color.getGammaCorrected(color, Settings.FISH_GAMMA); //Gamma correction for fish and other fields

                        newPixels[x + y * (int) canvas.getWidth()] = color.getIntRepresentation();
                    } else {
                        //if the current tile contains no fields, use the plankton density
                        int green = (int) ((tiles[y][x].getMuDensity() / 1000000f) * 255);

                        color = new utils.Color(0, green, 0);
                        color = utils.Color.getGammaCorrected(color, Settings.PLANKTON_GAMMA); //Gamma correction for plankton

                        newPixels[x + y * (int) canvas.getWidth()] = color.getIntRepresentation();
                    }
                }
            }
        }

        pixelWriter.setPixels(0, 0, (int) canvas.getWidth(), (int) canvas.getHeight(), pixelFormat, newPixels, 0, (int) canvas.getWidth());
    }

    private void drawVessels() {
        //draws each vessel in the Simulation
        synchronized (simulation.getVessels()) {
            for (Vessel vessel : simulation.getVessels()) {
                drawVessel(vessel);
            }
        }

    }

    private void drawVessel(Vessel vessel) {
        //draws the input vessel onto the graphical representation of the SimulationSpace
        double[][] vesselMatrix;
        double[][] netMatrix;
        double[][] nettingMatrix;

        //transform the "draft" vessel polygon to fit the input vessel's direction, scale and translation
        vesselMatrix = rotatePolygon(vesselShape, new Vector(0, 0), vessel.getDirection());
        vesselMatrix = scalePolygon(vesselMatrix, Settings.VESSEL_SCALE);
        vesselMatrix = translatePolygon(vesselMatrix, vessel.getBow());

        GraphicsContext context = canvas.getGraphicsContext2D();

        context.setFill(Color.WHITE);
        context.fillPolygon(vesselMatrix[0], vesselMatrix[1], vesselMatrix[0].length);

        if (vessel.getNet() == null) {
            return;
        }

        //transform the "draft" net polygon to fit the input vessel's direction, scale and translation
        netMatrix = rotatePolygon(netShape, new Vector(0, 0), vessel.getDirection());
        netMatrix = scalePolygon(netMatrix, Settings.VESSEL_SCALE);
        netMatrix = translatePolygon(netMatrix, vessel.getBow());

        //transform the "draft" netting polygon to fit the input vessel's direction, scale and translation
        nettingMatrix = rotatePolygon(nettingPoints, new Vector(0, 0), vessel.getDirection());
        nettingMatrix = scalePolygon(nettingMatrix, Settings.VESSEL_SCALE);
        nettingMatrix = translatePolygon(nettingMatrix, vessel.getBow());

        //draw the population of fish inside the net before drawing the netting
        fillNet(netMatrix, vessel.getNet());

        //draw the net
        context.setStroke(new Color(1, 1, 1, 0.65));
        context.strokePolygon(netMatrix[0], netMatrix[1], netMatrix[0].length);
        context.strokeLine(nettingMatrix[0][0], nettingMatrix[1][0], nettingMatrix[0][1], nettingMatrix[1][1]);
        context.strokeLine(nettingMatrix[0][2], nettingMatrix[1][2], nettingMatrix[0][3], nettingMatrix[1][3]);

        //draw a bezier curve to represent the net opening
        context.beginPath();
        context.bezierCurveTo(nettingMatrix[0][4], nettingMatrix[1][4], nettingMatrix[0][5], nettingMatrix[1][5], nettingMatrix[0][6], nettingMatrix[1][6]);
        context.setStroke(new Color(1, 1, 1, 0.3));
        context.stroke();

        //draw the netting
        for (int i = 7; i < nettingMatrix[0].length - 1; i += 2) {
            context.strokeLine(nettingMatrix[0][i], nettingMatrix[1][i], nettingMatrix[0][i + 1], nettingMatrix[1][i + 1]);
        }
    }

    private void fillNet(double[][] netMatrix, Net net) {
        //draws the current population of fish in a net of a vessel
        GraphicsContext context = canvas.getGraphicsContext2D();

        //create a directional vector to represent one of the netting lines
        double x1 = netMatrix[0][2] - netMatrix[0][3];
        double y1 = netMatrix[1][2] - netMatrix[1][3];
        double length1 = Math.sqrt(Math.pow(x1, 2) + Math.pow(y1, 2));
        double vec1X = x1 / length1;
        double vec1Y = y1 / length1;

        //create a directional vector to represent the other netting line
        double x2 = netMatrix[0][4] - netMatrix[0][3];
        double y2 = netMatrix[1][4] - netMatrix[1][3];
        double length2 = Math.sqrt(Math.pow(x2, 2) + Math.pow(y2, 2));
        double vec2X = x2 / length2;
        double vec2Y = y2 / length2;

        double collectiveX;
        double collectiveY;
        double collectiveLength;
        double collectiveVecX;
        double collectiveVecY;

        ListIterator<Fish> iterator = net.getFish().listIterator();

        synchronized (net.getFish()) {
            //while the net is not full and fish to be drawn still remain,
            //scale the two vectors created above and create a directional vector between them
            for (int multiplierY = 1; multiplierY < 30 * Settings.VESSEL_SCALE; ++multiplierY) {
                collectiveX = vec1X * multiplierY - vec2X * multiplierY;
                collectiveY = vec1Y * multiplierY - vec2Y * multiplierY;
                collectiveLength = Math.sqrt(Math.pow(collectiveX, 2) + Math.pow(collectiveY, 2));
                collectiveVecX = collectiveX / collectiveLength;
                collectiveVecY = collectiveY / collectiveLength;

                //while the scaled directional vector has a length shorter than
                //the distance between the netting lines at the corresponding scale,
                //scale the directional vector
                for (int multiplierX = 1; multiplierX < collectiveLength - 1; ++multiplierX) {
                    if (!iterator.hasNext()) {
                        return; //all fish has been drawn
                    }

                    //draw a fish at the tip of the scaled directional vector
                    utils.Color color = iterator.next().getColor();
                    context.setFill(Color.rgb(color.getRed(), color.getGreen(), color.getBlue()));
                    context.fillRect(collectiveVecX * multiplierX + vec2X * multiplierY + netMatrix[0][3], collectiveVecY * multiplierX + vec2Y * multiplierY + netMatrix[1][3], 2, 2);
                }
            }
        }
    }

    @Override
    public void run() {
        //starts the simulation on a new thread
        animationTimer.start();

        while (isRunning) {
            if (isPaused) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    //do nothing
                }

                continue; //do nothing
            }

            isProcessing = true;

            simulation.timeStep(); //update plankton density and fields

            if (simulation.getCurrentTimeStep() % ((int) Settings.DATACOLLECTOR_APPEND_DELAY) == 0) {
                dataCollector.append(simulation.getSpace(), simulation.getCurrentTimeStep()); //write the current statistics to file
            }

            isProcessing = false;
        }
    }

    public synchronized void togglePause() {
        isPaused = !isPaused;
    }

    public void setIsPaused(boolean isPaused) {
        this.isPaused = isPaused;
    }

    public boolean isProcessing() {
        return isProcessing;
    }

    public boolean isPaused() {
        return isPaused;
    }

    public void stop() {
        //terminates the simulation
        isRunning = false;
        animationTimer.stop();
        dataCollector.dispose(); //release output stream
    }

    public ContentBox getStatisticsUI(double width, DragListener dragListener) {
        //returns a ContentBox containing TextFields showcasing the current data
        //collected by the DataCollector
        return dataCollector.getStatisticsUI(width, dragListener);
    }

    public void updateStatistics() {
        dataCollector.calculateLatest(simulation.getSpace(), simulation.getCurrentTimeStep());
    }
}
