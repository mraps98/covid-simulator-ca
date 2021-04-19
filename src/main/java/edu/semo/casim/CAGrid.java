package edu.semo.casim;

import java.util.concurrent.ThreadLocalRandom;

/**
 * This class stores and provides fascilities for the processing of a 2d cellular automata.
 * This class is an abstract class, where its update functions are provided by subclasses.
 */
public abstract class CAGrid
{
    private int numStates;    // The number of states
    private int [][] cell;    // The states of the cells
    private boolean toroidal; // True iff the CA uses torroidal neighborhoods
    private int generation;   // The number of generations which have been run.


    /**
     * Construct a New CA Grid.
     * @param width The width of the grid.
     * @param height The height of the grid.
     * @param numStates The number of states.
     * @param toroidal Whether this uses toroidal neighborhoods.
     */
    public CAGrid(int width, int height, int numStates, boolean toroidal)
    {
        //populate the fields
        this.numStates = numStates;
        this.toroidal = toroidal;
        this.generation = 0;

        //create the grid
        cell = new int[height][width];
    }


    /**
     * Get the width of the grid.
     * @return width as an integer.
     */
    public int getWidth()
    {
        return cell[0].length;
    }


    /**
     * Get the height of the grid.
     * @return height as an integer.
     */
    public int getHeight()
    {
        return cell.length;
    }


    /**
     * Get the number of states in this CA.
     * @return the number of states.
     */
    public int getNumStates()
    {
        return numStates;
    }


    /**
     * Get the current generation number.
     * @return generation number
     */
    public int getGeneration()
    {
        return generation;
    }


    /**
     * Get the specified cell. If this is a toroidal CA, we translate
     * the cell coordinates and return the toroidal entry.
     * If it is not toroidal, we return -1 on invalid entry.
     * @param x
     * @param y
     * @return
     */
    public int getCell(int x, int y)
    {
        int w, h;
        w = getWidth();
        h = getHeight();

        //handle an invalid request
        if(!toroidal && (x<0 || y<0 || x>=w || y>=h)) {
            return -1;
        }

        // perform any toroidal corrections
        if(toroidal) {
            if(x < 0) { x = x + w; }
            if(y < 0) { y = y + h; }
            if(x >= w) { x %= w; }
            if(y >= h) { y %= h; }
        }

        return cell[y][x];
    }


    /**
     * Set the value of the specified cell. Note that this function does not
     * perform any toroidal corrections. Also, no validation is performed by
     * this function, so you are asked to behave yourself!
     * @param x x coordinate (zero based)
     * @param y y coordiante (zero based)
     * @param state state of the cell.
     */
    public void setCell(int x, int y, int state)
    {
        cell[y][x]=state;
    }


    /**
     * Randomize the grid
     */
    public void randomize()
    {
        for(int x=0; x<getWidth(); x++) {
            for(int y=0; y<getHeight(); y++) {
                setCell(x, y, ThreadLocalRandom.current().nextInt(0, getNumStates()));
            }
        }        
    }

    public abstract void randomize(int percentInfected);

    /**
     * Get the contents of the neighborhood. If the neighborhood is non-toroidal, -1 
     * will be present in all invalid slots.
     * @param x x coordinate (zero based)
     * @param y y cooridnate (zero based)
     * @param radius the radius of the neighborhood
     * @return The list of cell states in the neighborhood as an array.
     */
    public int[] getNeighborhood(int x, int y, int radius)
    {
        int[] neighborhood;
        int size;

        // allocate the neighborhood
        size = 2*radius + 1;
        size *= size;
        neighborhood = new int[size];

        // populate the neighborhood 
        int i=0;
        for(int cy = y-radius; cy <= y+radius; cy++){
            for(int cx = x-radius; cx <= x+radius; cx++){
                neighborhood[i] = getCell(cx, cy);
                i++;
            }
        }

        return neighborhood;
    }


    /**
     * Count the number of each state in the neighborhood. 
     * @param x x coordinate (zero based)
     * @param y y coordinate (zero based)
     * @param radius the radius of the neighborhood
     * @return An array containing the count of each state. The ar[k] is the count of state k.
     */
    public int[] countNeighborhood(int x, int y, int radius)
    {
        
        int [] count = new int[getNumStates()];
        int [] neighborhood = getNeighborhood(x, y, radius);

        for(int i=0; i<neighborhood.length; i++) {
            // skip -1s 
            if(neighborhood[i]==-1) { continue; }

            count[neighborhood[i]]++;
        }

        return count;
    }


    /**
     * This function should return an array to replace the cell grid. 
     * This is where derivative classes can specify their transition rules.
     */
    public abstract int[][] nextGeneration();

    
    /**
     * Update the CA using the nextGeneration function.
     */
    public void next()
    {
        // update the grid
        cell = nextGeneration();

        // count the generations
        generation++;
    }
}