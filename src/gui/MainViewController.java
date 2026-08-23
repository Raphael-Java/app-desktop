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

	@FXML
	private MenuItem menuItemSeller;

	@FXML
	private MenuItem menuItemDepartment;

	@FXML
	private MenuItem menuItemAbout;

	@FXML
	private ImageView logoImage;

	@FXML
	private AnchorPane anchorPane;

	@FXML
	private ImageView imageView;

	@FXML
	private VBox mainContainer;

	@FXML
	public void onBtReportsAction() {
		Alerts.showAlert("Atalho", null, "Tela de Relatórios em desenvolvimento", AlertType.INFORMATION);
	}

	@FXML
	public void onBtSettingsAction() {
		Alerts.showAlert("Atalho", null, "Tela de Configurações em desenvolvimento", AlertType.INFORMATION);
	}

	@FXML
	public void onBtNotificationsAction() {
		Alerts.showAlert("Atalho", null, "Sem novas notificações", AlertType.INFORMATION);
	}

	@FXML
	public void onBtProfileAction() {
		Alerts.showAlert("Atalho", null, "Perfil do Usuário Logado", AlertType.INFORMATION);
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
	public void onMenuItemAboutAction() {
		loadView("/gui/About.fxml", x -> {});
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// Proteção caso os elementos de imagem não existam na tela atual
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
			
			// Lógica universal para encontrar o VBox do centro (suporta BorderPane)
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