package main;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import models.Supplier;

import java.io.IOException;

public class SuppliersCardController {

    @FXML Label supplierNameLbl;
    @FXML Label addressLbl;
    @FXML Label contactDetailsLbl;
    @FXML Label emailLbl;
    @FXML Button viewBtn;
    @FXML Button editBtn;
    @FXML Button removeBtn;
    @FXML VBox nameVBox;
    @FXML VBox contactVBox;
    @FXML HBox buttonHBox;
    private Supplier supplier;

    public void initData(Supplier supplier, Double width){
        this.supplier = supplier;
        supplierNameLbl.setText("Name: " + supplier.getName());
        addressLbl.setText(supplier.getAddress());
        contactDetailsLbl.setText("Contact Number: " + supplier.getContactNumber());
        emailLbl.setText("Email: " + supplier.getEmail());
        nameVBox.setPrefWidth(width);
        contactVBox.setPrefWidth(width);
        buttonHBox.setPrefWidth(width);
        viewBtn.setTooltip(new Tooltip("View"));
        editBtn.setTooltip(new Tooltip("Edit"));;
        removeBtn.setTooltip(new Tooltip("Remove"));;
    }

    public void viewButtonClick(){
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("ViewSupplierPane.fxml"));
        try {
            Main.setStage(loader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
        ViewSupplierController vsc = loader.getController();
        vsc.initData(supplier);
    }

    public void editButtonClick(){
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("AddSupplierPane.fxml"));
        try {
            Main.setStage(loader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
        AddSupplierController asc = loader.getController();
        asc.initData(supplier);
    }

    public void removeButtonClick(){
        if (UserNotification.confirmationDialog(Main.stage, "Are you sure you want to remove " + supplier.getName() + "?", "This will delete all associated products of the supplier as well.")) {
            Main.connectionHandler.outputQueue.add("rs:" + supplier.getId());
        }
    }
}
