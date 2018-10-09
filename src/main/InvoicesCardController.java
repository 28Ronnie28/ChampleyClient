package main;

import javafx.beans.InvalidationListener;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import models.DataFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class InvoicesCardController {

    @FXML Label invoiceNumberLbl;
    @FXML Button viewBtn;
    @FXML Button removeBtn;
    @FXML Button sendBtn;
    @FXML Button exportBtn;
    @FXML VBox iNumberVBox;
    @FXML HBox buttonHBox;
    private DataFile dataFile;

    public void initData(DataFile dataFile, Double width){
        this.dataFile = dataFile;
        invoiceNumberLbl.setText("Invoice Number: " + dataFile.getFileName());
        iNumberVBox.setPrefWidth(width);
        buttonHBox.setPrefWidth(width);
        viewBtn.setTooltip(new Tooltip("View"));
        removeBtn.setTooltip(new Tooltip("Remove"));
        sendBtn.setTooltip(new Tooltip("Email"));
        exportBtn.setTooltip(new Tooltip("Export"));
    }

    public void openButtonClick(){
        DataFile dataFile2 = new DataFile(dataFile.getFileType(), dataFile.getFileName() + dataFile.getFileExtension(), dataFile.getFileExtension(), dataFile.getFileLength());
        dataFile2.setValue(2);
        ConnectionHandler.FileDownloader fileDownloader = Main.connectionHandler.new FileDownloader(dataFile2);
        fileDownloader.start();
        dataFile2.setFileDownloader(fileDownloader);
        Main.connectionHandler.user.update();
        fileDownloader.done.addListener((InvalidationListener) e -> {
            File openFile;
            if ((openFile = new File(Main.LOCAL_CACHE.getAbsolutePath() + "/" + dataFile2.getFileType() + "/" + dataFile2.getFileName())).exists() && openFile.length() == dataFile2.getFileLength()) {
                try {
                    java.awt.Desktop.getDesktop().open(openFile);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    public void removeButtonClick(){
        boolean rc = UserNotification.confirmationDialog(Main.stage, "Delete Invoice", "Are you sure you want to delete Invoice: " + dataFile.getFileName() + "?");
        if (rc) {
            Main.connectionHandler.outputQueue.add("ri:" + dataFile.getFileName());
        }
    }

    public void sendButtonClick(){//TODO test
        new EmailDialog(Main.stage, dataFile.getFileType(), dataFile.getFileName()).showDialog();
    }

    public void exportButtonClick() {
        DirectoryChooser dc = new DirectoryChooser();
        dc.setTitle("Choose Directory to export to..");
        File f = dc.showDialog(Main.stage);
        if (f != null) {
            DataFile dataFile2 = new DataFile(dataFile.getFileType(), dataFile.getFileName() + dataFile.getFileExtension(), dataFile.getFileExtension(), dataFile.getFileLength());
            dataFile2.setValue(2);
            ConnectionHandler.FileDownloader fileDownloader = Main.connectionHandler.new FileDownloader(dataFile2);
            fileDownloader.start();
            dataFile2.setFileDownloader(fileDownloader);
            Main.connectionHandler.user.update();
            fileDownloader.done.addListener((InvalidationListener) e -> {
                File source = new File(Main.LOCAL_CACHE.getAbsolutePath() + "/" + dataFile2.getFileType() + "/" + dataFile2.getFileName());
                File target = new File(f.getAbsolutePath() + "/" + dataFile2.getFileName());
                target.mkdirs();
                try {
                    Files.copy(source.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    new CustomDialog(Main.stage, "Export Successful", "You successfully exported your " + dataFile2.getFileType()).showDialog();
                } catch (IOException ex) {
                    ex.printStackTrace();
                    new CustomDialog(Main.stage, "Export Failed", "The export of your " + dataFile2.getFileType() + " failed.").showDialog();
                }
            });
        }
    }
}
