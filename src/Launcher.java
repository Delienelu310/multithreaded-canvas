import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;

import javafx.event.EventHandler;
import javafx.event.ActionEvent;
import javafx.application.Platform;


/**
 * Window, that takes parameters n, m, k, p (width, height, speed, chance) and cell size
 */
public class Launcher extends Stage{

    //Elements in the launcher window: inputs, buttons and labels
    public TextField width, height, speed, chance, cellSize;
    private Button exit, launch;
    public Label state;

    //reference to current window with grid of cells
    public MyCanvas currentCanvas = null;

    /**
     * Launcher`s constructor
     * 
     * It is responsible for adding elements onto the window, setting them up, binding the event handlers etc
     */
    public Launcher(){

        //Setting up the elements
        width = new TextField("50");
        width.setPromptText("n parameter");
        height = new TextField("50");
        height.setPromptText("m parameter");

        speed = new TextField("400");
        speed.setPromptText("speed in ms");
        chance = new TextField("0.1");
        chance.setPromptText("chance (double number between 0.0 and 1.0)");

        cellSize = new TextField("10");
        cellSize.setPromptText("Cell size");

        //launch button launches the grid with cells
        launch = new Button("Launch");
        launch.setOnAction(new LaunchEvent());

        //exit button terminates the program
        exit = new Button("Exit");
        exit.setOnAction(new ExitEvent());

        this.setOnCloseRequest(new LauncherClosing());

        //State label shows current state of the program
        state = new Label("Ready to launch");

        //settings up the window
        HBox buttons = new HBox(launch, exit);
        VBox layout = new VBox(width, height, speed, chance, cellSize, buttons, state);

        Scene scene = new Scene(layout, 500, 500);
        this.setScene(scene);

    }
}

/**
 * Launch button event handler
 * 
 * It reads parameters from input and creates canvas window with grid of cells
 * It takes from inputs width, height, speed, chance and cell size
 */
class LaunchEvent implements EventHandler<ActionEvent>{

    @Override
    public void handle(ActionEvent event){

        
        Button launch = (Button)event.getSource();
        Launcher launcher = (Launcher)launch.getScene().getWindow();

        //we cannot launch new canvas if there already is a launched canvas (for optimisation reasons) 
        if(launcher.currentCanvas != null) return;

        //reading the parameters from inputs
        int width, height, speed;
        double chance, cellSize;

        try{
            width = Integer.parseInt(launcher.width.getText());
            height = Integer.parseInt(launcher.height.getText());
            speed = Integer.parseInt(launcher.speed.getText());
            chance = Double.parseDouble(launcher.chance.getText());
            cellSize = Double.parseDouble(launcher.cellSize.getText());
            
            //filtering the data
            if(width < 1 || height < 1 || speed < 1 || chance < 0.0 || chance > 1.0 || cellSize <= 0) throw new Exception("Inappropriate input");
        }catch(Exception e){
            launcher.state.setText("Inappropriate input");
            return;
        }

        //creating canvas window with read parameters
        MyCanvas canvas = new MyCanvas(width, height, speed, chance, cellSize, launcher);
        canvas.launcher = launcher;

        launcher.currentCanvas = canvas;

        //launching the canvas window
        canvas.show();
        canvas.launchCells();

        launcher.state.setText("Canvas launched");

    }
}

/**
 * Exit button event handler
 * 
 * Kills the running thread in current canvas window
 * then terminates the program
 */
class ExitEvent implements EventHandler<ActionEvent>{
    @Override
    public void handle(ActionEvent event){

        //killing running threads 
        Button exitButton = (Button)event.getSource();
        Launcher launcher = (Launcher)exitButton.getScene().getWindow();
        MyCanvas canvas = launcher.currentCanvas;
        if(canvas != null){
            for(int i = 0; i < canvas.width; i++){
                for(int j = 0; j < canvas.height; j++){
                    canvas.cells[i][j].interrupt();
                }
            }
        }

        //then terminating the program
        Platform.exit();
    }
}

/**
 * Launcher`s window closing event
 
    Used to kill all the threads in current canvas before terminating the application
 */
class LauncherClosing implements EventHandler<WindowEvent>{
    @Override
    public void handle(WindowEvent event){

        //firstly killing the threads 
        Launcher launcher = (Launcher)event.getSource();
        if(launcher.currentCanvas != null){
            for(int i = 0; i < launcher.currentCanvas.width; i++){
                for(int j = 0; j < launcher.currentCanvas.height; j++){
                    launcher.currentCanvas.cells[i][j].interrupt();
                }
            }
        }

        //then terminating the program
        Platform.exit();
    }
}

//commands in terminal:
//use:
//--module-path "C:/Users/HP/JAVA/openjfx-20.0.1_windows-x64_bin-sdk/javafx-sdk-20.0.1/lib" --add-modules javafx.controls

//to generate documentation:
// javadoc --module-path "C:/Users/HP/JAVA/openjfx-20.0.1_windows-x64_bin-sdk/javafx-sdk-20.0.1/lib" --add-modules javafx.controls  -d "documentation" "src/*"

//to compile:
// javac -d bin --module-path "C:/Users/HP/JAVA/openjfx-20.0.1_windows-x64_bin-sdk/javafx-sdk-20.0.1/lib" --add-modules javafx.controls -cp "src/" src/App.java

//to launch: (from bin)
// java --module-path "C:/Users/HP/JAVA/openjfx-20.0.1_windows-x64_bin-sdk/javafx-sdk-20.0.1/lib" --add-modules javafx.controls  App