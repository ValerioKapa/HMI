package hmi;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class HMI extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("HmiFXML.fxml"));
        
        Scene scene = new Scene(root);
        primaryStage.setTitle("Notepad--");
        primaryStage.setScene(scene);
<<<<<<< HEAD
//      scene.getStylesheets().add(getClass().getResource("/styles/DarkTheme.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/Styles/LightTheme.css").toExternalForm());
        Image icon = new Image(getClass().getResourceAsStream("/Images/orange_frog.png"));
||||||| c297c67
//        scene.getStylesheets().add(getClass().getResource("/styles/DarkTheme.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/styles/LightTheme.css").toExternalForm());
        Image icon = new Image(getClass().getResourceAsStream("/images/orange_frog.png"));
=======
//        scene.getStylesheets().add(getClass().getResource("/styles/DarkTheme.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/styles/LightTheme.css").toExternalForm());
        Image icon = new Image(getClass().getResourceAsStream("/Images/orange_frog.png"));
>>>>>>> ValerioKapa-master
        primaryStage.getIcons().add(icon);
        primaryStage.show();
        
    }

    public static void main(String[] args) {
        launch(args);
    }
}
