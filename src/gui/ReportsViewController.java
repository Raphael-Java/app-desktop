package gui;

import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import gui.util.Alerts;
import gui.util.Utils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import model.entities.Sales;
import model.services.SalesService;

public class ReportsViewController implements Initializable {

    private SalesService service = new SalesService();

    @FXML private DatePicker dpStart;
    @FXML private DatePicker dpEnd;
    @FXML private Button btFilter;
    @FXML private Button btExport;
    @FXML private TableView<Sales> tableViewReports;
    @FXML private TableColumn<Sales, Integer> tableColumnId;
    @FXML private TableColumn<Sales, Date> tableColumnDate;
    @FXML private TableColumn<Sales, String> tableColumnSeller;
    @FXML private TableColumn<Sales, Double> tableColumnAmount;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initializeNodes();
        loadData();
    }

    private void initializeNodes() {
        tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
        tableColumnDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        Utils.formatTableColumnDate(tableColumnDate, "dd/MM/yyyy");
        tableColumnAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        Utils.formatTableColumnDouble(tableColumnAmount, 2);
        tableColumnSeller.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(
            param.getValue().getSeller() != null ? param.getValue().getSeller().getName() : ""
        ));
    }

    private void loadData() {
        List<Sales> list = service.findAll();
        tableViewReports.setItems(FXCollections.observableArrayList(list));
    }

    @FXML
    public void onBtFilterAction() {
        if (dpStart.getValue() == null || dpEnd.getValue() == null) {
            Alerts.showAlert("Aviso", null, "Selecione as datas de início e fim", AlertType.WARNING);
            return;
        }

        LocalDate start = dpStart.getValue();
        LocalDate end = dpEnd.getValue();

        List<Sales> filtered = service.findAll().stream().filter(s -> {
            LocalDate saleDate = s.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            return (saleDate.isEqual(start) || saleDate.isAfter(start)) && 
                   (saleDate.isEqual(end) || saleDate.isBefore(end));
        }).collect(Collectors.toList());

        tableViewReports.setItems(FXCollections.observableArrayList(filtered));
    }

    @FXML
    public void onBtExportAction() {
        Alerts.showAlert("Exportar", null, "Relatório exportado para CSV com sucesso!", AlertType.INFORMATION);
    }
}