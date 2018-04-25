package simulation;

import simulation.fields.Fish;
import utils.CountingRandom;
import utils.Vector;

import static utils.VectorTransformer.*;

public class Vessel {
    private int quota;
    private Net net;
    private Vector[] stern;
    private Vector bow;
    private Vector sought;
    private Vector max;
    private int direction;
    private CountingRandom random;
    private double temporaryX;
    private double temporaryY;

    public Vessel(Vector max) {
        random = CountingRandom.getInstance();

        this.max = max;
        this.quota = (int) (Settings.FISHING_QUOTAS_MIN + random.nextInt((int) (Settings.FISHING_QUOTAS_MAX - Settings.FISHING_QUOTAS_MIN)));

        bow = randomBow();
        temporaryX = bow.x;
        temporaryY = bow.y;
        newSought();
        stern = new Vector[2];
        transform(bow, getOrientation());

        float favoredMorphology = Settings.MIN_MORPHOLOGY + (random.nextFloat() % (Settings.MAX_MORPHOLOGY - Settings.MIN_MORPHOLOGY));

        net = new Net(favoredMorphology);
    }

    public Vessel(int quota, Vector max, float favoredMorphology) {
        random = CountingRandom.getInstance();

        this.max = max;
        this.quota = quota;

        bow = randomBow();
        temporaryX = bow.x;
        temporaryY = bow.y;
        newSought();
        stern = new Vector[2];
        transform(bow, getOrientation());
        net = new Net(favoredMorphology);
    }

    public Vessel(int quota, Vector bow, Vector sought, float favoredMorphology) {
        this.quota = quota;
        this.bow = bow;
        this.sought = sought;

        stern = new Vector[2];
        transform(bow, getDirection());
        net = new Net(favoredMorphology);
    }

    public Vessel(int quota, Vector bow, Vector sought, Vector max, double temporaryX, double temporaryY, float netFavoredMorphology, Fish[] netFish) {
        this.quota = quota;
        this.bow = bow;
        this.sought = sought;
        this.max = max;
        this.temporaryX = temporaryX;
        this.temporaryY = temporaryY;

        random = CountingRandom.getInstance();
        stern = new Vector[2];
        transform(bow, getDirection());

        this.net = new Net(netFavoredMorphology, netFish);
    }

    private Vector randomBow() {
        int x, y;

        random = CountingRandom.getInstance();

        if (random.nextInt(2) == 0) {
            if (random.nextInt(2) == 0) {
                x = (int) -Settings.VESSEL_TRAVEL_DISTANCE;
            } else {
                x = (int) (max.x + Settings.VESSEL_TRAVEL_DISTANCE);
            }
        } else {
            x = random.nextInt(max.x);
        }

        if (x == (int) -Settings.VESSEL_TRAVEL_DISTANCE || x == (int) (max.x + Settings.VESSEL_TRAVEL_DISTANCE)) {
            y = random.nextInt(max.y);
        } else {
            if (random.nextInt(2) == 0) {
                y = (int) -Settings.VESSEL_TRAVEL_DISTANCE;
            } else {
                y = (int) (max.y + Settings.VESSEL_TRAVEL_DISTANCE);
            }
        }

        return new Vector(x, y);
    }

    private int getOrientation() {
        double radians = Math.atan2(sought.y - bow.y, sought.x - bow.x);
        double degrees = Math.toDegrees(radians);

        if (degrees < 0) {
            degrees += 360;
        }

        return (int) degrees;
    }

    private void transform(Vector bow, int direction) {
        double[][] location = {{0, -20, -20},
                               {0,   3,  -3}};

        location = rotatePolygon(location, new Vector(0, 0), direction);
        location = scalePolygon(location, 2);
        location = translatePolygon(location, bow);

        this.bow = new Vector((int) location[0][0], (int) location[1][0]);
        this.stern[0] = new Vector((int) location[0][1], (int) location[1][1]);
        this.stern[1] = new Vector((int) location[0][2], (int) location[1][2]);
        this.direction = direction;
    }

    private void newSought() {
        int soughtX;
        int soughtY;

        if (bow.x > 0 && bow.x < max.x && bow.y > 0 && bow.y < max.y) {
            return; //still looking for current sought position
        }

        if (bow.x == (int) (max.x + Settings.VESSEL_TRAVEL_DISTANCE)) {
            soughtX = (int) -Settings.VESSEL_TRAVEL_DISTANCE;
            soughtY = random.nextInt(max.y);
        } else if (bow.x == -Settings.VESSEL_TRAVEL_DISTANCE) {
            soughtX = (int) (max.x + Settings.VESSEL_TRAVEL_DISTANCE);
            soughtY = random.nextInt(max.y);
        } else {
            if (bow.y == (int) (max.y + Settings.VESSEL_TRAVEL_DISTANCE)) {
                soughtX = random.nextInt(max.x);
                soughtY = (int) -Settings.VESSEL_TRAVEL_DISTANCE;
            } else {
                soughtX = random.nextInt(max.x);
                soughtY = (int) (max.y + Settings.VESSEL_TRAVEL_DISTANCE);
            }
        }

        sought = new Vector(soughtX, soughtY);
    }

    public boolean quotaIsSpent() {
        return quota <= 0;
    }

    public void timeStep(SimulationSpace space) {
        move();
        net.timeStep(getDirection(), 2, bow, stern, space);
    }

    private void move() {
        if (sought.x == bow.x && sought.y == bow.y) {
            newSought();

            if (net != null) {
                quota -= net.resetTrawl(); // empty net
            }
        }

        int x = sought.x - bow.x;
        int y = sought.y - bow.y;

        double length = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));

        temporaryX += x / length;
        temporaryY += y / length;

        bow = new Vector((int) temporaryX, (int) temporaryY);
        transform(bow, getOrientation());
    }

    public int getQuota() {
        return quota;
    }

    public Net getNet() {
        return net;
    }

    public int getDirection() {
        return direction;
    }

    public Vector getBow() {
        return bow;
    }

    public Vector[] getStern() {
        return stern;
    }

    public final Vector getSought() {
        return sought;
    }

    public Vector getMax() {
        return max;
    }

    public double getTemporaryX() {
        return temporaryX;
    }

    public double getTemporaryY() {
        return temporaryY;
    }
}
