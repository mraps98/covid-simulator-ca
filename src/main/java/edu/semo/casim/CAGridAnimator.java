package edu.semo.casim;

import javafx.animation.AnimationTimer;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class CAGridAnimator extends ImageView {
    private CAGrid ca;
    private AnimationTimer timer;
    private Color [] caColors;
    private Updater update;
    private double speed;
    private long delay;
    private long lastFrame;
    private static final double MAX_FRAMERATE=60;

    public CAGridAnimator(CAGrid ca)
    {
        this.ca = ca;

        //generate our color list
        //generateColors();
        caColors = new Color[]{Color.ORANGE, Color.RED, Color.GREEN, Color.YELLOW, Color.PURPLE, Color.MAGENTA, Color.BLACK};

        //set up our last frame
        lastFrame = 0;

        //create the animation timer
        timer = new AnimationTimer() {
			@Override
			public void handle(long now) {
                long interval = now - lastFrame;
                if(interval >= delay) {
                    lastFrame = now;
				    update();
                }
			}
            
        };

        render();

        update = null;
    }


    /**
     * Generate color levels as distance through HSB space relative to the
     * distance through the state space
     */
    private void generateColors()
    {
        int levels;         //number of levels

        // allocate colors
        levels = ca.getNumStates();
        caColors = new Color[levels];

        //go through each level 
        for(int i=0; i<levels; i++) {
            // compute the color components
            double hue = 360.0 * (i+1.0) / levels;
            double sat = i == levels-1 ? 0.0 : 0.9;
            double bright;

            if(i == 0) {
                bright = 0.0;
            } else if(i == levels-1) {
                bright = 1.0;
            } else {
                bright = 0.9;
            }
            caColors[i] = Color.hsb(hue, sat, bright);
        }
    }


    /**
     * Start the animation
     */
    public void start()
    {
        timer.start();
    }


    /**
     * Stop the animation
     */
    public void stop()
    {
        timer.stop();
    }


    /**
     * Update the grid animation with the next generation.
     */
    public void update()
    {
        //go to the next generation
        ca.next();

        //render the grid
        render();

        //call the updater (if there is one)
        if(update != null) {
            update.update(this);
        }
    }


    /**
     * Render the grid.
     */
    public void render()
    {
        CAGrid ca = getCA();
        int w = ca.getWidth();
        int h = ca.getHeight();
        WritableImage img = new WritableImage(w, h);
        PixelWriter pixel = img.getPixelWriter();

        //display the grid
        for(int x=0; x<w; x++) {
            for(int y=0; y<h; y++) {
                pixel.setColor(x, y, caColors[ca.getCell(x,y)]);
            }
        }

        setImage(img); 
    }


    /**
     * Get the cellular automata referenced from this grid.
     * @return The CA animated by this node.
     */
    public CAGrid getCA()
    {
        return ca;
    }


    /**
     * Interface for providing a callback which is called each time the
     * animator updates the CA.
     */
    public interface Updater
    {
        public void update(CAGridAnimator grid);
    }


    /**
     * Set the update handler.
     * @param update the update handler
     */
    public void setUpdater(Updater update) 
    {
        this.update = update;
    }


    /**
     * Set the animation speed.
     * @param speed Double in interveral [0.0, 1.0]
     */
    public void setSpeed(double speed)
    {
        this.speed = speed;

        //set the stopped speed
        if(speed == 0) {
            stop();
            return;
        }

        //completely unlock at 100% speed
        if(speed >= 1.0) {
            this.delay = 0;
        } else {
            //set the delay
            this.delay = (long)(1.0e9/(speed * MAX_FRAMERATE)); 
        }
    }


    /**
     * Get the speed of the animation.
     * @return a double in the range [0.0, 1.0]
     */
    public double getSpeed()
    {
        return speed;
    }
}
