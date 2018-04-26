package simulation.fields;

import simulation.*;
import utils.Color;
import utils.CountingRandom;
import utils.Vector;

import java.util.Arrays;
import java.util.Random;

public class Fish implements Field {

    private Vector position;
    private float health;
    private float energy;
    private float size;
    private float speed;
    private FishGenome genome;
    private int matingTimer;

    public Fish(FishGenome genome, Vector position) {
        this.genome = genome;
        this.position = position;
        this.size = 0.5f; //TODO: REVISE
        this.health = CountingRandom.getInstance().nextInt((int) (Settings.HEALTH_POINTS_PER_SIZE_POINTS * getSize() * Settings.MAX_FISH_SIZE));
        this.energy = 15;

        matingTimer = CountingRandom.getInstance().nextInt(30);
    }

    public Fish(Vector position, float health, float energy, float size, float speed, FishGenome genome) {
        this.position = position;
        this.health = health;
        this.energy = energy;
        this.size = size;
        this.speed = speed;
        this.genome = genome;
    }

    //How compatible a fish is with another. This determines the likelihood of them mating.
    public float getCompatibility(Fish other) {
        float genomeSimilarity = genome.calculateSimilarity(other.genome);

        //Logistic function
        //1/(1+e^(-STEEPNESS(x-MIDPOINT)))
        return (float) (1 / (1 + Math.pow((float) Math.E, -Settings.COMPATIBILITY_STEEPNESS * (genomeSimilarity - Settings.COMPATIBILITY_MIDPOINT))));
    }

    @Override
    public void update(SimulationSpace space) {
        //Decrease energy
        //Decrease health if energy low
        //Increase health if energy high
        //Increase size if energy high and is able to grow due to genome
        --matingTimer;

        if (!isAlive()) {
            space.queueRemoveField(this);
        }

        //Vector newPos = favoredMove(space);
        boolean[][] tileValid = getSurroundingTileValidity(space, 3);
        float[][] tileRatings = calculateSurroundingTileRatings(space, tileValid, 3);
        /*for (int i = 0; i < tileRatings.length; i++) {
            System.out.println(Arrays.toString(tileRatings[i]));
        }
        System.out.println();*/

        Vector newPos = findOptimalTile(tileRatings, tileValid, new Vector(0,0), new Vector(7, 7));

        newPos = Vector.subtract(newPos, new Vector(3,3));
        newPos = Vector.add(position, newPos);

        if (space.isWithinBounds(newPos)) {
            space.moveField(newPos, this);
        }

        Tile currentTile = space.getTile(position);

        if (currentTile.getMuDensity() < 100000) {
            energy -= 3;
        } else {
            energy += 1;

            if (energy > size * Settings.MAX_FISH_SIZE * Settings.ENERGY_POINTS_PER_SIZE_POINTS) {
                energy = size * Settings.MAX_FISH_SIZE * Settings.ENERGY_POINTS_PER_SIZE_POINTS;
            }
        }

        //Mating
        if (currentTile.getSubjects().size() > 2) {
            for (Field subject : currentTile.getSubjects()) {
                if (subject != this) {
                    interact(subject, space);
                }
            }
        }

        if (energy <= 0) {
            //health -= Settings.HEALTH_REDUCTION_ON_LOW_ENERGY;
        } else if (energy >= Settings.MIN_ENERGY_HEALTH_INCREASE) {
            health += Settings.ENERGY_HEALTH_INCREASE;

            if (health > size * Settings.MAX_FISH_SIZE * Settings.HEALTH_POINTS_PER_SIZE_POINTS) {
                health = size * Settings.MAX_FISH_SIZE * Settings.HEALTH_POINTS_PER_SIZE_POINTS;
            }
        }

        currentTile.subtractDensity(100000);
    }

    public void interact(Field subject, SimulationSpace space) {
        if (subject instanceof FishEgg) {
            ((FishEgg) subject).subtractEggs((int) (size * Settings.MAX_FISH_SIZE));
            energy += (int) (size * Settings.MAX_FISH_SIZE) * Settings.ENERGY_PER_EGG;

            if (energy > size * Settings.MAX_FISH_SIZE * Settings.ENERGY_POINTS_PER_SIZE_POINTS) {
                energy = size * Settings.MAX_FISH_SIZE * Settings.ENERGY_POINTS_PER_SIZE_POINTS;
            }
        }
        else if (subject instanceof Carcass) {
            ((Carcass) subject).consume((int) (size * Settings.MAX_FISH_SIZE));
            energy += (int) (size * Settings.MAX_FISH_SIZE) * genome.getCarnivoreEfficiency();

            if (energy > size * Settings.MAX_FISH_SIZE * Settings.ENERGY_POINTS_PER_SIZE_POINTS) {
                energy = size * Settings.MAX_FISH_SIZE * Settings.ENERGY_POINTS_PER_SIZE_POINTS;
            }
        }
        else if (subject instanceof Fish) {  //future maintainability
            if (matingTimer <= 0) {
                for (Field field : space.getTile(position).getSubjects()) {
                    if (!(field instanceof Fish)) {
                        return; //don't stack eggs or carcasses
                    }
                }

                float compatibility = getCompatibility((Fish) subject);

                if (compatibility >= Settings.MIN_COMPATIBILITY_MATING && energy >= Settings.MIN_ENERGY_MATING && ((Fish) subject).energy >= Settings.MIN_ENERGY_MATING) {
                    mate((Fish) subject, space);
                } else if (genome.getPredationTendency() >= Settings.MIN_PREDATION_TENDENCY) {
                    attack((Fish) subject, space);
                }

                matingTimer = CountingRandom.getInstance().nextInt(30);
            }
        }
    }

    public void attack(Fish other, SimulationSpace space) {
        int damage = (int) (genome.getAttackAbility() * Settings.MAX_ATTACK_DAMAGE);

        if (other.getHealth() <= damage) {
            energy -= other.getHealth() * Settings.ENERGY_CONSUMPTION_PER_ATTACK_DAMAGE;
            space.queueAddField(new Carcass((int) (other.getSize() * Settings.MAX_FISH_SIZE * Settings.NUTRITION_PER_SIZE_POINT), position));
        } else {
            energy -= damage * Settings.ENERGY_CONSUMPTION_PER_ATTACK_DAMAGE;
        }

        other.subtractHealth(damage);
    }

    public void mate(Fish mate, SimulationSpace space) {
        this.energy -= Settings.MATING_ENERGY_CONSUMPTION;
        mate.energy -= Settings.MATING_ENERGY_CONSUMPTION;

        FishGenome offSpringGenome = new FishGenome(this.genome, mate.getGenome());

        int numEggs = (int) (0.5 * size * Settings.MAX_FISH_SIZE + CountingRandom.getInstance().nextInt((int) (0.5 * size * Settings.MAX_FISH_SIZE)));

        FishEgg offSpring = new FishEgg(position, offSpringGenome, numEggs);

        space.queueAddField(offSpring);
    }

    private float calculateTileRating(Tile tile) {
        float rating = 0;

        rating += tile.getMuDensity() / 1000000f;

        if (energy >= Settings.MIN_ENERGY_MATING) {
            float bestCompatibility = 0;
            float tempCompatibility;

            for (Field subject : tile.getSubjects()) {
                if (subject instanceof Fish && subject != this) {
                    tempCompatibility = getCompatibility((Fish) subject);

                    if (tempCompatibility > bestCompatibility) {
                        bestCompatibility = tempCompatibility;
                    }
                }
            }

            rating += bestCompatibility;
        }

        return rating;
    }

    private boolean[][] getSurroundingTileValidity(SimulationSpace space, int radius) {
        int seekSquareLength = radius * 2 + 1;
        boolean validTiles[][] = new boolean[seekSquareLength][seekSquareLength];

        for (int y = 0; y < seekSquareLength; y++) {
            for (int x = 0; x < seekSquareLength; x++) {
                validTiles[y][x] = space.isWithinBounds(Vector.add(position, new Vector(x - radius, y - radius)));
            }
        }

        return validTiles;
    }

    private float[][] calculateSurroundingTileRatings(SimulationSpace space, boolean[][] tileValid, int radius) {
        int seekSquareLength = radius * 2 + 1;

        float tileRatings[][] = new float[seekSquareLength][seekSquareLength];

        for (int y = 0; y < seekSquareLength; y++) {
            for (int x = 0; x < seekSquareLength; x++) {
                if (tileValid[y][x]) {
                    tileRatings[y][x] = calculateTileRating(space.getTile(Vector.add(position, new Vector(x - radius, y - radius))));
                }
            }
        }

        return tileRatings;
    }

    private float sumTiles(float[][] tiles, boolean tileValidity[][], Vector min, Vector max) {
        float sum = 0;

        for (int y = min.y; y < max.y; y++) {
            for (int x = min.x; x < max.x; x++) {
                if (tileValidity[y][x]) {
                    sum += tiles[y][x];
                }
            }
        }

        return sum;
    }

    private Vector findOptimalTile(float[][] tileRatings, boolean tileValidity[][], Vector min, Vector max) {
        if (max.x - min.x <= 1 && max.y - min.y <= 1) {
            return max;
        }

        float dx = max.x - min.x;
        float dy = max.y - min.y;

        //Find bounding box coordinates
        int numAreas = 4;
        Vector mins[] = new Vector[4];
        Vector maxs[] = new Vector[4];

        //TODO: make sure these are correct...
        mins[0] = min;
        maxs[0] = new Vector(min.x + (int) Math.ceil(dx / 2), min.y + (int) Math.ceil(dy / 2));
        mins[1] = new Vector(min.x + (int) Math.floor(dy / 2), min.y);
        maxs[1] = new Vector(max.x, min.y + (int) Math.ceil(dy / 2));
        mins[2] = new Vector(min.x, min.y + (int) Math.floor(dy / 2));
        maxs[2] = new Vector(min.x + (int) Math.ceil(dx / 2), max.y);
        mins[3] = new Vector(min.x + (int) Math.floor(dx / 2), min.y + (int) Math.floor(dy / 2));
        maxs[3] = max;
        /*System.out.println("Mins: ");
        System.out.println(Arrays.toString(mins));
        System.out.println("Maxs: ");
        System.out.println(Arrays.toString(maxs));
        System.out.println();*/

        //Find sums of areas
        float[] sums = new float[4];
        for (int i = 0; i < numAreas; i++) {
            sums[i] = sumTiles(tileRatings, tileValidity, mins[i], maxs[i]);
        }

        //Find max value, starting at a random index.
        Random r = CountingRandom.getInstance();
        int startIndex = r.nextInt(4);
        float maxSum = 0;
        int maxSumIndex = startIndex;

        /*for (int i = 0; i < 4; i++) {
            if (sums[i] > maxSum) {
                maxSum = sums[i];
                maxSumIndex = i;
            }
        }*/

        for (int i = startIndex; i < 4; i++) {
            if (sums[i] > maxSum) {
                maxSum = sums[i];
                maxSumIndex = i;
            }
        }

        for (int i = 0; i < startIndex; i++) {
            if (sums[i] > maxSum) {
                maxSum = sums[i];
                maxSumIndex = i;
            }
        }

        //Do function recursively at sub-area
        return findOptimalTile(tileRatings, tileValidity, mins[maxSumIndex], maxs[maxSumIndex]);
    }

    private Vector findGreatestElement(float[][] arr) {
        float greatestValue = 0;
        Vector greatestPos = new Vector(0,0);

        for (int y = 0; y < arr.length; y++) {
            for (int x = 0; x < arr[0].length; x++) {
                if (greatestValue < arr[y][x]) {
                    greatestValue = arr[y][x];
                    greatestPos.x = x;
                    greatestPos.y = y;
                }
            }
        }

        return greatestPos;
    }

    @Override
    public boolean isAlive() {
        return health > 0;
    }

    //Getters
    @Override
    public Vector getPosition() {
        return position;
    }

    @Override
    public Color getColor() {
        return genome.getColor();
    }

    public float getHealth() {
        return health;
    }

    @Override
    public void setPosition(Vector position) {
        this.position = position;
    }


    public float getEnergy() {
        return energy;
    }

    public float getSize() {
        return size;
    }

    public float getSpeed() {
        return speed;
    }

    public FishGenome getGenome() {
        return genome;
    }

    private Vector favoredMove(SimulationSpace space) { //TODO: revise
        Random random = CountingRandom.getInstance();
        int xLower, xHigher, yLower, yHigher, x, y, xOffset, yOffset;
        long last, current;

        last = 0;
        x = position.x;
        y = position.y;

        if (energy >= Settings.MIN_ENERGY_MATING) {
            xLower = position.x < 3 ? 0 : position.x - 3;
            xHigher = position.x > space.getWidth() - 4 ? space.getWidth() - 1 : position.x + 3;

            yLower = position.y < 3 ? 0 : position.y - 3;
            yHigher = position.y > space.getHeight() - 4 ? space.getHeight() - 1 : position.y + 3;

            last = space.getTile(position.x, position.y).getSubjects().size() - 1;

            xOffset = random.nextInt(xHigher - xLower);
            yOffset = random.nextInt(yHigher - yLower);

            for (int i = yLower + yOffset; i <= yHigher + yOffset; ++i) {
                for (int j = xLower + xOffset; j <= xHigher + xOffset; ++j) {
                    int ic = i > yHigher ? i - yOffset : i;
                    int jc = j > xHigher ? j - xOffset : j;

                    if ((ic != position.y || jc != position.x) && space.getTile(jc, ic).getSubjects().size() - 1 > last) {
                        last = space.getTile(jc, ic).getSubjects().size() - 1;
                        x = (jc - x) != 0 ? (jc - x) / Math.abs(jc - x) + x : x;
                        y = (ic - y) != 0 ? (ic - y) / Math.abs(ic - y) + y : y;
                    }
                }
            }
        }

        if (energy < Settings.MIN_ENERGY_MATING || last < 1) {
            xLower = position.x < 1 ? 0 : position.x - 1;
            xHigher = position.x > space.getWidth() - 2 ? space.getWidth() - 1 : position.x + 1;

            yLower = position.y < 1 ? 0 : position.y - 1;
            yHigher = position.y > space.getHeight() - 2 ? space.getHeight() - 1 : position.y + 1;

            last = space.getTile(position).getMuDensity();

            x = position.x;
            y = position.y;

            xOffset = random.nextInt(xHigher - xLower);
            yOffset = random.nextInt(yHigher - yLower);

            for (int i = yLower + yOffset; i <= yHigher + yOffset; ++i) {
                for (int j = xLower + xOffset; j <= xHigher + xOffset; ++j) {
                    int ic = i > yHigher ? i - 3 : i;
                    int jc = j > xHigher ? j - 3 : j;

                    if (space.getTile(jc, ic).getMuDensity() > last || (space.getTile(jc, ic).getMuDensity() == last && random.nextInt(5) == 1)) {
                        last = space.getTile(jc, ic).getMuDensity();
                        x = jc;
                        y = ic;
                    }
                }
            }

            if (last < 100000) {
                xLower = position.x < 3 ? 0 : position.x - 3;
                xHigher = position.x > space.getWidth() - 4 ? space.getWidth() - 1 : position.x + 3;

                yLower = position.y < 3 ? 0 : position.y - 3;
                yHigher = position.y > space.getHeight() - 4 ? space.getHeight() - 1 : position.y + 3;

                last = space.tileDensity(xLower, position.x, yLower, position.y);
                x = -1;
                y = -1;

                current = space.tileDensity(position.x, xHigher, yLower, position.y);

                if (current > last) {
                    x = 1;
                    y = -1;
                    last = current;
                }

                current = space.tileDensity(position.x, xHigher, position.y, yHigher);

                if (current > last) {
                    x = 1;
                    y = 1;
                    last = current;
                }

                current = space.tileDensity(xLower, position.x, position.y, yHigher);

                if (current > last) {
                    x = -1;
                    y = 1;
                    last = current;
                }

                if (last < 4000000) {
                    x = random.nextInt(2) == 1 ? 1 : -1;
                    y = random.nextInt(2) == 1 ? 1 : -1;
                }

                x += position.x;
                y += position.y;
            }
        }

        return new Vector(x, y);
    }

    public void subtractHealth(int amount) {
        health -= amount;
    }
}
