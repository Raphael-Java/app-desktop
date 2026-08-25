package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import application.Main;
import gui.util.Alerts;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import model.services.DepartmentService;
import model.services.SalesService;
import model.services.SellerService;

public class MainViewController implements Initializable {

    @FXML private MenuItem menuItemHome;
    @FXML private MenuItem menuItemSeller;
    @FXML private MenuItem menuItemDepartment;
    @FXML private MenuItem menuItemSales;
    @FXML private MenuItem menuItemAbout;

    @FXML private ImageView logoImage;
    @FXML private AnchorPane anchorPane;
    @FXML private VBox mainContainer;

    @FXML
    public void onMenuItemHomeAction() {
        if (mainContainer != null) {
            mainContainer.getChildren().clear();
            mainContainer.getChildren().add(anchorPane);
        }
    }

    @FXML
    public void onBtReportsAction() {
        loadView("/gui/ReportsView.fxml", (ReportsViewController controller) -> {});
    }
    
    @FXML
    public void onBtNotificationsAction() {
        Alerts.showAlert("Atalho", null, "Sem novas notificações", AlertType.INFORMATION);
    }

    @FXML
    public void onBtProfileAction() {
        loadView("/gui/ProfileView.fxml", (ProfileViewController controller) -> {});
    }

    @FXML
    public void onMenuItemDashboardAction() {
        loadView("/gui/DashboardView.fxml", (DashboardController controller) -> {
            controller.loadDashboardData();
        });
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
    
    @FXML
    public void onMenuItemSalesAction() {
        loadView("/gui/SalesList.fxml", (SalesListController controller) -> {
            controller.setSalesService(new SalesService());
            controller.updateTableView();
        });
    }
    
    @FXML
    public void onBtSettingsAction() {
        loadView("/gui/SettingsView.fxml", (SettingsViewController controller) -> {
            // Se precisar passar serviços para a tela de configurações no futuro
        });
    }

    @FXML
    public void onMenuItemAboutAction() {
        loadView("/gui/About.fxml", x -> {});
    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        if (logoImage != null && anchorPane != null) {
            try {
                Image image = new Image(
                    getClass().getResourceAsStream("/img/Gemini_Generated_Image_h5a0jvh5a0jvh5a0.png")
                );

                logoImage.setImage(image);
                logoImage.setPreserveRatio(false);

                anchorPane.setMinSize(0, 0);
                anchorPane.setPrefSize(0, 0);

                logoImage.fitWidthProperty().bind(anchorPane.widthProperty());
                logoImage.fitHeightProperty().bind(anchorPane.heightProperty());
            } catch (Exception e) {
                System.out.println("Aviso: Imagem de logo não carregada.");
            }
        }
    }

    private synchronized <T> void loadView(String absoluteName, Consumer<T> initializingAction) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
            VBox newVBox = loader.load();

            Scene mainScene = Main.getMainScene();
            
            VBox mainVBox = mainContainer;
            if (mainVBox == null) {
                BorderPane borderPane = (BorderPane) mainScene.getRoot();
                ScrollPane scrollPane = (ScrollPane) borderPane.getCenter();
                mainVBox = (VBox) scrollPane.getContent();
            }

            mainVBox.getChildren().clear();
            mainVBox.getChildren().addAll(newVBox.getChildren());

            T controller = loader.getController();
            initializingAction.accept(controller);

        } catch (IOException e) {
            Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(), AlertType.ERROR);
        }
    }
}