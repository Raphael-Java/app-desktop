package gui;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import application.UserSession;
import gui.util.Alerts;
import gui.util.PdfExporter;
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
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Sales;
import model.services.SalesService;
import model.services.SellerService;

public class SalesListController implements Initializable {

    private SalesService service;

    @FXML
    private WebView salesChartWebView;

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
    private TableColumn<Sales, Sales> tableColumnREMOVE;

    @FXML
    private Button btNew;

    @FXML
    private Button btExportPdf;

    // Componentes injetados para controle de tamanho dinâmico da imagem central
    @FXML
    private ImageView imageViewOffroad;

    @FXML
    private StackPane imageContainer;

    private ObservableList<Sales> obsList;

    @FXML
    public void onBtExportPdfAction() {
        if (tableViewSales != null && !tableViewSales.getItems().isEmpty()) {
            PdfExporter.exportToPDF(tableViewSales, "Relatório de Vendas", "vendedores");
        } else {
            Alerts.showAlert("Aviso", null, "Não há dados na tabela para exportar.", AlertType.WARNING);
        }
    }

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
        initializeImageBinding();
        initializeWebView();
        applyPermissions();
    }

    private void initializeNodes() {
        tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
        tableColumnDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        Utils.formatTableColumnDate(tableColumnDate, "dd/MM/yyyy");
        tableColumnAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        Utils.formatTableColumnDouble(tableColumnAmount, 2);
        tableColumnStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        tableColumnSeller.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(
            param.getValue().getSeller() != null ? param.getValue().getSeller().getName() : ""
        ));
    }

    /**
     * Faz a imagem ajustar sua largura e altura de forma dinâmica com base no StackPane central,
     * garantindo expansão proporcional sem distorção.
     */
    private void initializeImageBinding() {
        if (imageViewOffroad != null && imageContainer != null) {
            imageViewOffroad.fitWidthProperty().bind(imageContainer.widthProperty());
            imageViewOffroad.fitHeightProperty().bind(imageContainer.heightProperty());
        }
    }

    private void initializeWebView() {
        if (salesChartWebView != null) {
            try {
                // Força o fundo transparente do container do JavaFX
                salesChartWebView.setStyle("-fx-background-color: transparent;");

                // Executa na thread de UI do JavaFX para aplicar transparência no container nativo do WebKit
                javafx.application.Platform.runLater(() -> {
                    try {
                        java.lang.reflect.Method getPageMethod = salesChartWebView.getEngine().getClass().getMethod("getPage");
                        Object page = getPageMethod.invoke(salesChartWebView.getEngine());
                        if (page != null) {
                            java.lang.reflect.Method setBackgroundColor = page.getClass().getMethod("setBackgroundColor", int.class);
                            setBackgroundColor.invoke(page, 0); // 0 = Transparente (ARGB)
                        }
                    } catch (Exception ignored) {
                    }
                });

                URL chartUrl = getClass().getResource("/img/finance-chart.html");
                if (chartUrl != null) {
                    salesChartWebView.getEngine().load(chartUrl.toExternalForm());

                    salesChartWebView.getEngine().getLoadWorker().stateProperty().addListener(
                        (obs, oldState, newState) -> {
                            if (newState == javafx.concurrent.Worker.State.SUCCEEDED) {
                                salesChartWebView.getEngine().executeScript(
                                    "document.documentElement.style.background = 'transparent';" +
                                    "document.body.style.background = 'transparent';"
                                );
                            }
                        }
                    );
                }
            } catch (Exception e) {
                System.out.println("Erro ao carregar animação: " + e.getMessage());
            }
        }
    }

    public void updateTableView() {
        if (service == null) {
            throw new IllegalStateException("Service estava nulo");
        }
        List<Sales> list = service.findAll();
        obsList = FXCollections.observableArrayList(list);
        tableViewSales.setItems(obsList);
    }

    private void applyPermissions() {
        // Garante que o botão de nova venda fique sempre visível no menu
        if (btNew != null) {
            btNew.setVisible(true);
        }

        UserSession session = UserSession.getInstance();
        if (session != null && !session.isAdmin()) {
            if (tableColumnREMOVE != null) {
                tableColumnREMOVE.setVisible(false);
            }
        }
    }
}