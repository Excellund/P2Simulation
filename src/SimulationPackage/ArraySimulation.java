package SimulationPackage;

import GFX.Display;
import SimulationPackage.Entities.Field;
import SimulationPackage.Entities.Tile;
import VectorPackage.Vector;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Random;

public class ArraySimulation implements Runnable
{
    private boolean isRunning = true;
    private Display scene;
    private int framesPerSecond, movesPerFrame;
    private Timer t;
    private Random random = new Random();

    private Tile[][] tiles;
    private ArrayList<Field> activeSubjects;

    // Constructor:
    public ArraySimulation(Display scene, int framesPerSecond, int movesPerFrame)
    {
        this.scene = scene;
        this.framesPerSecond = framesPerSecond;
        this.movesPerFrame = movesPerFrame;

        tiles = new Tile[scene.getHeight()][scene.getWidth()];
        activeSubjects = new ArrayList<>();

        for (int y = 0; y < scene.getHeight(); ++y)
        {
            for (int x = 0; x < scene.getWidth(); ++x)
            {
                tiles[y][x] = new Tile(random.nextInt(200000));
            }
        }

        for (int i = 0; i < 1500; ++i)
        {
            addSubject();
        }
    }

    // Methods:
    
    private void addSubject()
    {
        Field subject = new Field(new Vector(0, 0), 0, new Color(0, 0, 0), random.nextInt(100) + 50);
        subject.radius = random.nextInt(15) + 2;
        subject.position.x = random.nextInt(scene.getWidth());
        subject.position.y = random.nextInt(scene.getHeight());
        int r = random.nextInt(155) + 100;
        int b = random.nextInt(155) + 100;
        subject.color = new Color(r, 0, b);
    
        tiles[subject.position.y][subject.position.x].addSubject(subject);
        activeSubjects.add(subject);
    }
    
    private void moveSubjects()
    {
        ListIterator<Field> iterator = activeSubjects.listIterator();
        
        while (iterator.hasNext())
        {
            Field subject = iterator.next();
    
            Vector previous = subject.getPosition();
    
            tiles[subject.position.y][subject.position.x].removeSubject(subject);
            subject.setPosition(favoredMove(subject));
    
            if ((subject.position.x < 0 || subject.position.x >= scene.getWidth()) || (subject.position.y < 0 || subject.position.y >= scene.getHeight()))
            {
                subject.setPosition(previous);
            }
    
            tiles[subject.position.y][subject.position.x].addSubject(subject);
    
            if (tiles[subject.position.y][subject.position.x].getMuDensity() < 100000)
            {
                subject.subtractHealth(3);
        
                if (subject.getHealth() == 0)
                {
                    tiles[subject.position.y][subject.position.x].removeSubject(subject);
                    iterator.remove();
                }
            }
            else
            {
                subject.addHealth(1);
            }
    
            tiles[subject.position.y][subject.position.x].subtractDensity(100000);
        }
    }

    private Vector favoredMove(Field entity) //revise
    {
        int xLower, xHigher, yLower, yHigher, x, y, xOffset, yOffset;
        long last, current;
        
        last = 0;
        x = entity.position.x;
        y = entity.position.y;
        
        if (entity.getHealth() >= 290)
        {
            xLower = entity.position.x < 3 ? 0 : entity.position.x - 3;
            xHigher = entity.position.x > scene.getWidth() - 4 ? scene.getWidth() - 1 : entity.position.x + 3;
    
            yLower = entity.position.y < 3 ? 0 : entity.position.y - 3;
            yHigher = entity.position.y > scene.getHeight() - 4 ? scene.getHeight() - 1 : entity.position.y + 3;
            
            last = tiles[entity.position.y][entity.position.x].getSubjects().size();

            xOffset = random.nextInt(xHigher - xLower);
            yOffset = random.nextInt(yHigher - yLower);

            for (int i = yLower + yOffset; i <= yHigher + yOffset; ++i)
            {
                for (int j = xLower + xOffset; j <= xHigher + xOffset; ++j)
                {
                    int ic = i > yHigher ? i - yOffset : i;
                    int jc = j > xHigher ? j - xOffset : j;

                    if ((ic != entity.position.y || jc != entity.position.x) && tiles[ic][jc].getSubjects().size() > last)
                    {
                        last = tiles[ic][jc].getSubjects().size();
                        x = (jc - x) != 0 ? (jc - x) / Math.abs(jc - x) + x : x;
                        y = (ic - y) != 0 ? (ic - y) / Math.abs(ic - y) + y : y;
                    }
                }
            }
        }
        
        if (entity.getHealth() < 290 || last < 1)
        {
            xLower = entity.position.x < 1 ? 0 : entity.position.x - 1;
            xHigher = entity.position.x > scene.getWidth() - 2 ? scene.getWidth() - 1 : entity.position.x + 1;
    
            yLower = entity.position.y < 1 ? 0 : entity.position.y - 1;
            yHigher = entity.position.y > scene.getHeight() - 2 ? scene.getHeight() - 1 : entity.position.y + 1;
    
            last = tiles[entity.position.y][entity.position.x].getMuDensity();
    
            x = entity.position.x;
            y = entity.position.y;

            xOffset = random.nextInt(xHigher - xLower);
            yOffset = random.nextInt(yHigher - yLower);

            for (int i = yLower + yOffset; i <= yHigher + yOffset; ++i)
            {
                for (int j = xLower + xOffset; j <= xHigher + xOffset; ++j)
                {
                    int ic = i > yHigher ? i - 3 : i;
                    int jc = j > xHigher ? j - 3 : j;

                    if (tiles[ic][jc].getMuDensity() > last)
                    {
                        last = tiles[ic][jc].getMuDensity();
                        x = jc;
                        y = ic;
                    }
                }
            }
    
            if (last < 100000)
            {
                xLower = entity.position.x < 3 ? 0 : entity.position.x - 3;
                xHigher = entity.position.x > scene.getWidth() - 4 ? scene.getWidth() - 1 : entity.position.x + 3;
        
                yLower = entity.position.y < 3 ? 0 : entity.position.y - 3;
                yHigher = entity.position.y > scene.getHeight() - 4 ? scene.getHeight() - 1 : entity.position.y + 3;
        
                last = tileDensity(xLower, entity.position.x, yLower, entity.position.y, tiles);
                x = -1;
                y = -1;
        
                current = tileDensity(entity.position.x, xHigher, yLower, entity.position.y, tiles);
        
                if (current > last)
                {
                    x = 1;
                    y = -1;
                    last = current;
                }
        
                current = tileDensity(entity.position.x, xHigher, entity.position.y, yHigher, tiles);
        
                if (current > last)
                {
                    x = 1;
                    y = 1;
                    last = current;
                }
        
                current = tileDensity(xLower, entity.position.x, entity.position.y, yHigher, tiles);
        
                if (current > last)
                {
                    x = -1;
                    y = 1;
                    last = current;
                }
        
                if (last < 4000000)
                {
                    x = random.nextInt(2) == 1 ? 1 : -1;
                    y = random.nextInt(2) == 1 ? 1 : -1;
                }
        
                x += entity.position.x;
                y += entity.position.y;
            }
        }

        return new Vector(x, y);
    }

    private long tileDensity(int xLower, int xHigher, int yLower, int yHigher, Tile[][] tiles)
    {
        long combined = 0;

        for (int y = yLower; y < yHigher; ++y)
        {
            for (int x = xLower; x < xHigher; ++x)
            {
                combined += tiles[y][x].getMuDensity();
            }
        }

        return combined;
    }
    
    private void sustainPlankton()
    {
        for (int y = 0; y < tiles.length; ++y)
        {
            for (int x = 0; x < tiles[0].length; ++x)
            {
                tiles[y][x].addDensity(150);
            }
        }
    }
    
    private void sustainFields()
    {
        for (int y = 0; y < tiles.length; ++y)
        {
            for (int x = 0; x < tiles[0].length; ++x)
            {
                if (tiles[y][x].getSubjects().size() >= 2)
                {
                    int count = 0;

                    ListIterator<Field> iterator = tiles[y][x].getSubjects().listIterator();

                    while(iterator.hasNext())
                    {
                        Field subject = iterator.next();

                        if (subject.getHealth() >= 250)
                        {
                            subject.subtractHealth(100);
                            ++count;
                        }

                        if (count == 2)
                        {
                            addSubject();
                            break;
                        }
                    }
                }
            }
        }
    }
    
    @Override
    public void run()
    {
        int sleepTime = 1000 / framesPerSecond;

        t = new Timer(sleepTime, e ->
        {
            for (int i = 0; i < movesPerFrame; ++i)
            {
                moveSubjects();
                sustainPlankton();
                sustainFields();
            }

            scene.drawFrame(activeSubjects, tiles);

            if (!isRunning)
            {
                t.stop();
            }
        });

        t.start();
    }

    public void stop()
    {
        isRunning = false;
        scene.close();
    }
}
