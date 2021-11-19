package hmi;

import com.pdfjet.A4;
import com.pdfjet.CoreFont;
import com.pdfjet.Font;
import com.pdfjet.PDF;
import com.pdfjet.Page;
import com.pdfjet.Paragraph;
import com.pdfjet.TextBox;
import com.pdfjet.TextColumn;
import com.pdfjet.TextLine;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import static javafx.scene.text.Font.font;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.stage.Window;

public class HmiFXMLController implements Initializable {
    
    private File selectedFile = new File("New File");
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
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        taEdit.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> ov, String t, String t1) {
                String dfn = detailFileName.getText();
                if (!"*".equals(String.valueOf(dfn.charAt(dfn.length() - 1)))) 
                    detailFileName.setText(detailFileName.getText() + "*");
                if (taEdit.getText().isEmpty())
                    wcount = lcount = 0;
                detailWords.setText(String.valueOf(taEdit.getText().split("\\s+").length));
                detailLines.setText(String.valueOf(taEdit.getText().split("\n").length));
            }
        });
    }    
    
    @FXML
    private void openFile(ActionEvent event) throws FileNotFoundException, IOException {
        Window stage = details.getScene().getWindow();
        FileChooser fc = createFileChooser("open");
        
        try {
            File sFile = fc.showOpenDialog(stage);
            selectedFile = sFile;
            BufferedReader bfr = new BufferedReader(new FileReader(selectedFile));
        
            String line;
            while ((line = bfr.readLine()) != null) {
                taEdit.appendText(line);
                taEdit.appendText("\n");
                String words[] = line.split("\\s+");
                wcount += words.length;
                lcount++;
            }
            
            detailFileName.setText(selectedFile.getName());
            detailLastSaved.setText(String.valueOf(selectedFile.lastModified()));
            detailWords.setText(String.valueOf(wcount));
            detailLines.setText(String.valueOf(lcount));
        } catch(NullPointerException e) {
            System.out.println("The User didnt selected any file : " + e.toString());
        }
    }

    @FXML
    private void saveFile(ActionEvent event) {
        if (selectedFile.getName() != null || !detailFileName.getText().equals(selectedFile.getName())) {
            
        } else {
            File sFile = new File(selectedFile.getAbsolutePath());
            Window stage = details.getScene().getWindow();
            FileChooser fc = createFileChooser("save");
            try {
                fc.showSaveDialog(stage);
            } catch (Exception e) {
                System.out.println(e.toString());
            }
            saveTextToFile(selectedFile, taEdit.getText());
        }
    }

    @FXML
    private void saveFileAs(ActionEvent event) {
        Window stage = details.getScene().getWindow();
        FileChooser fc = createFileChooser("save");
        try {
            File savedFile = fc.showSaveDialog(stage);
            saveTextToFile(savedFile, taEdit.getText());
        } catch (NullPointerException e) {
            System.out.println(e.toString());
        }
    }

    @FXML
    private void closeEditor(ActionEvent event) {
        Stage stage = (Stage) details.getScene().getWindow();
        if(!selectedFile.getName().equals(detailFileName.getText())){
            Alert rusure = new Alert(AlertType.CONFIRMATION);
            rusure.setContentText("Do you want to save all changes?");
            
            ButtonType btnSave = new ButtonType("Save");
            ButtonType btnNoSave = new ButtonType("Dont Save");
            ButtonType btnCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
            
            rusure.getButtonTypes().setAll(btnSave, btnNoSave, btnCancel);
            
            Optional<ButtonType> opt = rusure.showAndWait();
            if(opt.get() == btnSave) {
                if (selectedFile != null) {
                    saveTextToFile(selectedFile, taEdit.getText());
                } else {
                    FileChooser fc = createFileChooser("save");
                    File savedFile = fc.showSaveDialog(stage);
                    saveTextToFile(savedFile, taEdit.getText());
                }
            } 
            if(opt.get() == btnNoSave) stage.close();
        }
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
    
    private void saveTextToFile(File f, String cnt) {
        try {
            PrintWriter writer = new PrintWriter(f);
            writer.println(cnt);
            writer.close();
        } catch (IOException e) {
            System.out.println(e.toString());
        }
        detailFileName.setText(f.getName());
    }
    
    private String convertTime(long time) {
        Date date = new Date(time);
        Format format = new SimpleDateFormat("yyyy MM dd HH:mm:ss");
        return format.format(date);
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
                
                Paragraph paragraph = new Paragraph();
                paragraph.add(new TextLine(font, taEdit.getText().replaceAll("\n", System.getProperty("line.separator"))));
                //paragraph.add(new TextLine(font, taEdit.getText()));
                
                TextColumn column = new TextColumn();
                column.setLocation(50f, 50f);
                column.setSize(540f, 0f);
                // column.SetLineBetweenParagraphs(true);
                column.setLineBetweenParagraphs(false);
                column.addParagraph(paragraph);
                
                float[] point = column.drawOn(page);
                
                pdf.complete();
                fos.flush();
                
            } catch (FileNotFoundException ex) {
                Logger.getLogger(HmiFXMLController.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
        
    }
}
