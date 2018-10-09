package main;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.svg.SVGGlyph;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import models.Product;
import models.QuotationProduct;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.ResourceBundle;

public class ProductsController implements Initializable{

    @FXML private TextField searchTxf;
    @FXML private Button searchBtn;
    @FXML private ListView productsListView;
    @FXML private Button addBtn;
    @FXML private Button removeBtn;
    @FXML private Button editBtn;
    @FXML private Button backBtn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Main.connectionHandler.products.addListener((InvalidationListener) e -> {
            populateProducts();
        });
        populateProducts();
    }

    public void populateProducts(){
        Platform.runLater(() -> {
            productsListView.setPlaceholder(new Label("No products available"));
            productsListView.setItems(Main.connectionHandler.products);
        });
    }

    public void searchButtonClick(){
        ObservableList displayList = FXCollections.observableArrayList();
        if (!searchTxf.getText().matches("")) {
            for (Product p: Main.connectionHandler.products) {
                if (p.getDescription().contains(searchTxf.getText())||p.getSupplier().contains(searchTxf.getText())||p.getCategory().contains(searchTxf.getText())) {
                    displayList.add(p);
                }
            }
        } else {
            displayList.addAll(Main.connectionHandler.products);
        }
        productsListView.setItems(displayList);
    }

    public void addButtonClick(){
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("AddProductPane.fxml"));
        try {
            Main.setStage(loader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removeButtonClick(){
        if (productsListView.getSelectionModel().getSelectedItem() != null) {
            Product p = (Product) productsListView.getSelectionModel().getSelectedItem();
            Main.connectionHandler.outputQueue.add("rp:" + p.getProductID());
        } else {
            new CustomDialog(Main.stage,"Product not selected.", "Select product before removing.", new JFXButton("Ok")).showDialog();
        }
    }

    public void editButtonClick(){
        if (productsListView.getSelectionModel().getSelectedItem() != null) {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("AddProductPane.fxml"));
            try {
                Main.setStage(loader.load());
            } catch (IOException e) {
                e.printStackTrace();
            }
            AddProductController apc = loader.getController();
            apc.initData((Product) productsListView.getSelectionModel().getSelectedItem());
        } else {
            new CustomDialog(Main.stage,"Product not selected.", "Select product before editing.", new JFXButton("Ok")).showDialog();
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
