import javafx.application.Application;
import javafx.stage.Stage;


/**
 * App class
 * 
 * When started, the launcher window appears
 * Launcher window is used to get user input for parameters
 * Parameters are used to launch canvas with grid of cells
 * 
 */
public class App extends Application{
    

    public void start(Stage primaryStage){
        Launcher launcherWindow = new Launcher();
        launcherWindow.show();
    }
    
    public static void main(String args[]){
        launch(args);
    }
}