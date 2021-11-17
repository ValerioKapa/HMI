package hmi;

import java.awt.Label;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

public class HmiFXMLController implements Initializable {
    
    private boolean fileStatus = false;
    private String filePath;
    private String fn; // Its easy to get it for filePath too but I m kind of sleepy here
    
    @FXML
    private HBox details;
    @FXML
    private SplitPane fileName;
    @FXML
    private SplitPane wordsLines;
    @FXML
    private SplitPane lastSaved;
    @FXML
    private MenuItem open;
    @FXML
    private MenuItem save;
    @FXML
    private MenuItem saveAs;
    @FXML
    private MenuItem close;
    @FXML
    private TextArea taEdit;
    @FXML
    private javafx.scene.control.Label detailFileName;
    @FXML
    private javafx.scene.control.Label detailWords;
    @FXML
    private javafx.scene.control.Label detailLines;
    @FXML
    private javafx.scene.control.Label detailLastSaved;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }    

    @FXML
    private void openFile(ActionEvent event) throws FileNotFoundException, IOException {
        Window stage = details.getScene().getWindow();
        // Create FileChooser
        FileChooser fc = new FileChooser();
        fc.setTitle("Open File");
        fc.getExtensionFilters().addAll(
                new ExtensionFilter("Text Files", "*.txt"));
        // Here we are setting the path that we want to open
        File dir = new File("/home/dimitris/");
        fc.setInitialDirectory(dir);
        
        int wcount = 0;
        int lcount = 0;
        
        try {
            File selectedFile = fc.showOpenDialog(stage);
            filePath = selectedFile.getAbsolutePath();
            // With String method substring selectedFile.getAbsolutePath().substring(lastIndexOf("\\") + 1);
            fn = selectedFile.getName();
            BufferedReader bfr = new BufferedReader(new FileReader(selectedFile));
        
            String line;
            while ((line = bfr.readLine()) != null) {
                taEdit.appendText(line);
                taEdit.appendText("\n");
                String words[] = line.split("\\s+");
                wcount += words.length;
                lcount++;
            }
        } catch(NullPointerException e) {
            System.out.println(e.toString());
        }
        
        detailFileName.setText(fn);
        detailWords.setText(String.valueOf(wcount));
        detailLines.setText(String.valueOf(lcount));
    }

    @FXML
    private void saveFile(ActionEvent event) {
    }

    @FXML
    private void saveFileAs(ActionEvent event) {
    }

    @FXML
    private void closeEditor(ActionEvent event) {
        Stage stage = (Stage) details.getScene().getWindow();
        if(!fileStatus){
            Alert rusure = new Alert(AlertType.CONFIRMATION);
            rusure.setContentText("Are you sure that you want to close the application?");
            
            ButtonType btnSave = new ButtonType("Save");
            ButtonType btnNoSave = new ButtonType("No Save");
            ButtonType btnCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
            
            rusure.getButtonTypes().setAll(btnSave, btnNoSave, btnCancel);
            
            Optional<ButtonType> opt = rusure.showAndWait();
            if(opt.get() == btnSave) {
                File savedFile = new File(fn, filePath);
            } 
            if(opt.get() == btnNoSave) stage.close();
        }
    }

    @FXML
    private void onTextChanged(InputMethodEvent event) {
        detailFileName.setText(fn + "*");
    }
    
}
