import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.stage.WindowEvent;

import java.util.Random;


/**
 * Canvas window
 * 
 * Canvas with grid of cells, which change their colors through time
 */
public class MyCanvas extends Stage{

    /**
     * canvas parameters
     * n, m, k
     */
    public int width, height, speed;

    /**
     * p
     */
    public double chance; 
    
    /**
     * cell size
     */
    public double cellSize;

    private Canvas canvas;
    public MyCell[][] cells;

    public Random random;
    public Launcher launcher;


    public MyCanvas(int width, int height, int speed, double chance, double cellSize, Launcher launcher){

        this.launcher = launcher;
        this.random = new Random();

        //saving the parameters
        this.width = width;
        this.height = height;
        this.speed = speed;
        this.chance = chance;
        this.cellSize = cellSize;

        //setting up the canvas
        this.canvas = new Canvas(width * cellSize, height * cellSize);

        //creating the threads
        this.cells = new MyCell[width][height];
        for(int i = 0; i < width; i++){
            for(int j = 0; j < height; j++){
                this.cells[i][j] = new MyCell(this, i, j);
            }
        }

        //setting up the window
        Pane container = new Pane();
        container.getChildren().add(canvas);

        Scene scene = new Scene(container, width * cellSize, height * cellSize);
        this.setScene(scene);

        this.canvas.setOnMouseClicked(new CellActivation());
        this.setOnCloseRequest(new CanvasExitEvent());

    }


    /**
     * Function used by threads to change color of cells on canvas
     * @param cell reference to cell, that provides all the neccessary information: coordinates of cell to change and color
     */
    public void draw(MyCell cell){
        
        Platform.runLater(() -> {
            GraphicsContext gc = this.canvas.getGraphicsContext2D();
            gc.setFill(cell.color);
            gc.fillRect(cell.x * this.cellSize, cell.y * this.cellSize, this.cellSize, this.cellSize);
        });
        

    }

    /**
     * Function to launch all the threads
     * 
     * To fully launch the canvas, this function must be used
     */
    public void launchCells(){
        for(int i = 0; i < this.width; i++){
            for(int j = 0; j < this.height; j++){
                this.cells[i][j].start();
            }
        }
    }

}

/**
 * Event handler for activation/deactivation of the canvas cells
 * 
 * The function takes coordinates of the click, calculates what the cell on the canvas was clicked, 
 * then changes the "active" flag on the related thread, freezing the thread
 * 
 */
class CellActivation implements EventHandler<MouseEvent>{
    @Override
    public void handle(MouseEvent event){

        Canvas canvas = (Canvas)event.getSource();
        MyCanvas stage = (MyCanvas)canvas.getScene().getWindow();
        
        //using mouse coordinates to get the cell, the click was performed on
        double 
            coord_x = event.getX(),
            coord_y = event.getY();
        int 
            x = (int)Math.floor(coord_x / stage.cellSize),
            y = (int)Math.floor(coord_y / stage.cellSize);
        
        MyCell cell = stage.cells[x][y];

        //changing the active flag
        cell.active = !cell.active;

        //printing information for debugging purposes
        System.out.println(cell.getName() + " " + cell.color);
    }
}

/**
 * Canvas window closing event handler
 * 
 * Used to kill the running threads when window is closed
 */
class CanvasExitEvent implements EventHandler<WindowEvent>{

    @Override
    public void handle(WindowEvent event){
        MyCanvas canvas = (MyCanvas)event.getSource();

        for(int i = 0; i < canvas.width; i++){
            for(int j = 0; j < canvas.height; j++){
                canvas.cells[i][j].interrupt();
            }
        }

        canvas.launcher.currentCanvas = null;
    }
}

