package gui;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import application.Main;
import application.UserSession;
import db.DB;
import gui.util.Alerts;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.services.DepartmentService;
import model.services.ManagerService;
import model.services.SalesService;
import model.services.SellerService;

public class MainViewController implements Initializable {

    // Menus Principais
    @FXML private Menu menuRegistration;

    // Itens do Menu
    @FXML private MenuItem menuItemHome;
    @FXML private MenuItem menuItemSeller;
    @FXML private MenuItem menuItemDepartment;
    @FXML private MenuItem menuItemManager; // 👈 Adicionado aqui
    @FXML private MenuItem menuItemSales;
    @FXML private MenuItem menuItemNewSale;
    @FXML private MenuItem menuItemAbout;

    // Componentes de Layout
    @FXML private ImageView logoImage;
    @FXML private AnchorPane anchorPane;
    @FXML private VBox mainContainer;
    @FXML private HBox cardsContainer;
    
    // Labels dos Cards da Home
    @FXML private Label lblTodaySales;
    @FXML private Label lblMonthTotal;
    @FXML private Label lblActiveSellers;
    @FXML private Label lblAverageTicket;
    
    // Status do Usuário no Topo
    @FXML private Label lblUserName;
    @FXML private Label lblUserRole;
    @FXML private ImageView imgUserAvatar;

    // Status do Banco de Dados no Rodapé
    @FXML private Circle circleDbStatus;
    @FXML private Label lblDbStatus;

    // Guarda a referência exata da estrutura da Home carregada no início
    private Node homeNode;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Salva o VBox da Home logo que a tela carrega
        this.homeNode = mainContainer;

        initializeLogoImage();
        updateDashboardCards(1250.00, 45280.00, 5, 85.50);
        
        loadUserSessionData();
        checkDatabaseConnection();
    }

    // -------------------------------------------------------------
    // ATALHOS RÁPIDOS (Menu Lateral Esquerdo)
    // -------------------------------------------------------------

    @FXML
    public void onBtCadastrarClienteAction() {
        loadView("/gui/CadastroCliente.fxml", (Object controller) -> {});
    }
    
    @FXML
    public void onBtConsultarEstoqueAction() {
        loadView("/gui/ConsultaEstoque.fxml", (Object controller) -> {});
    }

    @FXML
    public void onMenuItemCalculatorAction() {
        loadView("/gui/CalculatorView.fxml", (Object controller) -> {});
    }
    
    @FXML
    public void onBtCalculadoraAction() {
        loadView("/gui/CalculatorView.fxml", (Object controller) -> {});
    }

    // -------------------------------------------------------------
    // AÇÕES DE MENUS E NAVEGAÇÃO
    // -------------------------------------------------------------

    @FXML
    public void onMenuItemHomeAction() {
        if (homeNode != null) {
            setMainContent(homeNode);
            updateDashboardCards(1250.00, 45280.00, 5, 85.50);
        }
    }

    @FXML
    public void onBtReportsAction() {
        loadView("/gui/ReportsView.fxml", (ReportsViewController controller) -> {});
    }

    @FXML
    public void onBtExportPdfAction() {
        loadView("/gui/SellerList.fxml", (SellerListController controller) -> {
            controller.setSellerService(new SellerService());
            controller.updateTableView();
            
            Platform.runLater(() -> {
                controller.onBtExportPdfAction();
            });
        });
    }    

    @FXML
    public void onBtNotificationsAction() {
        StringBuilder notifs = new StringBuilder();

        double totalMes = 45280.00;
        double metaMes = 50000.00;
        
        if (totalMes >= metaMes) {
            notifs.append("🎉 META ATINGIDA! Parabéns à equipe.\n\n");
        } else {
            double restante = metaMes - totalMes;
            notifs.append(String.format("🎯 Faltam R$ %.2f para atingir a meta do mês!\n\n", restante));
        }

        int vendedoresAtivos = 5;
        if (vendedoresAtivos < 3) {
            notifs.append("⚠️ AVISO: Equipe com número baixo de vendedores ativos.\n\n");
        }

        notifs.append("📌 Lembrete: Fechamento de comissões na próxima sexta-feira.");

        Alerts.showAlert("Central de Notificações", "Alertas do Sistema", notifs.toString(), AlertType.INFORMATION);
    }

    @FXML
    public void onBtProfileAction() {
        loadView("/gui/ProfileView.fxml", (ProfileViewController controller) -> {});
    }

    @FXML
    public void onMenuItemDashboardAction() {
        loadView("/gui/DashboardView.fxml", (DashboardController controller) -> {});
    }

    @FXML
    public void onMenuItemSellerAction() {
        loadView("/gui/SellerList.fxml", (SellerListController controller) -> {
            controller.setSellerService(new SellerService());
            controller.updateTableView();
        });
    }

    @FXML
    public void onMenuItemDepartmentAction() {
        loadView("/gui/DepartmentList.fxml", (DepartmentListController controller) -> {
            controller.setDepartmentService(new DepartmentService());
            controller.updateTableView();
        });
    }

    // 👈 NOVO MÉTODO PARA ABRIR A TELA DE MANAGERS
    @FXML
    public void onMenuItemManagerAction() {
        loadView("/gui/ManagerList.fxml", (ManagerListController controller) -> {
            controller.setManagerService(new ManagerService());
            controller.updateTableView();
        });
    }

    @FXML
    public void onMenuItemSalesAction() {
        loadView("/gui/SalesList.fxml", (SalesListController controller) -> {
            controller.setSalesService(new SalesService());
            controller.updateTableView();
        });
    }

    @FXML
    public void onMenuItemNewSaleAction() {
        loadView("/gui/SalesForm.fxml", (SalesFormController controller) -> {});
    }

    @FXML
    public void onBtSettingsAction() {
        loadView("/gui/SettingsView.fxml", (SettingsViewController controller) -> {});
    }

    @FXML
    public void onMenuItemAboutAction() {
        loadView("/gui/About.fxml", x -> {});
    }

    @FXML
    public void onBtCalculateShippingAction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/ShippingModal.fxml"));
            VBox parent = loader.load();
            
            Stage stage = new Stage();
            stage.setTitle("Calculadora de Frete");
            stage.setScene(new Scene(parent));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.showAndWait();
        } catch (IOException e) {
            Alerts.showAlert("Erro", "Erro ao abrir calculador de frete", e.getMessage(), AlertType.ERROR);
        }
    }

    // -------------------------------------------------------------
    // MÉTODOS AUXILIARES
    // -------------------------------------------------------------

    private void loadUserSessionData() {
        UserSession session = UserSession.getInstance();
        
        if (session != null && session.getLoggedUser() != null) {
            if (lblUserName != null) lblUserName.setText(session.getLoggedUser().getName());
            if (lblUserRole != null) lblUserRole.setText(session.isAdmin() ? "Administrador" : "Operador");
        } else {
            if (lblUserName != null) lblUserName.setText("Lucas Silva");
            if (lblUserRole != null) lblUserRole.setText("Administrador");
        }
        
        applyPermissions();
    }

    private void checkDatabaseConnection() {
        if (circleDbStatus == null || lblDbStatus == null) return;

        try {
            Connection conn = DB.getConnection();
            if (conn != null && !conn.isClosed()) {
                circleDbStatus.setFill(Color.web("#22c55e"));
                lblDbStatus.setText("Conectado ao Banco de Dados");
            } else {
                setDbDisconnectedStatus();
            }
        } catch (Exception e) {
            setDbDisconnectedStatus();
        }
    }

    private void setDbDisconnectedStatus() {
        if (circleDbStatus != null) circleDbStatus.setFill(Color.web("#ef4444"));
        if (lblDbStatus != null) lblDbStatus.setText("Desconectado do Banco de Dados");
    }

    public void updateDashboardCards(double todaySales, double monthTotal, int sellersCount, double avgTicket) {
        if (lblTodaySales != null) lblTodaySales.setText(String.format("R$ %.2f", todaySales));
        if (lblMonthTotal != null) lblMonthTotal.setText(String.format("R$ %.2f", monthTotal));
        if (lblActiveSellers != null) lblActiveSellers.setText(String.valueOf(sellersCount));
        if (lblAverageTicket != null) lblAverageTicket.setText(String.format("R$ %.2f", avgTicket));
    }

    private void initializeLogoImage() {
        if (logoImage != null && anchorPane != null) {
            try {
                Image image = new Image(
                    getClass().getResourceAsStream("/img/office_work.png")
                );

                logoImage.setImage(image);
                logoImage.setPreserveRatio(false);

                VBox.setVgrow(anchorPane, Priority.ALWAYS);

                anchorPane.setMinSize(0, 0);
                anchorPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

                logoImage.fitWidthProperty().bind(anchorPane.widthProperty());
                logoImage.fitHeightProperty().bind(anchorPane.heightProperty());
            } catch (Exception e) {
                System.out.println("Aviso: Imagem de logo não carregada.");
            }
        }
    }

    private void applyPermissions() {
        if (menuRegistration != null) {
            menuRegistration.setVisible(true);
        }
        if (menuItemSeller != null) {
            menuItemSeller.setDisable(false);
        }
    }

    private synchronized <T> void loadView(String absoluteName, Consumer<T> initializingAction) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
            Node newContent = loader.load(); 

            T controller = loader.getController();
            if (initializingAction != null && controller != null) {
                initializingAction.accept(controller);
            }

            setMainContent(newContent);

        } catch (IOException e) {
            Alerts.showAlert("IO Exception", "Erro ao carregar a página", e.getMessage(), AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void setMainContent(Node node) {
        Scene mainScene = Main.getMainScene();
        if (mainScene != null && mainScene.getRoot() instanceof BorderPane) {
            BorderPane borderPane = (BorderPane) mainScene.getRoot();
            
            if (borderPane.getCenter() instanceof ScrollPane) {
                ScrollPane scrollPane = (ScrollPane) borderPane.getCenter();

                scrollPane.setFitToWidth(true);
                scrollPane.setFitToHeight(true);
                scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
                scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

                scrollPane.setContent(node);
            }
        }
    }
}