package gui;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import gui.util.Alerts;
import gui.util.Utils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Sales;
import model.services.SalesService;
import model.services.SellerService;

public class SalesListController implements Initializable {

    private SalesService service;

    @FXML
    private TableView<Sales> tableViewSales;

    @FXML
    private TableColumn<Sales, Integer> tableColumnId;

    @FXML
    private TableColumn<Sales, Date> tableColumnDate;

    @FXML
    private TableColumn<Sales, Double> tableColumnAmount;

    @FXML
    private TableColumn<Sales, String> tableColumnStatus;

    @FXML
    private TableColumn<Sales, String> tableColumnSeller;

    @FXML
    private Button btNew;

    private ObservableList<Sales> obsList;

    @FXML
    public void onBtNewAction(ActionEvent event) {
        Stage parentStage = Utils.currentStage(event);
        Sales obj = new Sales();
        createDialogForm(obj, "/gui/SalesForm.fxml", parentStage);
    }
    
    private void createDialogForm(Sales obj, String absoluteName, Stage parentStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
            Pane pane = loader.load();

            SalesFormController controller = loader.getController();
            controller.setSales(obj);
            controller.setServices(new SalesService(), new SellerService());
            controller.loadAssociatedObjects();
            controller.updateFormData();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Enter Sale data");
            dialogStage.setScene(new Scene(pane));
            dialogStage.setResizable(false);
            dialogStage.initOwner(parentStage);
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.showAndWait();
            
            updateTableView();
        } catch (IOException e) {
            Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(), AlertType.ERROR);
        }
    }

    public void setSalesService(SalesService service) {
        this.service = service;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initializeNodes();
    }

    private void initializeNodes() {
        tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
        tableColumnDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        Utils.formatTableColumnDate(tableColumnDate, "dd/MM/yyyy");
        tableColumnAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        Utils.formatTableColumnDouble(tableColumnAmount, 2);
        tableColumnStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        
        // Pega o nome do Vendedor da propriedade aninhada
        tableColumnSeller.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(
            param.getValue().getSeller() != null ? param.getValue().getSeller().getName() : ""
        ));
    }

    public void updateTableView() {
        if (service == null) {
            throw new IllegalStateException("Service estava nulo");
        }
        List<Sales> list = service.findAll();
        obsList = FXCollections.observableArrayList(list);
        tableViewSales.setItems(obsList);
    }
}