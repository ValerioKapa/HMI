package hmi;

import java.awt.Label;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class HmiFXMLController implements Initializable {
    
    @FXML
    private HBox details;
    @FXML
    private SplitPane fileName;
    @FXML
    private SplitPane wordsLines;
    @FXML
    private SplitPane lastSaved;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }    
    
}
