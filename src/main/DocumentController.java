package main;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import models.DataFile;
import models.UploadFile;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ResourceBundle;

public class DocumentController implements Initializable {

    @FXML private TextField searchTxf;
    @FXML private Button searchBtn;
    @FXML private ScrollPane documentsScrollPane;
    @FXML private VBox documentsList;
    @FXML private Button addBtn;
    @FXML private Button backBtn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Main.connectionHandler.documents.addListener((InvalidationListener) e -> {
            populateDocuments();
        });
        populateDocuments();
    }

    private void populateDocuments(){
        ObservableList<HBox> documentCards = FXCollections.observableArrayList();
        for (DataFile df: Main.connectionHandler.documents) {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("DocumentCard.fxml"));
            HBox root = null;
            try {
                root = loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            root.setPrefWidth(documentsScrollPane.getPrefWidth() - 10);
            DocumentCardController dcc = loader.getController();
            dcc.initData(df, documentsScrollPane.getPrefWidth() / 3);
            documentCards.add(root);
        }
        Platform.runLater(() -> {
            documentsList.getChildren().clear();
            documentsList.getChildren().addAll(documentCards);
        });
    }

    public void searchButtonClick(){
        ObservableList<DataFile> displayList = FXCollections.observableArrayList();
        if (!searchTxf.getText().matches("")) {
            for (DataFile df: Main.connectionHandler.documents) {
                if (df.getFileName().contains(searchTxf.getText())) {
                    displayList.add(df);
                }
            }
        } else {
            displayList.addAll(Main.connectionHandler.documents);
        }
        ObservableList<HBox> documentCards = FXCollections.observableArrayList();
        for (DataFile df: displayList) {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("DocumentCard.fxml"));
            HBox root = null;
            try {
                root = loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            root.setPrefWidth(documentsScrollPane.getPrefWidth() - 10);
            DocumentCardController dcc = loader.getController();
            dcc.initData(df, documentsScrollPane.getPrefWidth() / 3);
            documentCards.add(root);
        }
        documentsList.getChildren().clear();
        documentsList.getChildren().addAll(documentCards);
    }

    public void addButtonClick(){
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select file to upload..");
            File file = fileChooser.showOpenDialog(Main.stage);
            if (file != null) {
                UploadFile uploadFile = new UploadFile(file.getName(), "Documents", Files.readAllBytes(file.toPath()));
                UploadDialog uploadDialog = new UploadDialog(Main.stage);
                Main.uploading.addListener(al -> {
                    if (!Main.uploading.get()) {
                        uploadDialog.closeAnimation();
                    }
                });
                Main.uploading.set(true);
                new Thread(() -> {
                    Main.connectionHandler.sendData(uploadFile);
                    Main.uploading.set(false);
                }).start();
                uploadDialog.showDialog();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void backButtonClick(){
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("HomePane.fxml"));
        try {
            Main.setStage(loader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
