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

public class InvoicesController implements Initializable {

    @FXML private TextField searchTxf;
    @FXML private Button searchBtn;
    @FXML private ScrollPane invoicesScrollPane;
    @FXML private VBox invoicesList;
    @FXML private Button backBtn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Main.connectionHandler.invoices.addListener((InvalidationListener) e -> {
            populateInvoices();
        });
        populateInvoices();
    }

    private void populateInvoices(){
        ObservableList<HBox> invoicesCards = FXCollections.observableArrayList();
        for (DataFile df: Main.connectionHandler.invoices) {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("InvoicesCard.fxml"));
            HBox root = null;
            try {
                root = loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            root.setPrefWidth(invoicesScrollPane.getPrefWidth() - 10);
            InvoicesCardController icc = loader.getController();
            icc.initData(df, invoicesScrollPane.getPrefWidth() / 5);
            invoicesCards.add(root);
        }
        Platform.runLater(() -> {
            invoicesList.getChildren().clear();
            invoicesList.getChildren().addAll(invoicesCards);
        });
    }

    public void searchButtonClick(){
        ObservableList<DataFile> displayList = FXCollections.observableArrayList();
        if (!searchTxf.getText().matches("")) {
            for (DataFile df: Main.connectionHandler.invoices) {
                if (df.getFileName().contains(searchTxf.getText())) {
                    displayList.add(df);
                }
            }
        } else {
            displayList.addAll(Main.connectionHandler.invoices);
        }
        ObservableList<HBox> invoiceCards = FXCollections.observableArrayList();
        for (DataFile df: displayList) {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("InvoicesCard.fxml"));
            HBox root = null;
            try {
                root = loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            root.setPrefWidth(invoicesScrollPane.getPrefWidth() - 10);
            InvoicesCardController icc = loader.getController();
            icc.initData(df, invoicesScrollPane.getPrefWidth() / 5);
            invoiceCards.add(root);
        }
        invoicesList.getChildren().clear();
        invoicesList.getChildren().addAll(invoiceCards);
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
