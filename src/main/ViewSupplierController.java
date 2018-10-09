package main;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.Button;

import javafx.scene.control.ListView;
import models.Product;
import models.Supplier;

import java.io.IOException;

public class ViewSupplierController {

    @FXML private Label nameLbl;
    @FXML private Label contactLbl;
    @FXML private Hyperlink emailHL;
    @FXML private Label addressLbl;
    @FXML private ListView productsListView;
    @FXML private Button backBtn;
    private Supplier supplier;

    public void initData(Supplier supplier) {
        this.supplier = supplier;
        Main.connectionHandler.suppliers.addListener((InvalidationListener) e -> {
            populateSupplier();
        });
        populateSupplier();
        Main.connectionHandler.products.addListener((InvalidationListener) e -> {
            populateProducts();
        });
        populateProducts();
    }

    public void populateSupplier(){
        Platform.runLater(() -> {
            nameLbl.setText(supplier.getName());
            contactLbl.setText("Contact Number: " + supplier.getContactNumber());
            emailHL.setText(supplier.getEmail());
            addressLbl.setText("Address: " + supplier.getAddress());
        });
    }

    public void populateProducts(){
        Platform.runLater(() -> {
            ObservableList<Product> products = FXCollections.observableArrayList();
            for (Product p: Main.connectionHandler.products) {
                if (p.getSupplierID() == supplier.getId()) {
                    products.add(p);
                }
            }
            productsListView.getItems().clear();
            productsListView.getItems().addAll(products);
        });
    }

    public void backButtonPressed(){
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("SuppliersPane.fxml"));
        try {
            Main.setStage(loader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void emailHLButtonClick(){//TODO test
        new EmailDialog(Main.stage, "", "").showDialog();
    }
}
