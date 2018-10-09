package main;

import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import models.Product;
import models.Supplier;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AddProductController implements Initializable {

    @FXML ComboBox supplierCmb;
    @FXML ComboBox categoryCmb;
    @FXML TextField descriptionTxf;
    @FXML TextField priceTxf;
    @FXML Button addBtn;
    @FXML Button backBtn;
    @FXML Button addCategoryBtn;
    @FXML Button removeCategoryBtn;
    private Product product;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        supplierCmb.getItems().add("Select Supplier");
        supplierCmb.getItems().addAll(Main.connectionHandler.suppliers);
        supplierCmb.getSelectionModel().select(0);
        categoryCmb.getItems().add("Select Category");
        categoryCmb.getSelectionModel().select(0);
        categoryCmb.getItems().addAll(Main.connectionHandler.categories);
        Main.connectionHandler.categories.addListener((InvalidationListener) e -> {
            Platform.runLater(() -> {
                categoryCmb.getItems().clear();
                categoryCmb.getItems().add("Select Category");
                categoryCmb.getSelectionModel().select(0);
                categoryCmb.getItems().addAll(Main.connectionHandler.categories);
            });
        });
        Main.connectionHandler.suppliers.addListener((InvalidationListener) e -> {
            Platform.runLater(() -> {
                supplierCmb.getItems().clear();
                supplierCmb.getItems().add("Select Supplier");
                supplierCmb.getItems().addAll(Main.connectionHandler.suppliers);
                supplierCmb.getSelectionModel().select(0);
            });
        });
    }

    public void initData(Product product) {
        this.product = product;
        supplierCmb.getSelectionModel().select(product.getSupplier());
        categoryCmb.getSelectionModel().select(product.getCategory());
        descriptionTxf.setText(product.getDescription());
        priceTxf.setText(Double.toString(product.getPrice()));
    }

    public void addButtonClick(){
        if (!supplierCmb.getSelectionModel().getSelectedItem().toString().matches("Select Supplier")) {
            if (!categoryCmb.getSelectionModel().getSelectedItem().toString().matches("Select Category")) {
                if (!descriptionTxf.getText().matches("")) {
                    if (!priceTxf.getText().matches("")) {
                        try {
                            if (product == null) {
                                Main.connectionHandler.outputQueue.add(new Product(-1, descriptionTxf.getText(), categoryCmb.getSelectionModel().getSelectedItem().toString(), supplierCmb.getSelectionModel().getSelectedItem().toString(), -1, Double.parseDouble(priceTxf.getText())));
                            } else {
                                Main.connectionHandler.outputQueue.add(new Product(product.getProductID(), descriptionTxf.getText(), categoryCmb.getSelectionModel().getSelectedItem().toString(), supplierCmb.getSelectionModel().getSelectedItem().toString(), -1, Double.parseDouble(priceTxf.getText())));
                            }
                            FXMLLoader loader = new FXMLLoader();
                            loader.setLocation(getClass().getResource("ProductsPane.fxml"));
                            try {
                                Main.setStage(loader.load());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } catch (NumberFormatException ex) {
                            new CustomDialog(Main.stage,"NumberFormatException", "Make Sure The Price Is In Number Format Only.", new JFXButton("Ok")).showDialog();
                        }
                    } else {
                        new CustomDialog(Main.stage,"Price not entered", "Enter price before adding product.", new JFXButton("Ok")).showDialog();
                    }
                } else {
                    new CustomDialog(Main.stage,"Description not entered", "Enter description before adding product.", new JFXButton("Ok")).showDialog();
                }
            } else {
                new CustomDialog(Main.stage,"Category not selected", "Select category before adding product.", new JFXButton("Ok")).showDialog();
            }
        } else {
            new CustomDialog(Main.stage,"Supplier not selected", "Select supplier before adding product.", new JFXButton("Ok")).showDialog();
        }
    }

    public void addCategoryButtonClick(){
        Main.connectionHandler.outputQueue.add("ac:" + UserNotification.getText("Category", "Category Name: "));
    }

    public void removeCategoryButtonClick(){
        Main.connectionHandler.outputQueue.add("rc:" + categoryCmb.getSelectionModel().getSelectedItem().toString().substring(0, categoryCmb.getSelectionModel().getSelectedItem().toString().indexOf(" - ")));
    }

    public void backButtonClick(){
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("ProductsPane.fxml"));
        try {
            Main.setStage(loader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
