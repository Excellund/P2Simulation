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

    @Override
    public void update(SimulationSpace space) {
        handleMovement(space); //Find and move towards optimal tile
        handleInteraction(space); //Find and perform desired action with environment

        //Subtract passive energy loss
        energy -= size *
                (speed * Settings.FISH_SPEED_PENALTY +
                        genome.getHerbivoreEfficiency() * Settings.FISH_HERBIVORE_EFFICIENCY_PENALTY +
                        genome.getCarnivoreEfficiency() * Settings.FISH_CARNIVORE_EFFICIENCY_PENALTY +
                        genome.getAttackAbility() * Settings.FISH_ATTACK_ABILITY_PENALTY);

        //Handle growth and maturity
        if (size < genome.getSize()) {
            size += Settings.FISH_GROWTH_RATE_PER_TIMESTEP;
        } else if (!isMature) {
            isMature = true;
        }

        //Maintain health based on energy level
        if (energy <= 10) {
            health -= Settings.HEALTH_REDUCTION_ON_LOW_ENERGY;
        } else if (energy >= Settings.MIN_ENERGY_HEALTH_INCREASE) {
            health += Settings.ENERGY_HEALTH_INCREASE;

            if (health > size * Settings.MAX_FISH_SIZE * Settings.HEALTH_POINTS_PER_SIZE_POINTS) {
                health = size * Settings.MAX_FISH_SIZE * Settings.HEALTH_POINTS_PER_SIZE_POINTS;
            }
        }

        --matingTimer;

        //Check whether fish should be removed
        if (!isAlive()) {
            space.queueRemoveField(this);
        }
    }

    //Handles interaction on the current tile for the fish
    private void handleInteraction(SimulationSpace space) {
        Tile currentTile = space.getTile(position);

        float energyQuotient = 1 - (energy / (size * Settings.MAX_FISH_SIZE * Settings.ENERGY_POINTS_PER_SIZE_POINTS));

        //Calculate desires
        float planktonDesire = calculateTilePlanktonRating(currentTile) * energyQuotient;
        float matingDesire = calculateTileMatingRating(currentTile);
        float predationDesire = calculateTilePredationRating(currentTile) * energyQuotient;
        float scavengingDesire = calculateTileScavengingRating(currentTile) * energyQuotient;

        //Interact according to highest desire
        if (planktonDesire >= matingDesire && planktonDesire >= predationDesire && planktonDesire >= scavengingDesire) {
            interactWithPlankton(space);
        } else if (matingDesire >= planktonDesire && matingDesire >= predationDesire && matingDesire >= scavengingDesire) {
            interactWithMostCompatible(currentTile.getFields(), space);
        } else if (predationDesire >= planktonDesire && predationDesire >= matingDesire && predationDesire >= scavengingDesire) {
            interactWithWeakestFish(currentTile.getFields(), space);
        } else {
            interactWithMostNutritious(currentTile.getFields(), space);
        }

        //Limit energy
        if (energy > size * Settings.MAX_FISH_SIZE * Settings.ENERGY_POINTS_PER_SIZE_POINTS) {
            energy = size * Settings.MAX_FISH_SIZE * Settings.ENERGY_POINTS_PER_SIZE_POINTS;
        }
    }

    //Handles movement for the fish
    private void handleMovement(SimulationSpace space) {
        int visionRadius = (int) Settings.VISION_RANGE > 1 ? (int) Settings.VISION_RANGE - 1 : 1; //Ensure vision radius is a valid value

        //Get tile ratings
        boolean[][] tileValid = getSurroundingTileValidity(space, visionRadius);
        float[][] tileRatings = calculateSurroundingTileRatings(space, tileValid, visionRadius);

        Vector newPos = findOptimalTile(tileRatings, tileValid, new Vector(0, 0), new Vector(visionRadius * 2, visionRadius * 2));

        //Subtract vision radius from the position vector to get a vector pointing to the optimal tile relative to the fish' position
        newPos = Vector.subtract(newPos, new Vector(visionRadius, visionRadius));

        //Get an absolute vector pointing to the optimal tile
        newPos = Vector.add(position, newPos);
        moveTowards(newPos, space);
    }

    //Interacts with plankton on current tile
    private void interactWithPlankton(SimulationSpace space) {
        Tile tile = space.getTile(position);

        if (size * Settings.MAX_FISH_SIZE > (tile.getMuDensity() / 1000f)) {
            addEnergy(tile.getMuDensity() / 1000f * genome.getHerbivoreEfficiency());
        } else {
            addEnergy(size * Settings.MAX_FISH_SIZE * genome.getHerbivoreEfficiency());
        }

        tile.subtractDensity((int) (size * Settings.MAX_FISH_SIZE * 1000f));
    }

    //Interacts with most nutritious carcass or egg on current tile
    private void interactWithMostNutritious(List<Field> fields, SimulationSpace space) {
        float best = 0;
        Field mostNutritious = null;

        //Find most nutritious
        for (Field currentField : fields) {
            if (currentField instanceof FishEgg) {
                FishEgg egg = (FishEgg) currentField;
                FishGenome eggGenome = egg.getGenome();

                //calculate nutrients of egg
                float current = size * genome.getCarnivoreEfficiency() * eggGenome.getSize();
                current *= 1 - getCompatibility(egg);

                if (current > best) {
                    best = current;
                    mostNutritious = currentField;
                }
            } else if (currentField instanceof Carcass) {
                //calculate nutrients of carcass
                float carcassEnergy = ((Carcass) currentField).getNutrition() / (Settings.MAX_FISH_SIZE * Settings.ENERGY_POINTS_PER_SIZE_POINTS);
                float current = carcassEnergy * size * genome.getCarnivoreEfficiency();

                if (current > best) {
                    best = current;
                    mostNutritious = currentField;
                }
            }
        }

        if (mostNutritious != null) {
            //interact with the most nutritious if it exists
            interact(mostNutritious, space);
        }
    }

    //How compatible a fish is with another
    private float getCompatibility(FishGenome other) {
        float genomeSimilarity = genome.calculateSimilarity(other);

        return (float) (1 / (1 + Math.pow((float) Math.E, -Settings.COMPATIBILITY_STEEPNESS * (genomeSimilarity - Settings.COMPATIBILITY_MIDPOINT))));
    }

    public float getCompatibility(Fish other) {
        return getCompatibility(other.genome);
    }

    private float getCompatibility(FishEgg other) {
        return getCompatibility(other.getGenome());
    }

    private void interactWithWeakestFish(List<Field> fields, SimulationSpace space) {
        //finds the weakest fish in the specified list of Fields
        //and interacts with it. Should only be used for predators.
        float bestRating = 0;
        float healthQuotient = Settings.MAX_FISH_SIZE * Settings.HEALTH_POINTS_PER_SIZE_POINTS;
        Fish weakestFish = null;

        for (Field currentField : fields) {
            if (currentField instanceof Fish && currentField != this) {
                Fish other = (Fish) currentField;

                //rate the current fish in terms of its health points and compatibility
                float currentRating = 1 - (other.getSize() * (other.getHealth() / healthQuotient));
                currentRating *= 1 - getCompatibility(other);

                if (currentRating > bestRating) {
                    bestRating = currentRating;
                    weakestFish = other;
                }
            }
        }

        if (weakestFish != null) {
            //interact with the weakest fish if it exists
            interact(weakestFish, space);
        }
    }

    private void interactWithMostCompatible(List<Field> fields, SimulationSpace space) {
        //finds the most compatible fish in the specified list of Fields
        //and interacts with it. If a fish is sufficiently compatible,
        //this will mean mating.
        float bestCompatibility = 0;
        Fish mostCompatible = null;

        //Find most compatible
        for (Field currentField : fields) {
            if (currentField instanceof Fish && currentField != this) {
                //calculate the compatibility
                float currentCompatibility = getCompatibility((Fish) currentField);

                if (currentCompatibility > bestCompatibility) {
                    bestCompatibility = currentCompatibility;
                    mostCompatible = (Fish) currentField;
                }
            }
        }

        //Interact with most compatible fish, if exists
        if (mostCompatible != null) {
            interact(mostCompatible, space);
        }
    }

    //Moves towards a tile based on the fish' speed
    private void moveTowards(Vector target, SimulationSpace space) {
        Vector newPosition = new Vector(position.x, position.y);

        float numMoves = (1 + speed * Settings.MAX_MOVES_CORRESPONDING_TO_SPEED);
        for (int i = 0; i < numMoves; ++i) {
            if (newPosition.equals(target)) {
                break;
            }

            //Determine direction on x axis
            if (newPosition.x < target.x && newPosition.x < space.getWidth() - 1) {
                ++newPosition.x;
            } else if (newPosition.x > target.x && newPosition.x > 0) {
                --newPosition.x;
            }

            //Determine direction on y axis
            if (newPosition.y < target.y && newPosition.y < space.getHeight() - 1) {
                ++newPosition.y;
            } else if (newPosition.y > target.y && newPosition.y > 0) {
                --newPosition.y;
            }

            //Subtract energy for each move
            energy -= size * speed * Settings.ENERGY_SPEED_CORRELATION;

            if (energy < 0) {
                energy = 0;
            }
        }

        //update the position of the fish on the SimulationSpace
        space.moveField(newPosition, this);
    }

    private void interact(Field currentField, SimulationSpace space) {
        //interacts with the specified Field.
        //If it is a fish egg, it will be scavenged if the eggs' genome is not sufficiently compatible.
        //If it is a carcass it will be scavenged.
        //If it is another fish, either predation or mating will be performed based on the compatibility.
        if (currentField instanceof FishEgg) {
            int numEggsConsumed = ((FishEgg) currentField).subtractEggs((int) (size * Settings.MAX_FISH_SIZE));

            addEnergy(numEggsConsumed * Settings.ENERGY_PER_EGG * genome.getCarnivoreEfficiency());

            if (energy > size * Settings.MAX_FISH_SIZE * Settings.ENERGY_POINTS_PER_SIZE_POINTS) {
                energy = size * Settings.MAX_FISH_SIZE * Settings.ENERGY_POINTS_PER_SIZE_POINTS;
            }
        } else if (currentField instanceof Carcass) {
            energy += ((Carcass) currentField).consume((int) (size * Settings.MAX_FISH_SIZE)) *
                    genome.getCarnivoreEfficiency();

            if (energy > size * Settings.MAX_FISH_SIZE * Settings.ENERGY_POINTS_PER_SIZE_POINTS) {
                energy = size * Settings.MAX_FISH_SIZE * Settings.ENERGY_POINTS_PER_SIZE_POINTS;
            }
        } else if (currentField instanceof Fish) {  //future maintainability
            //Ensure no new eggs or carcasses are places on top existing eggs or carcasses
            for (Field field : space.getTile(position).getFields()) {
                if (!(field instanceof Fish)) {
                    return; //don't stack eggs or carcasses
                }
            }

            float compatibility = getCompatibility((Fish) currentField);

            Fish currentFish = (Fish) currentField;
            if (compatibility >= Settings.MIN_COMPATIBILITY_MATING && energy >= Settings.MIN_ENERGY_MATING * size &&
                    currentFish.getEnergy() >= Settings.MIN_ENERGY_MATING * currentFish.size) {
                if (matingTimer <= 0 && ((Fish) currentField).getMatingTimer() <= 0 &&
                        isMature && ((Fish) currentField).isMature()) {
                    mate((Fish) currentField, space);
                }
            } else if (genome.getPredationTendency() >= Settings.MIN_PREDATION_TENDENCY) {
                attack((Fish) currentField, space);
            }
        }
    }

    private void attack(Fish other, SimulationSpace space) {
        //attacks the specified Fish based on attack damage
        //and subtracts energy based on the damage dealt.
        float damage = genome.getAttackAbility() * Settings.MAX_ATTACK_DAMAGE;

        if (other.getHealth() <= damage) {
            energy -= other.getHealth() * Settings.ENERGY_CONSUMPTION_PER_ATTACK_DAMAGE;
            space.queueAddField(new Carcass((int) other.getEnergy(), position));
        } else {
            energy -= damage * Settings.ENERGY_CONSUMPTION_PER_ATTACK_DAMAGE;
        }

        other.subtractHealth(damage);
    }

    //Mates fish with other fish and creates offspring
    private void mate(Fish mate, SimulationSpace space) {
        //Subtract energy from both fish
        this.energy -= Settings.MATING_ENERGY_CONSUMPTION * size;
        mate.energy -= Settings.MATING_ENERGY_CONSUMPTION * mate.getSize();

        //Reset mating timer
        matingTimer = (int) Settings.MATING_DELAY;
        mate.matingTimer = (int) Settings.MATING_DELAY;

        //Create offspring
        FishGenome offspringGenome = new FishGenome(this.genome, mate.getGenome());
        int numEggs = (int) ((Settings.MATING_ENERGY_CONSUMPTION * size + Settings.MATING_ENERGY_CONSUMPTION * mate.getSize()) / Settings.ENERGY_PER_EGG);
        FishEgg offspring = new FishEgg(position, offspringGenome, numEggs);

        space.queueAddField(offspring);
    }

    //Calculates the total rating for a tile
    private float calculateTileRating(Tile tile, Vector position, SimulationSpace space) {
        float rating = 0;

        rating += calculateTilePlanktonRating(tile);
        rating += calculateTileMatingRating(tile);
        rating += calculateTileScavengingRating(tile);
        rating += calculateTilePredationRating(tile);
        rating += calculateTileSchoolingRating(position, space);
        rating -= calculateTilePredatorRating(tile);

        return rating;
    }

    //Calculates plankton rating for a specific tile
    private float calculateTilePlanktonRating(Tile tile) {
        return (tile.getMuDensity() / 1000000f) * genome.getHerbivoreTendency();
    }

    private float calculateTileScavengingRating(Tile tile) {
        //returns the scavenging rating for a specific tile.
        //Only one Field will be considered.
        //The rating is based on the best Field on the specified Tile.
        float bestRating = 0;

        for (Field currentField : tile.getFields()) {
            if (currentField instanceof FishEgg) {
                //calculate the rating based on the egg and the scavenge tendency
                FishGenome genome = ((FishEgg) currentField).getGenome();
                float currentRating = size * genome.getScavengeTendency() * genome.getSize();

                if (currentRating > bestRating) {
                    bestRating = currentRating;
                }
            } else if (currentField instanceof Carcass) {
                //calculate the rating based on the carcass and the scavenge tendency
                float carcassEnergy = ((Carcass) currentField).getNutrition() / (Settings.MAX_FISH_SIZE * Settings.ENERGY_POINTS_PER_SIZE_POINTS);
                float currentRating = carcassEnergy * size * genome.getScavengeTendency();

                if (currentRating > bestRating) {
                    bestRating = currentRating;
                }
            }
        }

        return bestRating;
    }

    private float calculateTilePredationRating(Tile tile) {
        //Returns the predation rating for a specific tile (Based on own predation tendency)
        //The rating only considers the weakest Fish on the specified Tile.
        float bestRating = 0;
        float healthQuotient = Settings.MAX_FISH_SIZE * Settings.HEALTH_POINTS_PER_SIZE_POINTS;

        for (Field currentField : tile.getFields()) {
            if (currentField instanceof Fish && currentField != this) {
                Fish other = (Fish) currentField;
                //calculates the rating of the Fish based on its health and compatibility
                float currentRating = 1 - (other.getSize() * (other.getHealth() / healthQuotient));
                currentRating *= 1 - getCompatibility(other);

                if (currentRating > bestRating) {
                    bestRating = currentRating;
                }
            }
        }

        return bestRating;
    }

    //Calculates schooling rating for a specific tile
    private float calculateTileSchoolingRating(Vector position, SimulationSpace space) {
        int radius = 5;
        float bestRating = 0;
        int offset = CountingRandom.getInstance().nextInt(3) - 1; //Do check at a small random offset to avoid patterns

        if (genome.getSchoolingTendency() >= 0.3) //schooling is very expensive, should be limited where it's possible
        {
            //For efficiency purposes y is incremented by two every iteration
            for (int y = position.y - radius + offset; y <= position.y + radius + offset; y += 2) {
                if (y >= 0 && y < space.getHeight()) {

                    //For efficiency purposes x is incremented by two every iteration
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

    private float calculateTileMatingRating(Tile tile) {
        //Returns the mating rating for a specific tile.
        //The rating only considers the most compatible Fish on the specified Tile.
        float bestCompatibility = 0;

        if (energy >= Settings.MIN_ENERGY_MATING * size && matingTimer <= 0 && isMature) {
            for (Field currentField : tile.getFields()) {
                if (currentField instanceof Fish && currentField != this) {
                    //Calculate compatibility
                    float tempCompatibility = getCompatibility((Fish) currentField);

                    if (tempCompatibility > bestCompatibility) {
                        bestCompatibility = tempCompatibility;
                    }
                }
            }
        }

        return bestCompatibility;
    }

    private float calculateTilePredatorRating(Tile tile) {
        //Returns the predator rating for a specific tile (Based on predators on tile).
        //The rating only considers the most dangerous predator on the specified Tile.
        float worstRating = 0;

        for (Field currentField : tile.getFields()) {
            if (currentField instanceof Fish && currentField != this) {
                Fish temp = (Fish) currentField;
                float compatibility = getCompatibility(temp);
                //calculate the danger level based on the Fish' predation tendency, attack damage and compatibility
                float currentRating = temp.getGenome().getPredationTendency() * temp.getGenome().getAttackAbility();
                currentRating *= 1 - compatibility;

                if (currentRating > worstRating) {
                    worstRating = currentRating;
                }
            }
        }

        return worstRating / size;
    }

    //Gets a 2 dimensional boolean array indicating whether nearby tiles are valid
    private boolean[][] getSurroundingTileValidity(SimulationSpace space, int radius) {
        int seekSquareLength = radius * 2 + 1;
        boolean[][] validTiles = new boolean[seekSquareLength][seekSquareLength];

        for (int y = 0; y < seekSquareLength; y++) {
            for (int x = 0; x < seekSquareLength; x++) {
                //Tiles are valid if they are within the bounds of the simulation
                validTiles[y][x] = space.isWithinBounds(Vector.add(position, new Vector(x - radius, y - radius)));
            }
        }

        return validTiles;
    }

    private float[][] calculateSurroundingTileRatings(SimulationSpace space, boolean[][] tileValid, int radius) {
        //Calculates a 2 dimensional float array representing ratings of nearby tiles.
        //A Tile is valid if it is within the bounds of the SimulationSpace.
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
        //Returns the sum of the values in the specified two dimensional array.
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
        //Finds and returns a vector pointing to the most optimal Tile recursively.
        //The most optimal Tile is the best Tile in the best area.
        float dx = max.x - min.x;
        float dy = max.y - min.y;

        //If min and max are equal there are no more sub-areas to calculate
        if (dx == 0 && dy == 0) {
            return min;
        }

        int numAreas = 4;

        //Bounding boxes
        Vector[] mins = new Vector[4];
        Vector[] maxs = new Vector[4];

        //Find bounding box coordinates
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

        int maxSumIndex = findMaxIndex(sums);

        //Do function recursively at sub-area
        return findOptimalTile(tileRatings, tileValidity, mins[maxSumIndex], maxs[maxSumIndex]);
    }

    //Finds the index containing the maximum value. The checks are done starting from a random index, to ensure a different direction is picked, in case all values are equal
    private int findMaxIndex(float[] values) {
        //Get random starting index
        Random random = CountingRandom.getInstance();
        int startIndex = random.nextInt(4);

        float maxSum = 0;
        int numAreas = values.length;
        int maxSumIndex = startIndex;

        //Iterate from chosen random starting index to end of array
        for (int i = startIndex; i < numAreas; i++) {
            if (values[i] > maxSum + 0.001) { //Add a small value to avoid precision errors
                maxSum = values[i];
                maxSumIndex = i;
            }
        }

        //Iterate from 0 to chosen starting index
        for (int i = 0; i < startIndex; i++) {
            if (values[i] > maxSum + 0.001) { //Add a small value to avoid precision errors
                maxSum = values[i];
                maxSumIndex = i;
            }
        }

        return maxSumIndex;
    }

    public void addEnergy(float amount) {
        energy += amount;
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
        //Return based on settings (either color from genome or color from tendencies)
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
}
