package gui;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import application.Main;
import gui.listeners.DataChangeListener;
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
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Department;
import model.entities.Manager;
import model.services.DepartmentService;
import model.services.ManagerService;

public class ManagerListController implements Initializable, DataChangeListener {

    private ManagerService service;

    @FXML
    private TableView<Manager> tableViewManager;

    @FXML
    private TableColumn<Manager, Integer> tableColumnId;

    @FXML
    private TableColumn<Manager, String> tableColumnName;

    @FXML
    private TableColumn<Manager, String> tableColumnEmail;

    @FXML
    private TableColumn<Manager, Department> tableColumnDepartment;

    @FXML
    private TableColumn<Manager, Manager> tableColumnEDIT;

    @FXML
    private TableColumn<Manager, Manager> tableColumnREMOVE;

    @FXML
    private Button btnNew;

    private ObservableList<Manager> obsList;

    public void setManagerService(ManagerService service) {
        this.service = service;
    }

    @FXML
    public void onBtnNewAction(ActionEvent event) {
        Stage parentStage = Utils.currentStage(event);
        Manager obj = new Manager();
        createDialogForm(obj, "/gui/ManagerForm.fxml", parentStage);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initializeNodes();
    }

    private void initializeNodes() {
        tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
        tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
        tableColumnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        
        tableColumnDepartment.setCellValueFactory(new PropertyValueFactory<>("department"));
        tableColumnDepartment.setCellFactory(column -> new TableCell<Manager, Department>() {
            @Override
            protected void updateItem(Department department, boolean empty) {
                super.updateItem(department, empty);
                if (empty || department == null) {
                    setText(null);
                } else {
                    setText(department.getName());
                }
            }
        });

        Stage stage = (Stage) Main.getMainScene().getWindow();
        tableViewManager.prefHeightProperty().bind(stage.heightProperty());
    }

    public void updateTableView() {
        if (service == null) {
            throw new IllegalStateException("Service estava nulo");
        }
        List<Manager> list = service.findAll();
        obsList = FXCollections.observableArrayList(list);
        tableViewManager.setItems(obsList);
        initEditButtons();
        initRemoveButtons();
    }

    private void createDialogForm(Manager obj, String absoluteName, Stage parentStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
            Pane pane = loader.load();

            ManagerFormController controller = loader.getController();
            controller.setManager(obj);
            controller.setServices(new ManagerService(), new DepartmentService());
            controller.loadAssociatedObjects();
            controller.subscribeDataChangeListener(this);
            controller.updateFormData();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Informe os dados do Gerente");
            dialogStage.setScene(new Scene(pane));
            dialogStage.setResizable(false);
            dialogStage.initOwner(parentStage);
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            Alerts.showAlert("IO Exception", "Erro ao carregar a tela", e.getMessage(), AlertType.ERROR);
        }
    }

    @Override
    public void onDataChanged() {
        updateTableView();
    }

    private void initEditButtons() {
        tableColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
        tableColumnEDIT.setCellFactory(param -> new TableCell<Manager, Manager>() {
            private final Button button = new Button("Editar");

            @Override
            protected void updateItem(Manager obj, boolean empty) {
                super.updateItem(obj, empty);
                if (obj == null) {
                    setGraphic(null);
                    return;
                }
                setGraphic(button);
                button.setOnAction(event -> createDialogForm(obj, "/gui/ManagerForm.fxml", Utils.currentStage(event)));
            }
        });
    }

    private void initRemoveButtons() {
        tableColumnREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
        tableColumnREMOVE.setCellFactory(param -> new TableCell<Manager, Manager>() {
            private final Button button = new Button("Deletar");

            @Override
            protected void updateItem(Manager obj, boolean empty) {
                super.updateItem(obj, empty);
                if (obj == null) {
                    setGraphic(null);
                    return;
                }
                setGraphic(button);
                button.setOnAction(event -> removeEntity(obj));
            }
        });
    }

    private void removeEntity(Manager obj) {
        Optional<ButtonType> result = Alerts.showConfirmation("Confirmação", "Tem certeza que deseja deletar?");
        if (result.get() == ButtonType.OK) {
            if (service == null) {
                throw new IllegalStateException("Service estava nulo");
            }
            try {
                service.remove(obj);
                updateTableView();
            } catch (Exception e) {
                Alerts.showAlert("Erro ao remover objeto", null, e.getMessage(), AlertType.ERROR);
            }
        }
    }
}