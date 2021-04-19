package edu.semo.casim;

/**
 * Conway's game of life using my little framework.
 */
public class Life extends CAGrid {

    public Life(int width, int height)
    {
        super(width, height, 2, true);
    }


    @Override
    public int[][] nextGeneration() {
        int[][] result = new int[getHeight()][getWidth()];

        //get the new cell states
        for(int x=0; x<getWidth(); x++) {
            for(int y=0; y<getHeight(); y++) {
                int state = getCell(x, y);
                int[] counts = countNeighborhood(x, y, 1);
                counts[state]--; //remove myself

                //perform the rules
                if(counts[1] < 2) {
                    //starvation
                    state = 0;
                } else if(counts[1] > 3) {
                    //overcrowding
                    state = 0;
                } else if(counts[1] == 3) {
                    //reproduction
                    state = 1;
                }
                //stasis is implied if none of these are met.
                result[y][x] = state;
            }
        }
        return result;
    }
    
}
