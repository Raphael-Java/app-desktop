package gui;

import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import gui.util.Alerts;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import model.entities.Sales;
import model.entities.Seller;
import model.services.SalesService;
import model.services.SellerService;

public class SalesFormController implements Initializable {

    private Sales entity;
    private SalesService service;
    private SellerService sellerService;

    @FXML private TextField txtId;
    @FXML private DatePicker dpDate;
    @FXML private TextField txtAmount;
    @FXML private TextField txtStatus;
    @FXML private ComboBox<Seller> comboBoxSeller;
    @FXML private Label labelErrorDate;
    @FXML private Label labelErrorAmount;
    @FXML private Button btSave;
    @FXML private Button btCancel;

    private ObservableList<Seller> obsListSeller;

    public void setSales(Sales entity) { this.entity = entity; }
    public void setServices(SalesService service, SellerService sellerService) {
        this.service = service;
        this.sellerService = sellerService;
    }

    @FXML
    public void onBtSaveAction(ActionEvent event) {
        try {
            entity = getFormData();
            service.saveOrUpdate(entity);
            Utils.currentStage(event).close();
        } catch (Exception e) {
            Alerts.showAlert("Error saving object", null, e.getMessage(), AlertType.ERROR);
        }
    }

    @FXML
    public void onBtCancelAction(ActionEvent event) {
        Utils.currentStage(event).close();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initializeComboBoxSeller();
    }

    public void updateFormData() {
        if (entity == null) return;
        txtId.setText(String.valueOf(entity.getId()));
        if (entity.getDate() != null) {
            dpDate.setValue(LocalDate.ofInstant(entity.getDate().toInstant(), ZoneId.systemDefault()));
        }
        txtAmount.setText(String.format("%.2f", entity.getAmount()));
        txtStatus.setText(entity.getStatus());
        if (entity.getSeller() == null) {
            comboBoxSeller.getSelectionModel().selectFirst();
        } else {
            comboBoxSeller.setValue(entity.getSeller());
        }
    }

    public void loadAssociatedObjects() {
        if (sellerService == null) throw new IllegalStateException("SellerService está nulo");
        List<Seller> list = sellerService.findAll();
        obsListSeller = FXCollections.observableArrayList(list);
        comboBoxSeller.setItems(obsListSeller);
    }

    private Sales getFormData() {
        Sales obj = new Sales();
        obj.setId(Utils.tryParseToInt(txtId.getText()));
        
        Instant instant = Instant.from(dpDate.getValue().atStartOfDay(ZoneId.systemDefault()));
        obj.setDate(Date.from(instant));
        obj.setAmount(Utils.tryParseToDouble(txtAmount.getText()));
        obj.setStatus(txtStatus.getText());
        obj.setSeller(comboBoxSeller.getValue());
        return obj;
    }

    private void initializeComboBoxSeller() {
        Callback<ListView<Seller>, ListCell<Seller>> factory = lv -> new ListCell<Seller>() {
            @Override
            protected void updateItem(Seller item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : item.getName());
            }
        };
        comboBoxSeller.setCellFactory(factory);
        comboBoxSeller.setButtonCell(factory.call(null));
    }
}