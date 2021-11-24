package hmi;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class HMI extends Application {
    public static Stage stage;
    
    @Override
    public void start(Stage primaryStage) throws Exception{
        stage = primaryStage;
        
        Parent root = FXMLLoader.load(getClass().getResource("HmiFXML.fxml"));
        
        Scene scene = new Scene(root);
        primaryStage.setTitle("Notepad--");
        primaryStage.setScene(scene);
        
        scene.getStylesheets().add(getClass().getResource("/Styles/LightTheme.css").toExternalForm());
        
        Image icon = new Image(getClass().getResourceAsStream("/Images/orange_frog.png"));
        primaryStage.getIcons().add(icon);
        primaryStage.show();
        
    }

    public static void main(String[] args) {
        launch(args);
    }
}
