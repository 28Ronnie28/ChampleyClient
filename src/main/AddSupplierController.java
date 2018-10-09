package main;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;

import models.Supplier;

import java.io.IOException;

public class AddSupplierController {

    @FXML TextField nameTxf;
    @FXML TextField contactNumberTxf;
    @FXML TextField emailTxf;
    @FXML TextField addressTxf;
    @FXML Button addBtn;
    @FXML Button backBtn;
    private Supplier supplier;

    public void initData(Supplier supplier) {
        this.supplier = supplier;
        nameTxf.setText(supplier.getName());
        contactNumberTxf.setText(supplier.getContactNumber());
        emailTxf.setText(supplier.getEmail());
        addressTxf.setText(supplier.getAddress());
    }

    public void addButtonClick() {
        if (!nameTxf.getText().matches("")) {
            if (!contactNumberTxf.getText().matches("")) {
                if (!emailTxf.getText().matches("")) {
                    if (!addressTxf.getText().matches("")) {
                        if (supplier == null) {
                            Main.connectionHandler.outputQueue.add(new Supplier(-1, nameTxf.getText(), contactNumberTxf.getText(), emailTxf.getText(), addressTxf.getText()));
                        } else {
                            Main.connectionHandler.outputQueue.add(new Supplier(supplier.getId(), nameTxf.getText(), contactNumberTxf.getText(), emailTxf.getText(), addressTxf.getText()));
                        }
                        FXMLLoader loader = new FXMLLoader();
                        loader.setLocation(getClass().getResource("SuppliersPane.fxml"));
                        try {
                            Main.setStage(loader.load());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        new CustomDialog(Main.stage,"Address not entered", "Enter Address before adding product.", new JFXButton("Ok")).showDialog();
                    }
                } else {
                    new CustomDialog(Main.stage,"Email Address not entered", "Enter Email Address before adding product.", new JFXButton("Ok")).showDialog();
                }
            } else {
                new CustomDialog(Main.stage,"Contact Number not entered", "Enter Contact Number before adding product.", new JFXButton("Ok")).showDialog();
            }
        } else {
            new CustomDialog(Main.stage,"Supplier Name not entered", "Enter Supplier Name before adding product.", new JFXButton("Ok")).showDialog();
        }
    }

    public void backButtonClick(){
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("SuppliersPane.fxml"));
        try {
            Main.setStage(loader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
