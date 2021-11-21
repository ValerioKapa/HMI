package hmi;

import com.pdfjet.A4;
import com.pdfjet.CoreFont;
import com.pdfjet.Font;
import com.pdfjet.PDF;
import com.pdfjet.Page;
import com.pdfjet.TextBox;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.stage.Window;

public class HmiFXMLController implements Initializable {
    
    private File selectedFile = new File("New File");
    boolean isNew = true;
    boolean isModified = false;
    int wcount = 0;
    int lcount = 0;
    
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
    private Label detailFileName;
    @FXML
    private Label detailWords;
    @FXML
    private Label detailLines;
    @FXML
    private Label detailLastSaved;
    @FXML
    private Slider fontSlider;
    @FXML
    private int caretPossitionA;
    private int fontSize = 18;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        taEdit.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> ov, String t, String t1) {
                if (!isModified) {
                    isModified = true;
                    detailFileName.setText(detailFileName.getText() + "*");
                }
                
                detailWords.setText(String.valueOf(taEdit.getText().split("\\s+").length));
                detailLines.setText(String.valueOf(taEdit.getText().split("\n").length));
                if (taEdit.getText().isEmpty()) {
                    wcount = lcount = 0;
                    detailWords.setText("0");
                    detailLines.setText("0");
                }
            }
        });
        
        fontSlider.valueProperty().addListener((observable, oldValue, newValue) -> {

            taEdit.setStyle("-fx-font-size: " + newValue.intValue() + "px");
        });
    }    
    
    @FXML
    private void openFile(ActionEvent event) throws FileNotFoundException, IOException {
        isNew = false;
        if (isModified) closeOperation();
        Window stage = details.getScene().getWindow();
        FileChooser fc = createFileChooser("open");
        try {
            File sFile = fc.showOpenDialog(stage);
            selectedFile = sFile;
            taEdit.clear();
            Scanner line = new Scanner(selectedFile);
            while (line.hasNextLine()) {
                taEdit.appendText(line.nextLine());
                if (line.hasNext())
                    taEdit.appendText("\n");
                String words[] = line.toString().split("\\s+");
                wcount += words.length;
                lcount++;
                
                isModified = false;
        }
            /*BufferedReader bfr = new BufferedReader(new FileReader(selectedFile));
            String line;
            while ((line = bfr.readLine()) != null) {
                taEdit.appendText(line);
                if (!(bfr.readLine() == null)) //We need to fix this prompt!
                    taEdit.appendText("\n");
                String words[] = line.split("\\s+");
                wcount += words.length;
                lcount++;
                
                isModified = false;
            }*/
            detailFileName.setText(selectedFile.getName());
            detailLastSaved.setText(convertTime(selectedFile.lastModified()));
            detailWords.setText(String.valueOf(wcount));
            detailLines.setText(String.valueOf(lcount));
        } catch(Exception e) {
            System.out.println("The User didnt selected any file : " + e.toString());
        }
    }

    @FXML
    private void saveFile(ActionEvent event) {
        Window stage = details.getScene().getWindow();
        FileChooser fc = createFileChooser("save");
        if (isModified) {
            if (isNew) {
                try {
                    File newFile = fc.showSaveDialog(stage);
                    saveTextToFile(newFile);
                } catch (Exception e) {
                    System.out.println("The User didnt selected any file : " + e.toString());
                }
            }
            saveTextToFile(selectedFile);
        }
    }

    @FXML
    private void saveFileAs(ActionEvent event) {
        Window stage = details.getScene().getWindow();
        FileChooser fc = createFileChooser("save");
        try {
            File savedFile = fc.showSaveDialog(stage);
            saveTextToFile(savedFile);
        } catch (NullPointerException e) {
            System.out.println(e.toString());
        }
    }

    @FXML
    private void closeEditor(ActionEvent event) {
        Stage stage = (Stage) details.getScene().getWindow();
        if(isModified) closeOperation();
        else stage.close();
    }
    
    private FileChooser createFileChooser(String opt) {
        FileChooser fc = new FileChooser();
        if (opt.equals("open")) fc.setTitle("Open File");
        else fc.setTitle("Save File");
        fc.getExtensionFilters().addAll(
                new ExtensionFilter("Text Files", "*.txt"));
        // Here we are setting the path that we want to open
        File dir = new File(System.getProperty("user.home"));
        fc.setInitialDirectory(dir);
        
        return fc;
    }
    
    private void saveTextToFile(File f) {
        ObservableList<CharSequence> paragraph = taEdit.getParagraphs();
        Iterator<CharSequence> iter = paragraph.iterator();
        try {
            File file = new File(f.getAbsolutePath());
            f.delete();
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            while (iter.hasNext()) {
                CharSequence seq = iter.next();
                writer.append(seq);
                writer.newLine();
            }
            writer.flush();
            writer.close();
            detailFileName.setText(file.getName());
            detailLastSaved.setText(convertTime(f.lastModified()));
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        isModified = isNew = false;
    }
    
    private String convertTime(long time) {
        Date date = new Date(time);
        Format format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return format.format(date);
    }
    
    private void closeOperation() {
        Stage stage = (Stage) details.getScene().getWindow();
        Alert rusure = new Alert(AlertType.CONFIRMATION);
        rusure.setContentText("Do you want to save the changes?");
            
        ButtonType btnSave = new ButtonType("Save");
        ButtonType btnNoSave = new ButtonType("Dont Save");
        ButtonType btnCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
            
        rusure.getButtonTypes().setAll(btnSave, btnNoSave, btnCancel);
            
        Optional<ButtonType> opt = rusure.showAndWait();
        if(opt.get() == btnSave &&  isNew) {
            FileChooser fc = createFileChooser("save");
            File savedFile = fc.showSaveDialog(stage);
            saveTextToFile(savedFile);
        }
        else if (opt.get() == btnSave) saveTextToFile(selectedFile);
        else if(opt.get() == btnNoSave && isModified) rusure.close();
        else stage.close();
    }
    @FXML
    private void pdfExport(ActionEvent event) throws Exception{
        
        taEdit.setWrapText(false);
        
        Stage stage = (Stage) details.getScene().getWindow();
        
        FileChooser fc = new FileChooser();
        
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Document", "*.pdf"));
        fc.setTitle("Save to PDF");
        fc.setInitialFileName("untitled");
        
        File file = fc.showSaveDialog(stage);
        
        if (file != null){
            
            String str = file.getAbsolutePath();
            
            try {
                FileOutputStream fos = new FileOutputStream(str);
                BufferedOutputStream bos = new BufferedOutputStream(fos);
                
                PDF pdf = new PDF(bos);
                Page page = new Page(pdf, A4.PORTRAIT);
                
                Font font = new Font(pdf, CoreFont.HELVETICA);
                
                TextBox textbox = new TextBox(font);
                
                textbox.setLocation(50f, 50f);
                
                //Set sizing for an A4 page, cannot be changed by user saddly :)
                textbox.setWidth(500f);
                
                textbox.setNoBorders();
                
                textbox.setText(taEdit.getText());
                textbox.drawOn(page);
                
                pdf.complete();
                fos.flush();
                
            } catch (FileNotFoundException ex) {
                Logger.getLogger(HmiFXMLController.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
        
    }
    
    @FXML
    private void aboutWindow(ActionEvent event){
        
        Window stage = details.getScene().getWindow();
        
        Pane secondaryLayout = new Pane();
        
        Label secondLabel = new Label("Οικοδομήθηκε απο:"
                + "\nΒαλέριο Κιόσε"
                + "\nΔημήτρη Ρούμπο");
	secondaryLayout.getChildren().add(secondLabel);

        Image image1 = new Image(getClass().getResourceAsStream("/Images/blue_frog_big.png"));
        ImageView imageView = new ImageView(image1);
        imageView.setX(200);
        imageView.setY(70);
        imageView.setFitHeight(300); 
        imageView.setFitWidth(300);
        secondaryLayout.getChildren().add(imageView);       
        
	Scene secondScene = new Scene(secondaryLayout, 500, 300);

	// New window (Stage)
	Stage newWindow = new Stage();
        newWindow.setResizable(false);
	newWindow.setTitle("About");
	newWindow.setScene(secondScene);

	// Set position of second window, related to primary window.
	newWindow.setX(stage.getX() + 200);
	newWindow.setY(stage.getY() + 100);
        
        Image icon = new Image(getClass().getResourceAsStream("/Images/orange_frog.png"));
        newWindow.getIcons().add(icon);
        
	newWindow.show();        
    }
    
    @FXML 
    private void selectBeggin(ActionEvent event){
        taEdit.requestFocus(); 
        caretPossitionA = taEdit.getCaretPosition();    
    }

    @FXML 
    private void selectEnd(ActionEvent event){
        taEdit.requestFocus(); 
        taEdit.selectRange(caretPossitionA, taEdit.getCaretPosition());    
    }    
}
