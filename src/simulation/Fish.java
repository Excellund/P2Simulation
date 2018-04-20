package simulation;

import utils.Color;
import utils.CountingRandom;
import utils.Vector;

import java.util.Random;

public class Fish implements Field {

    private Vector position;
    private float health;
    private float energy;
    private float size;
    private float speed;
    private FishGenome genome;

    public Fish(FishGenome genome, Vector position) {
        this.genome = genome;
        this.position = position;
        this.health = CountingRandom.getInstance().nextInt(100) + 50;
    }

    //How compatible a fish is with another. This determines the likelyhood of them mating.
    public float getCompatibility(Fish other) {
        float genomeSimilarity = genome.calculateSimilarity(other.genome);

        //Logistisk funktion
        //1/(1+e^(-50(x-0.85)))
        return (float) (1 / (1 + Math.pow((float) Math.E, -50 * (genomeSimilarity-0.85))));
    }

    @Override
    public void update(SimulationSpace space) {
        //Decrease energy
        //Decrease health if energy low
        //Increase health if energy high
        //Increase size if energy high and is able to grow due to genome

        Vector newPos = favoredMove(space);

        if (newPos.x >= 0 && newPos.x < space.getWidth() &&
                newPos.y >= 0 && newPos.y < space.getHeight()) {
            space.moveField(newPos, this);
        }

        if (space.getTile(position).getMuDensity() < 100000) {
            health -= 3;
        } else {
            health++;
        }

        space.getTile(position).subtractDensity(100000);
    }

    @Override
    public void interact(Field field, SimulationSpace space) {

    }

    @Override
    public boolean isAlive() {
        return health > 0;
    }

    @Override
    public Vector getPosition() {
        return position;
    }

    @Override
    public Color getColor() {
        return new Color(255, 0, 0);
    }

    public void attack(Fish other) {

    }

    public Fish mate(Fish mate) {
        FishGenome childGenome = new FishGenome(this.genome, mate.getGenome());
        //childGenome.mutate(1); //TODO: Remove when implemented
        return new Fish(childGenome, position);
    }


    //Getters
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

        if (getHealth() >= 290) {
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

                    if ((ic != position.y || jc != position.x) && space.getTile(jc, ic).getSubjects().size() > last) {
                        last = space.getTile(jc, ic).getSubjects().size();
                        x = (jc - x) != 0 ? (jc - x) / Math.abs(jc - x) + x : x;
                        y = (ic - y) != 0 ? (ic - y) / Math.abs(ic - y) + y : y;
                    }
                }
            }
        }

        if (health < 290 || last < 1) {
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

                    if (space.getTile(jc, ic).getMuDensity() > last) {
                        last = space.getTile(jc, ic).getMuDensity();
                        x = jc;
                        y = ic;
                    }
                }
            }

            if (last < 100000) {
                xLower = position.x < 3 ? 0 : position.x - 3;
                xHigher = position.x > space.getWidth() - 4 ? space.getWidth()- 1 : position.x + 3;

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
