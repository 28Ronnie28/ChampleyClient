package main;

import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import models.Product;
import models.Quotation;
import models.QuotationProduct;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AddQuotationContoller implements Initializable {

    @FXML TextField quoteNumberTxf;
    @FXML TextField clientNameTxf;
    @FXML TextField contactNumberTxf;
    @FXML TextField emailTxf;
    @FXML TextField searchTxf;
    @FXML Button searchBtn;
    @FXML ListView productsListView;
    @FXML ComboBox quantityCmb;
    @FXML TextField priceTxf;
    @FXML Button addToQouteBtn;
    @FXML ListView qouteListView;
    @FXML Button removeBtn;
    @FXML Label quantitiesLbl;
    @FXML Label totalPriceLbl;
    @FXML Button createBtn;
    @FXML Button backBtn;
    private ObservableList<QuotationProduct> quotationProducts;
    private int quantities;
    private Double totalPrice;
    private boolean newQ;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        quotationProducts = FXCollections.observableArrayList();
        ObservableList x = FXCollections.observableArrayList();
        x.add("Select Quantity");
        for (int i = 1; i < 501; i++) {
            x.add(i);
        }
        quantityCmb.getItems().addAll(x);
        quantityCmb.getSelectionModel().select(0);
        quantitiesLbl.setText("Total Quantities: 0");
        totalPriceLbl.setText("Total Price for Quotation: R 0");
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
                if (p.getDescription().matches(searchTxf.getText())||p.getSupplier().matches(searchTxf.getText())||p.getCategory().matches(searchTxf.getText())) {
                    displayList.add(p);
                }
            }
        } else {
            displayList.addAll(Main.connectionHandler.products);
        }
        productsListView.setItems(displayList);
    }

    public void addToQuoteButtonClick(){
        if (productsListView.getSelectionModel().getSelectedItem() != null) {
            if (!quantityCmb.getSelectionModel().getSelectedItem().toString().matches("Select Quantity")) {
                Product selectedP = (Product) productsListView.getSelectionModel().getSelectedItem();
                boolean exist = false;
                for (QuotationProduct qp : quotationProducts) {
                    if (qp.getProduct() == selectedP) {
                        quotationProducts.remove(qp);
                        quotationProducts.add(qp);
                        qp.setQuantity(qp.getQuantity() + Integer.parseInt(quantityCmb.getSelectionModel().getSelectedItem().toString()));
                        quantities = +Integer.parseInt(quantityCmb.getSelectionModel().getSelectedItem().toString());
                        totalPrice = +qp.getProduct().getPrice() * Integer.parseInt(quantityCmb.getSelectionModel().getSelectedItem().toString());
                        exist = true;
                    }
                }
                if (!exist) {
                    if (!priceTxf.getText().matches("")) {
                        try {
                            if (Double.parseDouble(priceTxf.getText()) < selectedP.getPrice()) {
                                if (UserNotification.confirmationDialog(Main.stage, "Price entered warning!", "Are you sure you want to add a product to a quote where asking price is less than buying price?")) {
                                    selectedP.setPrice(Double.parseDouble(priceTxf.getText()));
                                    quotationProducts.add(new QuotationProduct(selectedP, Integer.parseInt(quantityCmb.getSelectionModel().getSelectedItem().toString())));
                                    quantities = +Integer.parseInt(quantityCmb.getSelectionModel().getSelectedItem().toString());
                                    totalPrice = +selectedP.getPrice() * Integer.parseInt(quantityCmb.getSelectionModel().getSelectedItem().toString());
                                }
                            } else {
                                selectedP.setPrice(Double.parseDouble(priceTxf.getText()));
                                quotationProducts.add(new QuotationProduct(selectedP, Integer.parseInt(quantityCmb.getSelectionModel().getSelectedItem().toString())));
                                quantities = +Integer.parseInt(quantityCmb.getSelectionModel().getSelectedItem().toString());
                                totalPrice = +selectedP.getPrice() * Integer.parseInt(quantityCmb.getSelectionModel().getSelectedItem().toString());
                            }
                        } catch (NumberFormatException e) {
                            new CustomDialog(Main.stage, "Price wrongly entered", "Make sure price is of numeric value only.", new JFXButton("Ok")).showDialog();
                        }
                        quantitiesLbl.setText("Total Quantities: " + quantities);
                        totalPriceLbl.setText("Total Price for Quotation: R " + totalPrice);
                        qouteListView.setItems(quotationProducts);
                        quantityCmb.getSelectionModel().select(0);

                    } else {
                        new CustomDialog(Main.stage, "Price not entered", "Enter price before adding.", new JFXButton("Ok")).showDialog();
                    }
                }
            } else {
                new CustomDialog(Main.stage,"Quantity not selected", "Select the quantity of the product before adding.", new JFXButton("Ok")).showDialog();
            }
        } else {
            new CustomDialog(Main.stage,"Product not selected", "Select the product before adding.", new JFXButton("Ok")).showDialog();
        }
    }

    public void removeFromQuoteButtonClick(){
        if (qouteListView.getSelectionModel().getSelectedItem() != null) {
            QuotationProduct selectedP = (QuotationProduct) qouteListView.getSelectionModel().getSelectedItem();
            for (QuotationProduct qp : quotationProducts) {
                if (qp == selectedP) {
                    quantities = -qp.getQuantity();
                    totalPrice = -qp.getProduct().getPrice() * qp.getQuantity();
                    quotationProducts.remove(qp);
                }
                quantitiesLbl.setText("Total Quantities: " + quantities);
                totalPriceLbl.setText("Total Price for Quotation: R " + totalPrice);
            }
        }  else {
            new CustomDialog(Main.stage,"Product not selected", "Select the product you want to remove before removing.", new JFXButton("Ok")).showDialog();
        }
    }

    public void addButtonClick(){
        if (!quoteNumberTxf.getText().matches("")) {
            if (!clientNameTxf.getText().matches("")) {
                if (!contactNumberTxf.getText().matches("")) {
                    if (!emailTxf.getText().matches("")) {
                        Main.connectionHandler.outputQueue.add(new Quotation(quoteNumberTxf.getText(), clientNameTxf.getText(), contactNumberTxf.getText(), emailTxf.getText(), quotationProducts, newQ));
                        FXMLLoader loader = new FXMLLoader();
                        loader.setLocation(getClass().getResource("QuotationsPane.fxml"));
                        try {
                            Main.setStage(loader.load());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        new CustomDialog(Main.stage,"Email Address not entered", "Enter Email Address before creating quotation.", new JFXButton("Ok")).showDialog();
                    }
                } else {
                    new CustomDialog(Main.stage,"Contact Number not entered", "Enter Contact Number before creating quotation.", new JFXButton("Ok")).showDialog();
                }
            } else {
                new CustomDialog(Main.stage,"Client Name not entered", "Enter Client Name before creating quotation.", new JFXButton("Ok")).showDialog();
            }
        } else {
            new CustomDialog(Main.stage,"Quotation Number not entered", "Enter Quotation Number before creating quotation.", new JFXButton("Ok")).showDialog();
        }
    }

    public void backButtonClick(){
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("QuotationsPane.fxml"));
        try {
            Main.setStage(loader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }






}
