package simulation.Subjects;

import simulation.*;
import utils.Color;
import utils.CountingRandom;
import utils.Vector;

import java.util.Random;

public class Fish implements Field {

    public final static int FISH_HEALTH_CONSUMPTION = 300;

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

        this.size = 0.5f; //TODO: REVISE
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

        Vector newPos = favoredMove(space);

        if (newPos.x >= 0 && newPos.x < space.getWidth() && newPos.y >= 0 && newPos.y < space.getHeight()) {
            space.moveField(newPos, this);
        }

        Tile currentTile = space.getTile(position);
        if (currentTile.getMuDensity() < 100000) {
            health -= 3;
        } else {
            health++;
        }

        Random r = CountingRandom.getInstance();

        //Mating
        if (currentTile.getSubjects().size() > 2) {
            for (Field subject : currentTile.getSubjects()) {
                if (subject != this) {
                    if (subject instanceof Fish) {
                        Fish fishSubject = (Fish) subject;

                        if (fishSubject.getHealth() >= 250 && this.getHealth() >= 250) {
                            //System.out.println("Comp: " + this.getCompatibility(fishSubject) + ", Simi: " + genome.calculateSimilarity(fishSubject.genome) + " - " + this.hashCode() + " : " + fishSubject.hashCode());
                            if (r.nextFloat() > this.getCompatibility(fishSubject)) {
                                interact(fishSubject, space);
                            }
                        }
                    }
                }
            }
        }

        currentTile.subtractDensity(100000);
    }

    public void interact(Field subject, SimulationSpace space) {
        if (subject instanceof FishEgg) {
            ((FishEgg) subject).subtractEggs((int) (size * Settings.MAX_FISH_SIZE));
            energy += (int) (size * Settings.MAX_FISH_SIZE) * Settings.ENERGY_PER_EGG;
        }
        else if (subject instanceof Carcass) {
            ((Carcass) subject).consume((int) (size * Settings.MAX_FISH_SIZE));
            energy += (int) (size * Settings.MAX_FISH_SIZE) * genome.getCarnivoreEfficiency();
        }
        else if (subject instanceof Fish) //future maintainability
        {
            if (getCompatibility((Fish) subject) >= Settings.MIN_COMPATIBILITY_MATING) {
                mate((Fish) subject, space);
            }
            else if (genome.getPredationTendency() >= Settings.MIN_PREDATION_TENDENCY) {
                attack((Fish) subject);
            }
        }
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
        return genome.getColor();
    }

    public void attack(Fish other) {
        int damage = (int) (genome.getAttackAbility() * Settings.MAX_ATTACK_DAMAGE);

        if (other.getHealth() < damage) {
            energy -= other.getHealth() * Settings.ENERGY_CONSUMPTION_PER_ATTACK_DAMAGE;
        } else {
            energy -= damage * Settings.ENERGY_CONSUMPTION_PER_ATTACK_DAMAGE;
        }

        other.subtractHealth(damage);
    }

    public void mate(Fish mate, SimulationSpace space) {
        this.health -= FISH_HEALTH_CONSUMPTION;
        mate.health -= FISH_HEALTH_CONSUMPTION;

        FishGenome childGenome = new FishGenome(this.genome, mate.getGenome());

        childGenome.mutate();

        Fish child = new Fish(childGenome, position);

        child.health = 10; //TODO: change, fix
        space.queueAddField(child);
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
        return genome.getSize();
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

                    if ((ic != position.y || jc != position.x) && space.getTile(jc, ic).getSubjects().size() - 1 > last) {
                        last = space.getTile(jc, ic).getSubjects().size() - 1;
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
