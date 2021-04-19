package edu.semo.casim;

public class Stochastic3State extends CAGrid {

    public Stochastic3State(int width, int height)
    {
        super(width, height, 3, true);
    }


    @Override
    public int[][] nextGeneration() {
        int [][] result = new int[getHeight()][getWidth()];
        double p[] = {0.1, 0.5, 0.5};
        double q;

        for(int y=0; y<getHeight(); y++) {
            for(int x=0; x<getHeight(); x++) {
                result[y][x] = getCell(x, y);
                int [] count = countNeighborhood(x, y, 1);

                // Rule option 1, if we fall into a state, we are done
                for(int i=0; i<p.length; i++) {
                    // skip our own state
                    if(i == result[y][x]) { continue; }

                    //compute escape probability
                    q = Math.pow(1-p[i], count[i]);

                    if(q < Math.random()) {
                        //fail to escape
                        result[y][x] = i;
                        break;
                    }
                }
            }
        }

        return result;
    }

    
}
