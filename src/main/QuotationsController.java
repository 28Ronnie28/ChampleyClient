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
import models.DataFile;
import models.Supplier;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class QuotationsController implements Initializable{

    @FXML private TextField searchTxf;
    @FXML private Button searchBtn;
    @FXML private ScrollPane quotationsScrollPane;
    @FXML private VBox quotationsList;
    @FXML private Button addBtn;
    @FXML private Button backBtn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Main.connectionHandler.quotations.addListener((InvalidationListener) e -> {
            populateQuotations();
        });
        populateQuotations();
    }

    private void populateQuotations(){
        ObservableList<HBox> quotationsCards = FXCollections.observableArrayList();
        for (DataFile df: Main.connectionHandler.quotations) {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("QuotationsCard.fxml"));
            HBox root = null;
            try {
                root = loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            root.setPrefWidth(quotationsScrollPane.getPrefWidth() - 10);
            QuotationsCardController qcc = loader.getController();
            qcc.initData(df, quotationsScrollPane.getPrefWidth() / 2);
            quotationsCards.add(root);
        }
        Platform.runLater(() -> {
            quotationsList.getChildren().clear();
            quotationsList.getChildren().addAll(quotationsCards);
        });
    }

    public void searchButtonClick(){
        ObservableList<DataFile> displayList = FXCollections.observableArrayList();
        if (!searchTxf.getText().matches("")) {
            for (DataFile df: Main.connectionHandler.quotations) {
                if (df.getFileName().contains(searchTxf.getText())) {
                    displayList.add(df);
                }
            }
        } else {
            displayList.addAll(Main.connectionHandler.quotations);
        }
        ObservableList<HBox> quotationsCards = FXCollections.observableArrayList();
        for (DataFile df: displayList) {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("QuotationsCard.fxml"));
            HBox root = null;
            try {
                root = loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            root.setPrefWidth(quotationsScrollPane.getPrefWidth() - 10);
            QuotationsCardController qcc = loader.getController();
            qcc.initData(df, quotationsScrollPane.getPrefWidth() / 2);
            quotationsCards.add(root);
        }
        quotationsList.getChildren().clear();
        quotationsList.getChildren().addAll(quotationsCards);
    }

    public void addButtonClick(){
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("AddQuotationPane.fxml"));
        try {
            Main.setStage(loader.load());
        } catch (IOException e) {
            e.printStackTrace();
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
