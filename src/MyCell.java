import javafx.scene.paint.Color;

import java.lang.InterruptedException;
import java.lang.Thread;

/**
 * Thread related to certain thread, that performs color changing thorugh time
 */
public class MyCell extends Thread{

    /**
     * Reference to window
     * 
     * It is required to use its draw function to change color
     */
    private MyCanvas canvas;
    
    public int x, y;
    public Color color;

    /**
     * Active flag
     * 
     * Depending on this flag, related cell is frozen or active
     */
    public boolean active = false;

    public MyCell(MyCanvas canvas, int x, int y){
        //give a name to the thread
        super("My Cell thread " + x + " " + y);

        //starting values
        this.canvas = canvas;

        this.x = x;
        this.y = y;
        this.color = this.generateRandomColor();

        this.active = true;

        //starting state
        this.canvas.draw(this);
    }

    public void run(){
        try{
            while(true){


                long timeChosen = this.canvas.random.nextLong((long)this.canvas.speed / 2, 
                    (long)this.canvas.speed + (long)(this.canvas.speed / 2));

                Thread.sleep(timeChosen);


                if(!this.active) continue;

                synchronized(this.canvas){
                    System.out.println(this.getName() + " color started");

                    double choise = this.canvas.random.nextDouble();

                    if(choise < this.canvas.chance){
                        this.color = this.generateRandomColor();
                        this.canvas.draw(this);
                    }else{
                        try{
                            
                            this.color = this.getAverageColor();
                            this.canvas.draw(this);
                        }catch(AllNeighboursInactiveException e){

                        }
                    }

                    System.out.println(this.getName() + " finished");

                }
            }

        }catch(InterruptedException e){
            // System.out.println(this.getName() + " is interrupted: " + e.getMessage());
        }catch(Exception e){
            System.out.println(this.getName() + " has exception: " + e.getMessage());
        }
        
        
    }

    /**
     * function used by thread to generate random colors
     * 
     * @return javafx Color object which goes into color field
     */
    public Color generateRandomColor(){
        double red = this.canvas.random.nextDouble();
        double green = this.canvas.random.nextDouble();
        double blue = this.canvas.random.nextDouble();
        return new Color(red, green, blue, 1.0);
    }

    /**
     * function used to read colors of top, bottom, right and left active neighbours and get average color 
     * 
     * @return javafx color object, that goes into color field
     * @throws AllNeighboursInactiveException if all neighbours are inactive, the average color can`t be chosen, so the color does not change
     */
    public Color getAverageColor() throws AllNeighboursInactiveException{
        double red = 0;
        double green = 0;
        double blue = 0;
        int activeNeighbours = 0;

        //the neighbours
        MyCell[] neighbours = {
            this.canvas.cells[(this.x + 1) % this.canvas.width][this.y],
            this.canvas.cells[(this.x - 1 + this.canvas.width) % this.canvas.width][this.y],
            this.canvas.cells[this.x][(this.y + 1) % this.canvas.height],
            this.canvas.cells[this.x][(this.y - 1 + this.canvas.height) % this.canvas.height]
        };

        for(int i = 0; i < 4; i++){
            if(!neighbours[i].active) continue;

            activeNeighbours++;
            red += neighbours[i].color.getRed();
            green += neighbours[i].color.getGreen();
            blue += neighbours[i].color.getBlue();
        }

        if(activeNeighbours == 0) throw new AllNeighboursInactiveException();
        
        red /= activeNeighbours;
        green /= activeNeighbours;
        blue /= activeNeighbours;

        return new Color(red, green, blue, 1.0);
    }
}

/**
 * Exception used in function getAverageColor() 
 * 
 * Used when all surrounding neighbours are inactive and the average color cannot be taken
 */
class AllNeighboursInactiveException extends Exception{
    AllNeighboursInactiveException(){
        super("Neighbours are inactive");
    }
}
