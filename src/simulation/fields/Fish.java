package simulation.fields;

import simulation.FishGenome;
import simulation.Settings;
import simulation.SimulationSpace;
import simulation.Tile;
import utils.Color;
import utils.CountingRandom;
import utils.Vector;

import java.util.List;
import java.util.Objects;
import java.util.Random;

public class Fish implements Field {

    private Vector position;
    private float health;
    private float energy;
    private float size;
    private float speed;
    private FishGenome genome;
    private int matingTimer;
    private boolean isMature;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Fish fish = (Fish) o;
        return Float.compare(fish.health, health) == 0 &&
                Float.compare(fish.energy, energy) == 0 &&
                Float.compare(fish.size, size) == 0 &&
                Float.compare(fish.speed, speed) == 0 &&
                matingTimer == fish.matingTimer &&
                isMature == fish.isMature &&
                Objects.equals(position, fish.position) &&
                Objects.equals(genome, fish.genome);
    }

    @Override
    public int hashCode() {

        return Objects.hash(position, health, energy, size, speed, genome, matingTimer, isMature);
    }

    public Fish(FishGenome genome, Vector position) {
        this.genome = genome;
        this.position = position;
        this.size = genome.getSpawnSize();
        this.health = 1 + CountingRandom.getInstance().nextInt((int) (Settings.HEALTH_POINTS_PER_SIZE_POINTS * this.size * Settings.MAX_FISH_SIZE) + 1);
        this.energy = Settings.ENERGY_PER_EGG;
        this.matingTimer = 0;
        this.isMature = false;
        this.speed = genome.getSpeed();
    }

    public Fish(Vector position, float health, float energy, float size, float speed, FishGenome genome, int matingTimer, boolean isMature) {
        this.position = position;
        this.health = health;
        this.energy = energy;
        this.size = size;
        this.speed = speed;
        this.genome = genome;
        this.matingTimer = matingTimer;
        this.isMature = isMature;
    }

    //How compatible a fish is with another. This determines the likelihood of them mating.
    public float getCompatibility(Fish other) {
        float genomeSimilarity = genome.calculateSimilarity(other.genome);

        return (float) (1 / (1 + Math.pow((float) Math.E, -Settings.COMPATIBILITY_STEEPNESS * (genomeSimilarity - Settings.COMPATIBILITY_MIDPOINT))));
    }

    @Override
    public void update(SimulationSpace space) {
        move(space); //move towards optimal tile

        Tile currentTile = space.getTile(position);

        performAction(currentTile, space); //interact with environment

        energy -= size * Settings.FISH_SIZE_PENALTY +
                speed * Settings.FISH_SPEED_PENALTY +
                genome.getHerbivoreEfficiency() * Settings.FISH_HERBIVORE_EFFICIENCY_PENALTY +
                genome.getCarnivoreEfficiency() * Settings.FISH_CARNIVORE_EFFICIENCY_PENALTY +
                genome.getAttackAbility() * Settings.FISH_ATTACK_ABILITY_PENALTY;

        if (size < genome.getSize()) {
            size += Settings.FISH_GROWTH_RATE_PER_TIMESTEP;
        } else if (!isMature) {
            isMature = true;
        }

        if (energy <= 10) //maintain health based on energy level
        {
            health -= Settings.HEALTH_REDUCTION_ON_LOW_ENERGY;
        } else if (energy >= Settings.MIN_ENERGY_HEALTH_INCREASE) {
            health += Settings.ENERGY_HEALTH_INCREASE;

            if (health > size * Settings.MAX_FISH_SIZE * Settings.HEALTH_POINTS_PER_SIZE_POINTS) {
                health = size * Settings.MAX_FISH_SIZE * Settings.HEALTH_POINTS_PER_SIZE_POINTS;
            }
        }

        --matingTimer;

        if (!isAlive()) //check whether fish should be removed
        {
            space.queueRemoveField(this);
        }
    }

    private void performAction(Tile currentTile, SimulationSpace space) {
        float energyQuotient = 1 - (energy / (size * Settings.MAX_FISH_SIZE * Settings.ENERGY_POINTS_PER_SIZE_POINTS));
        float planktonDesire = nearbyPlanktonRating(currentTile) * energyQuotient;
        float matingDesire = nearbyMatingRating(currentTile);
        float predationDesire = nearbyPredationRating(currentTile) * energyQuotient;
        float scavengingDesire = nearbyScavengingRating(currentTile) * energyQuotient;

        if (planktonDesire >= matingDesire && planktonDesire >= predationDesire && planktonDesire >= scavengingDesire) {
            interactWithPlankton(space);
        } else if (matingDesire >= planktonDesire && matingDesire >= predationDesire && matingDesire >= scavengingDesire) {
            interactWithMostCompatible(currentTile.getFields(), space);
        } else if (predationDesire >= planktonDesire && predationDesire >= matingDesire && predationDesire >= scavengingDesire) {
            interactWithWeakestFish(currentTile.getFields(), space);
        } else {
            interactWithMostNutritious(currentTile.getFields(), space);
        }

        if (energy > size * Settings.MAX_FISH_SIZE * Settings.ENERGY_POINTS_PER_SIZE_POINTS) {
            energy = size * Settings.MAX_FISH_SIZE * Settings.ENERGY_POINTS_PER_SIZE_POINTS;
        }
    }

    private void move(SimulationSpace space) {
        int radius = (int) Settings.VISION_RANGE > 1 ? (int) Settings.VISION_RANGE - 1 : 1;
        boolean[][] tileValid = getSurroundingTileValidity(space, radius);
        float[][] tileRatings = calculateSurroundingTileRatings(space, tileValid, radius);

        Vector newPos = findOptimalTile(tileRatings, tileValid, new Vector(0, 0), new Vector(radius * 2, radius * 2));

        newPos = Vector.subtract(newPos, new Vector(radius, radius));
        newPos = Vector.add(position, newPos);
        moveTowards(newPos, space);
    }

    private void interactWithPlankton(SimulationSpace space) {
        Tile tile = space.getTile(position);

        if (size * Settings.MAX_FISH_SIZE > (tile.getMuDensity() / 1000f)) {
            energy += tile.getMuDensity() / 1000f;
        } else {
            energy += size * Settings.MAX_FISH_SIZE;
        }

        tile.subtractDensity((int) (size * Settings.MAX_FISH_SIZE * 1000f));
    }

    private void interactWithMostNutritious(List<Field> fields, SimulationSpace space) {
        float best = 0;
        Field mostNutritious = null;

        for (Field currentField : fields) {
            if (currentField instanceof FishEgg) {
                FishGenome eggGenome = ((FishEgg) currentField).getGenome();
                float current = size * genome.getCarnivoreEfficiency() * eggGenome.getSize();

                if (current > best) {
                    best = current;
                    mostNutritious = currentField;
                }
            } else if (currentField instanceof Carcass) {
                float carcassEnergy = ((Carcass) currentField).getNutrition() / (Settings.MAX_FISH_SIZE * Settings.ENERGY_POINTS_PER_SIZE_POINTS);
                float current = carcassEnergy * size * genome.getCarnivoreEfficiency();

                if (current > best) {
                    best = current;
                    mostNutritious = currentField;
                }
            }
        }

        if (mostNutritious != null) {
            interact(mostNutritious, space);
        }
    }

    private void interactWithWeakestFish(List<Field> fields, SimulationSpace space) {
        float bestRating = 0;
        float healthQuotient = Settings.MAX_FISH_SIZE * Settings.HEALTH_POINTS_PER_SIZE_POINTS;
        Fish weakestFish = null;

        for (Field currentField : fields) {
            if (currentField instanceof Fish && currentField != this) {
                Fish other = (Fish) currentField;

                float currentRating = 1 - (other.getSize() * (other.getHealth() / healthQuotient));
                currentRating *= 1 - getCompatibility(other);

                if (currentRating > bestRating) {
                    bestRating = currentRating;
                    weakestFish = other;
                }
            }
        }

        if (weakestFish != null) {
            interact(weakestFish, space);
        }
    }

    private void interactWithMostCompatible(List<Field> fields, SimulationSpace space) {
        float bestCompatibility = 0;
        Fish mostCompatible = null;

        for (Field currentField : fields) {
            if (currentField instanceof Fish && currentField != this) {
                float currentCompatibility = getCompatibility((Fish) currentField);

                if (currentCompatibility > bestCompatibility) {
                    bestCompatibility = currentCompatibility;
                    mostCompatible = (Fish) currentField;
                }
            }
        }

        if (mostCompatible != null) {
            interact(mostCompatible, space);
        }
    }

    private void moveTowards(Vector target, SimulationSpace space) {
        Vector newPosition = new Vector(position.x, position.y);

        for (int i = 0; i < (1 + speed * Settings.MAX_MOVES_CORRESPONDING_TO_SPEED); ++i) {
            if (newPosition.equals(target)) {
                break;
            }

            if (newPosition.x < target.x && newPosition.x < space.getWidth() - 1) {
                ++newPosition.x;
            } else if (newPosition.x > target.x && newPosition.x > 0) {
                --newPosition.x;
            }

            if (newPosition.y < target.y && newPosition.y < space.getHeight() - 1) {
                ++newPosition.y;
            } else if (newPosition.y > target.y && newPosition.y > 0) {
                --newPosition.y;
            }

            energy -= size * speed * Settings.ENERGY_SPEED_CORRELATION;

            if (energy < 0) {
                energy = 0;
            }
        }

        space.moveField(newPosition, this);
    }

    private void interact(Field currentField, SimulationSpace space) {
        if (currentField instanceof FishEgg) {
            energy += ((FishEgg) currentField).subtractEggs((int) (size * Settings.MAX_FISH_SIZE)) * Settings.ENERGY_PER_EGG * genome.getCarnivoreEfficiency();

            if (energy > size * Settings.MAX_FISH_SIZE * Settings.ENERGY_POINTS_PER_SIZE_POINTS) {
                energy = size * Settings.MAX_FISH_SIZE * Settings.ENERGY_POINTS_PER_SIZE_POINTS;
            }
        } else if (currentField instanceof Carcass) {
            energy += ((Carcass) currentField).consume((int) (size * Settings.MAX_FISH_SIZE)) * genome.getCarnivoreEfficiency();

            if (energy > size * Settings.MAX_FISH_SIZE * Settings.ENERGY_POINTS_PER_SIZE_POINTS) {
                energy = size * Settings.MAX_FISH_SIZE * Settings.ENERGY_POINTS_PER_SIZE_POINTS;
            }
        } else if (currentField instanceof Fish) {  //future maintainability
            for (Field field : space.getTile(position).getFields()) {
                if (!(field instanceof Fish)) {
                    return; //don't stack eggs or carcasses
                }
            }

            float compatibility = getCompatibility((Fish) currentField);

            if (compatibility >= Settings.MIN_COMPATIBILITY_MATING && energy >= Settings.MIN_ENERGY_MATING && ((Fish) currentField).getEnergy() >= Settings.MIN_ENERGY_MATING) {
                if (matingTimer <= 0 && ((Fish) currentField).getMatingTimer() <= 0 && isMature && ((Fish) currentField).isMature()) {
                    mate((Fish) currentField, space);
                }
            } else if (genome.getPredationTendency() >= Settings.MIN_PREDATION_TENDENCY) {
                attack((Fish) currentField, space);
            }
        }
    }

    private void attack(Fish other, SimulationSpace space) {
        float damage = genome.getAttackAbility() * Settings.MAX_ATTACK_DAMAGE;

        if (other.getHealth() <= damage) {
            energy -= other.getHealth() * Settings.ENERGY_CONSUMPTION_PER_ATTACK_DAMAGE;
            space.queueAddField(new Carcass((int) other.getEnergy(), position));
        } else {
            energy -= damage * Settings.ENERGY_CONSUMPTION_PER_ATTACK_DAMAGE;
        }

        other.subtractHealth(damage);
    }

    private void mate(Fish mate, SimulationSpace space) {
        this.energy -= Settings.MATING_ENERGY_CONSUMPTION * size;
        mate.energy -= Settings.MATING_ENERGY_CONSUMPTION * mate.getSize();

        FishGenome offSpringGenome = new FishGenome(this.genome, mate.getGenome());

        int numEggs = (int) ((Settings.MATING_ENERGY_CONSUMPTION * size + Settings.MATING_ENERGY_CONSUMPTION * mate.getSize()) / Settings.ENERGY_PER_EGG);

        FishEgg offSpring = new FishEgg(position, offSpringGenome, numEggs);

        matingTimer = (int) Settings.MATING_DELAY;
        mate.matingTimer = (int) Settings.MATING_DELAY;

        space.queueAddField(offSpring);
    }

    private float calculateTileRating(Tile tile, Vector position, SimulationSpace space) {
        float rating = 0;

        rating += nearbyPlanktonRating(tile);
        rating += nearbyMatingRating(tile);
        rating += nearbyScavengingRating(tile);
        rating += nearbyPredationRating(tile);
        rating += nearbySchoolingRating(position, space);
        rating -= nearbyPredatorRating(tile);

        return rating;
    }

    private float nearbyPlanktonRating(Tile tile) {
        return (tile.getMuDensity() / 1000000f) * genome.getHerbivoreTendency();
    }

    private float nearbyScavengingRating(Tile tile) {
        float bestRating = 0;

        for (Field currentField : tile.getFields()) {
            if (currentField instanceof FishEgg) {
                FishGenome genome = ((FishEgg) currentField).getGenome();
                float currentRating = size * genome.getScavengeTendency() * genome.getSize();

                if (currentRating > bestRating) {
                    bestRating = currentRating;
                }
            } else if (currentField instanceof Carcass) {
                float carcassEnergy = ((Carcass) currentField).getNutrition() / (Settings.MAX_FISH_SIZE * Settings.ENERGY_POINTS_PER_SIZE_POINTS);
                float currentRating = carcassEnergy * size * genome.getScavengeTendency();

                if (currentRating > bestRating) {
                    bestRating = currentRating;
                }
            }
        }

        return bestRating;
    }

    private float nearbyPredationRating(Tile tile) {
        float bestRating = 0;
        float healthQuotient = Settings.MAX_FISH_SIZE * Settings.HEALTH_POINTS_PER_SIZE_POINTS;

        for (Field currentField : tile.getFields()) {
            if (currentField instanceof Fish && currentField != this) {
                Fish other = (Fish) currentField;

                float currentRating = 1 - (other.getSize() * (other.getHealth() / healthQuotient));
                currentRating *= 1 - getCompatibility(other);

                if (currentRating > bestRating) {
                    bestRating = currentRating;
                }
            }
        }

        return bestRating;
    }

    private float nearbySchoolingRating(Vector position, SimulationSpace space) {
        int radius = 5;
        float bestRating = 0;
        int offset = CountingRandom.getInstance().nextInt(3) - 1;

        if (genome.getSchoolingTendency() >= 0.3) //schooling is very expensive, should be limited where it's possible
        {
            for (int y = position.y - radius + offset; y <= position.y + radius + offset; y += 2) {
                if (y >= 0 && y < space.getHeight()) {
                    for (int x = position.x - radius + offset; x <= position.x + radius + offset; x += 2) {
                        Vector currentPosition = new Vector(x, y);

                        if (x >= 0 && x < space.getWidth() && !currentPosition.equals(position)) {
                            for (Field currentField : space.getTile(currentPosition).getFields()) {
                                if (currentField instanceof Fish && currentField != this) {
                                    Fish other = (Fish) currentField;
                                    float currentRating = getCompatibility(other);

                                    if (currentRating > bestRating) {
                                        bestRating = currentRating;

                                        if (bestRating >= 0.9) {
                                            break; //another limit to schooling
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return bestRating * genome.getSchoolingTendency();
    }

    private float nearbyMatingRating(Tile tile) {
        float bestCompatibility = 0;

        if (energy >= Settings.MIN_ENERGY_MATING && matingTimer <= 0 && isMature) {
            for (Field currentField : tile.getFields()) {
                if (currentField instanceof Fish && currentField != this) {
                    float tempCompatibility = getCompatibility((Fish) currentField);

                    if (tempCompatibility > bestCompatibility) {
                        bestCompatibility = tempCompatibility;
                    }
                }
            }
        }

        return bestCompatibility;
    }

    private float nearbyPredatorRating(Tile tile) {
        float worstRating = 0;

        for (Field currentField : tile.getFields()) {
            if (currentField instanceof Fish && currentField != this) {
                Fish temp = (Fish) currentField;
                float compatibility = getCompatibility(temp);
                float currentRating = temp.getGenome().getPredationTendency() * temp.getGenome().getAttackAbility(); //danger level
                currentRating *= 1 - compatibility;

                if (currentRating > worstRating) {
                    worstRating = currentRating;
                }
            }
        }

        return worstRating / size;
    }

    private boolean[][] getSurroundingTileValidity(SimulationSpace space, int radius) {
        int seekSquareLength = radius * 2 + 1;
        boolean[][] validTiles = new boolean[seekSquareLength][seekSquareLength];

        for (int y = 0; y < seekSquareLength; y++) {
            for (int x = 0; x < seekSquareLength; x++) {
                validTiles[y][x] = space.isWithinBounds(Vector.add(position, new Vector(x - radius, y - radius)));
            }
        }

        return validTiles;
    }

    private float[][] calculateSurroundingTileRatings(SimulationSpace space, boolean[][] tileValid, int radius) {
        int seekSquareLength = radius * 2 + 1;
        float[][] tileRatings = new float[seekSquareLength][seekSquareLength];
        Vector currentPosition;

        for (int y = 0; y < seekSquareLength; y++) {
            for (int x = 0; x < seekSquareLength; x++) {
                if (tileValid[y][x]) {
                    currentPosition = Vector.add(position, new Vector(x - radius, y - radius));
                    tileRatings[y][x] = calculateTileRating(space.getTile(currentPosition), currentPosition, space);
                }
            }
        }

        return tileRatings;
    }

    private float sumTiles(float[][] tiles, boolean tileValidity[][], Vector min, Vector max) {
        float sum = 0;

        for (int y = min.y; y <= max.y; y++) {
            for (int x = min.x; x <= max.x; x++) {
                if (tileValidity[y][x]) {
                    sum += tiles[y][x];
                }
            }
        }

        return sum;
    }

    private Vector findOptimalTile(float[][] tileRatings, boolean tileValidity[][], Vector min, Vector max) {
        float dx = max.x - min.x;
        float dy = max.y - min.y;

        if (dx == 0 && dy == 0) {
            return min;
        }

        //Find bounding box coordinates
        int numAreas = 4;
        Vector[] mins = new Vector[4];
        Vector[] maxs = new Vector[4];

        mins[0] = min;
        maxs[0] = new Vector(min.x + (int) Math.floor(dx / 2), min.y + (int) Math.floor(dy / 2));
        mins[1] = new Vector(min.x + (int) Math.ceil(dy / 2), min.y);
        maxs[1] = new Vector(max.x, min.y + (int) Math.floor(dy / 2));
        mins[2] = new Vector(min.x, min.y + (int) Math.ceil(dy / 2));
        maxs[2] = new Vector(min.x + (int) Math.floor(dx / 2), max.y);
        mins[3] = new Vector(min.x + (int) Math.ceil(dx / 2), min.y + (int) Math.ceil(dy / 2));
        maxs[3] = max;

        //Find sums of areas
        float[] sums = new float[numAreas];
        for (int i = 0; i < numAreas; i++) {
            sums[i] = sumTiles(tileRatings, tileValidity, mins[i], maxs[i]);
        }

        int maxSumIndex = findMaxSumIndex(sums);

        //Do function recursively at sub-area
        return findOptimalTile(tileRatings, tileValidity, mins[maxSumIndex], maxs[maxSumIndex]);
    }

    private int findMaxSumIndex(float[] sums) {
        //Find max value, starting at a random index.
        Random random = CountingRandom.getInstance();
        int startIndex = random.nextInt(4);
        float maxSum = 0;
        int numAreas = sums.length;
        int maxSumIndex = startIndex;

        for (int i = startIndex; i < numAreas; i++) {
            if (sums[i] > maxSum + 0.001) {
                maxSum = sums[i];
                maxSumIndex = i;
            }
        }

        for (int i = 0; i < startIndex; i++) {
            if (sums[i] > maxSum + 0.001) {
                maxSum = sums[i];
                maxSumIndex = i;
            }
        }

        return maxSumIndex;
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
        if (Settings.COLOR_BY_TENDENCY < 1) {
            return genome.getColor();
        } else {
            return new Color(
                    genome.getPredationTendency(),
                    genome.getHerbivoreTendency(),
                    genome.getScavengeTendency()
            );
        }
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

    public boolean isMature() {
        return isMature;
    }

    public int getMatingTimer() {
        return matingTimer;
    }

    public float getSpeed() {
        return speed;
    }

    public FishGenome getGenome() {
        return genome;
    }

    public void subtractHealth(float amount) {
        health -= amount;
    }
}
