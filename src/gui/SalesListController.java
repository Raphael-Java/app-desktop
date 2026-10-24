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
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Sales;
import model.services.SalesService;
import model.services.SellerService;

public class SalesListController implements Initializable {

    private SalesService service;

    @FXML
    private HBox salesHBox;

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

    private void initializeImageBinding() {
        if (imageViewOffroad != null && imageContainer != null) {
            // Faz a imagem preencher 100% da largura e altura da caixa
            imageViewOffroad.fitWidthProperty().bind(imageContainer.widthProperty());
            imageViewOffroad.fitHeightProperty().bind(imageContainer.heightProperty());

            // Remove o clip individual para evitar vãos transparentes entre as colunas
            imageContainer.setClip(null);
        }

        // Aplica o arredondamento nas bordas do conjunto inteiro (Tabela + Imagem + Gráfico)
        if (salesHBox != null) {
            Rectangle clip = new Rectangle();
            clip.widthProperty().bind(salesHBox.widthProperty());
            clip.heightProperty().bind(salesHBox.heightProperty());
            clip.setArcWidth(16);
            clip.setArcHeight(16);
            salesHBox.setClip(clip);
        }
    }

    private void initializeWebView() {
        if (salesChartWebView != null) {
            try {
                salesChartWebView.setStyle("-fx-background-color: transparent;");

                URL chartUrl = getClass().getResource("/img/finance-chart.html");
                if (chartUrl != null) {
                    salesChartWebView.getEngine().load(chartUrl.toExternalForm());

                    // Esconde as barras de rolagem nativas da página Web para evitar scroll na tela
                    salesChartWebView.getEngine().getLoadWorker().stateProperty().addListener(
                        (obs, oldState, newState) -> {
                            if (newState == javafx.concurrent.Worker.State.SUCCEEDED) {
                                salesChartWebView.getEngine().executeScript(
                                    "document.documentElement.style.overflow = 'hidden';" +
                                    "document.body.style.overflow = 'hidden';" +
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