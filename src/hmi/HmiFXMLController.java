package hmi;

import com.pdfjet.A4;
import com.pdfjet.CoreFont;
import com.pdfjet.Font;
import com.pdfjet.PDF;
import com.pdfjet.Page;
import com.pdfjet.TextBox;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
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
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.stage.Window;

public class HmiFXMLController implements Initializable {
    
    private File selectedFile = new File("New File");
    private boolean isNew = true;
    private boolean isModified = false;
    private int wcount = 0;
    private int lcount = 0;
    private int caretPossitionA;
    private int fontSize = 18;
    private String copiedText = "";
    
    @FXML
    private HBox details;
    @FXML
    private SplitPane fileName;
    @FXML
    private SplitPane lastSaved;
    @FXML
    private MenuItem open;
    @FXML
    private MenuItem menuNew;
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
    private MenuItem undo;
    @FXML
    private MenuItem redo;
    @FXML
    private MenuItem copy;
    @FXML
    private MenuItem cut;
    @FXML
    private MenuItem paste;
    @FXML
    private MenuItem toPDF;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        taEdit.setWrapText(true);
        taEdit.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> ov, String t, String t1) {
                if (!isModified) {
                    isModified = true;
                    detailFileName.setText(detailFileName.getText() + "*");
                }
                if (taEdit.getText().isEmpty())
                    wcount = lcount = 0;
                detailWords.setText(String.valueOf(taEdit.getText().split("\\s+").length));
                detailLines.setText(String.valueOf(taEdit.getText().split("\n").length));
                if (taEdit.getText().isEmpty()) {
                    wcount = lcount = 0;
                    detailWords.setText("0");
                    detailLines.setText("0");
                }
            }        
        });
    
        fontSlider.setValue(fontSize);
        fontSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            taEdit.setStyle("-fx-font-size: " + newValue.intValue() + "px");
        });
        
        ContextMenu cm = new ContextMenu();
        
        MenuItem startSelect = new MenuItem("Start Selection");
        startSelect.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                taEdit.requestFocus(); 
                caretPossitionA = taEdit.getCaretPosition();    
            }
        
        });
        
        MenuItem endSelect = new MenuItem("End Selection");
        endSelect.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                taEdit.requestFocus(); 
                taEdit.selectRange(caretPossitionA, taEdit.getCaretPosition());
            }
            
        });
        
        MenuItem allSelect = new MenuItem("Select All");
        allSelect.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                taEdit.selectAll();
            }
        
        });
        
        MenuItem cmUndo = new MenuItem("Undo");
        cmUndo.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                taEdit.undo();
            }
        
        });
        
        MenuItem cmRedo = new MenuItem("Redo");
        cmRedo.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                taEdit.redo();
            }
        
        });
        
        MenuItem cmCut = new MenuItem("Cut");
        cmCut.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                cut();
            }
        
        });
       
        MenuItem cmCopy = new MenuItem("Copy");
        cmCopy.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                copy();
            }
        
        });
        
        MenuItem cmPaste = new MenuItem("Paste");
        cmPaste.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                paste();
            }
        
        });
        
        cm.getItems().add(cmUndo);
        cm.getItems().add(cmRedo);
        cm.getItems().add(new SeparatorMenuItem());
        cm.getItems().add(cmCut);
        cm.getItems().add(cmCopy);
        cm.getItems().add(cmPaste);
        cm.getItems().add(new SeparatorMenuItem());
        cm.getItems().add(startSelect);
        cm.getItems().add(endSelect);
        cm.getItems().add(allSelect);
        
        taEdit.setContextMenu(cm);
        
        // Accelerators
        open.setAccelerator(KeyCombination.keyCombination("Ctrl+O"));
        menuNew.setAccelerator(KeyCombination.keyCombination("Ctrl+N"));
        save.setAccelerator(KeyCombination.keyCombination("Ctrl+S"));
        saveAs.setAccelerator(KeyCombination.keyCombination("Ctrl+Shift+S"));
        close.setAccelerator(KeyCombination.keyCombination("Ctrl+W"));
        undo.setAccelerator(KeyCombination.keyCombination("Ctrl+Z"));
        redo.setAccelerator(KeyCombination.keyCombination("Ctrl+Y"));
        copy.setAccelerator(KeyCombination.keyCombination("Ctrl+C"));
        cut.setAccelerator(KeyCombination.keyCombination("Ctrl+X"));
        paste.setAccelerator(KeyCombination.keyCombination("Ctrl+P"));
        toPDF.setAccelerator(KeyCombination.keyCombination("Ctrl+Shift+P"));
        
        taEdit.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent t) {
                if (t.isControlDown() && t.getCode() == KeyCode.COMMA) {
                    fontSize++;
                    fontSlider.setValue(fontSize);
                }
                if (t.isControlDown() && t.getCode() == KeyCode.PERIOD) {
                    fontSize--;
                    fontSlider.setValue(fontSize);
                }
            }
            
        });
    }
    
    @FXML
    private void openFile(ActionEvent event) throws FileNotFoundException, IOException {
        isNew = false;
        if (isModified) closeOperation("");
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
            detailFileName.setText(selectedFile.getName());
            detailLastSaved.setText(convertTime(selectedFile.lastModified()));
            detailWords.setText(String.valueOf(wcount));
            detailLines.setText(String.valueOf(lcount));
        } catch(Exception e) {
            System.out.println("The User didnt selected any file : " + e.toString());
        }
    }
    
    @FXML
    private void newFile(ActionEvent event) {
        if (isModified) closeOperation("");
        isNew = true;
        isModified = false;
        if (!detailFileName.getText().equals("New File"))
            selectedFile = new File(System.getProperty("user.home") + System.lineSeparator() + "New File");
        detailFileName.setText(selectedFile.getName());
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
        if(isModified) closeOperation("exit");
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
        } catch (IOException e) {
            System.out.println(e.toString());
        }
        isModified = isNew = false;
    }
    
    private String convertTime(long time) {
        Date date = new Date(time);
        Format format = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        return format.format(date);
    }
    
    private void closeOperation(String msg) {
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
        else if (opt.get() == btnNoSave && isModified) {
            rusure.close();
            selectedFile = null;
        }
        if (selectedFile == null && msg.equals("exit")) stage.close();
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
        
        Pane aboutPane = new Pane();
        
        TextFlow flow = new TextFlow();

        Text text1=new Text("Δημιουργήθηκε από τους:");
        text1.setStyle("-fx-font-weight: bold");
        text1.setTranslateY(100);
        text1.setTranslateX(30);
        
        Text text2=new Text("\n\nΚιόσε Βαλέριο \nΡούμπος Δημήτρης \nΚάτω από την επιμέλεια του Μαρτσέλο");
        text2.setTranslateY(100);//pano kato
        text2.setTranslateX(40);//mesa ekso
         
        Label aboutLabel = new Label("JavaFX 17 - CSS - PDFjet");
        aboutLabel.setTranslateY(260);
        aboutLabel.setTranslateX(30);
	aboutPane.getChildren().add(aboutLabel);
        
        flow.getChildren().addAll(text1, text2);   
        aboutPane.getChildren().add(flow);
       
        Image image1 = new Image(getClass().getResourceAsStream("/Images/notepad--.png"));
        ImageView imageView = new ImageView(image1);
        aboutPane.getChildren().add(imageView);       
        
	Scene secondScene = new Scene(aboutPane, 500, 300);

	//New window (Stage)
	Stage newWindow = new Stage();
        newWindow.setResizable(false);
	newWindow.setTitle("About Notepad--");
	newWindow.setScene(secondScene);

	//Set position of second window, related to primary window.
	newWindow.setX(stage.getX() + 200);
	newWindow.setY(stage.getY() + 100);
        
        Image icon = new Image(getClass().getResourceAsStream("/Images/orange_frog.png"));
        newWindow.getIcons().add(icon);
        
	newWindow.show();        
    }

    @FXML
    private void undoEdit(ActionEvent event) {
        taEdit.undo();
    }

    @FXML
    private void redoEdit(ActionEvent event) {
        taEdit.redo();
    }

    @FXML
    private void copyEdit(ActionEvent event) {
        copy();
    }

    @FXML
    private void cutEdit(ActionEvent event) {
        cut();
    }

    @FXML
    private void pasteEdit(ActionEvent event) {
        paste();
    }
    
    private void copy() {
        copiedText = "";
        copiedText = taEdit.getSelectedText();
    }
    
    private void cut() {
        copiedText = "";
        copiedText = taEdit.getSelectedText();
        taEdit.setText(taEdit.getText().replace(taEdit.getSelectedText(),"")); // taEdit.replaceSelection("");
    }
    
    private void paste() {
        taEdit.insertText(taEdit.getCaretPosition(), copiedText);
    }
    
    @FXML
    private void lightTheme(ActionEvent event){
        HMI.stage.getScene().getStylesheets().clear();
        HMI.stage.getScene().setUserAgentStylesheet(null);
        HMI.stage.getScene().getStylesheets().add(getClass().getResource("/Styles/LightTheme.css").toExternalForm());
    }
    @FXML
    private void darkTheme(ActionEvent event){
        HMI.stage.getScene().getStylesheets().clear();
        HMI.stage.getScene().setUserAgentStylesheet(null);
        HMI.stage.getScene().getStylesheets().add(getClass().getResource("/Styles/DarkTheme.css").toExternalForm());
    }    
}
