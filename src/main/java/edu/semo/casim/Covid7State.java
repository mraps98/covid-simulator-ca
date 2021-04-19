package edu.semo.casim;

public class Covid7State extends CAGrid {

    private int currentlyInfected;
    private int maxInfected;
    private int numDead;

    public Covid7State(int width, int height)
    {
        super(width, height, 7, true);
        currentlyInfected = 0;
        maxInfected = 0;
    }

    public int getCurrentlyInfected() {
        return currentlyInfected;
    }

    public int getMaxInfected() {
        return maxInfected;
    }

    public int getNumDead(){
        return numDead;
    }

    public double getPercentDead(){
        return Math.round(((double)numDead / ((double)getHeight() * (double)getWidth())) * 100);
    }

    @Override
    public int[][] nextGeneration() {
        currentlyInfected = 0;
        numDead = 0;
        int [][] result = new int[getHeight()][getWidth()];
        double rules[][] = {
            {0.9, 0, 0.5, 0.6, 0.4, 0.45, 0.5},
            {0.9, 0.1, 0.9, 0.1, 0.8, 0.2, 0.1},
            {0.7, 0.9, 0.8, 0.65, 0.99, 0.88, 0},
            {0.6, 0.8, 0.77, 0.8, 0.89, 0.92, 0.78},
            {0.1, 0.1, 0.8, 0.1, 0.9, 0.1, 0.1},
            {0.4, 0.1, 0.4, 0.6, 0.7, 0.5, 0.1},
            {0, 0, 0, 0, 0, 0, 1}
        };
        double q;

        for(int y=0; y<getHeight(); y++) {
            for(int x=0; x<getHeight(); x++) {
                result[y][x] = getCell(x, y);
                int [] count = countNeighborhood(x, y, 1);

                // Rule option 1, if we fall into a state, we are done
                for(int i=0; i<rules[result[y][x]].length; i++) {
                    // skip our own state
                    if(i == result[y][x]) { continue; }

                    //compute escape probability
                    q = Math.pow(1-rules[result[y][x]][i], count[i]);

                    if(q < Math.random()) {
                        //fail to escape
                        result[y][x] = i;
                        break;
                    }
                }

                //calculate numinfected
                if (getCell(x, y) == 0 || getCell(x,y) == 1)
                {
                    currentlyInfected++;
                }

                //calculate numDead
                if (getCell(x, y) == 6)
                {
                    numDead++;
                }
            }
        }

        //calculate maxinfected ever
        if(currentlyInfected > maxInfected) {
            maxInfected = currentlyInfected;
        }


        return result;
    }

    /**
     * Randomze the grid
     * @param percentInfected double from 0 to 100
     */
    public void randomize(int percentInfected){
        for(int x=0; x<getWidth(); x++) {
            for(int y=0; y<getHeight(); y++) {
                if(java.util.concurrent.ThreadLocalRandom.current().nextInt(0, 100) < percentInfected){
                    setCell(x, y, java.util.concurrent.ThreadLocalRandom.current().nextInt(0, 2));
                }else{
                    setCell(x, y, java.util.concurrent.ThreadLocalRandom.current().nextInt(2, getNumStates()));
                }
            }
        }
    }

    public void randomizeLowInfected(){
        for(int x=0; x<getWidth(); x++) {
            for(int y=0; y<getHeight(); y++) {
                setCell(x, y, java.util.concurrent.ThreadLocalRandom.current().nextInt(2, getNumStates()));
                if(java.util.concurrent.ThreadLocalRandom.current().nextInt(0, 100) < 1){
                    setCell(x, y, java.util.concurrent.ThreadLocalRandom.current().nextInt(0, 2));
                }
            }
        }
    }

    public void randomizeHighInfected(){
        for(int x=0; x<getWidth(); x++) {
            for(int y=0; y<getHeight(); y++) {
                setCell(x, y, java.util.concurrent.ThreadLocalRandom.current().nextInt(0, 2));
                if(java.util.concurrent.ThreadLocalRandom.current().nextInt(0, 100) < 1){
                    setCell(x, y, java.util.concurrent.ThreadLocalRandom.current().nextInt(2, getNumStates()));
                }
            }
        }
    }

}
