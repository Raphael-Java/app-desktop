package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import application.Main;
import gui.util.Alerts;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class ProfileViewController implements Initializable {

    @FXML private StackPane stackPane;
    @FXML private ImageView bgImage;
    @FXML private TextField txtProfileName;
    @FXML private TextField txtProfileEmail;
    @FXML private TextField txtProfileRole;
    @FXML private PasswordField txtNewPassword;
    @FXML private PasswordField txtConfirmPassword;
    @FXML private Button btSaveProfile;
    @FXML private Button btLogout;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if (bgImage != null) {
            // Garante o vínculo do tamanho assim que a tela for carregada na cena principal
            javafx.application.Platform.runLater(() -> {
                if (bgImage.getScene() != null) {
                    bgImage.fitWidthProperty().bind(bgImage.getScene().widthProperty());
                    bgImage.fitHeightProperty().bind(bgImage.getScene().heightProperty());
                }
            });
        }
    }

    @FXML
    public void onBtSaveProfileAction() {
        if (!txtNewPassword.getText().equals(txtConfirmPassword.getText())) {
            Alerts.showAlert("Erro", null, "As senhas não coincidem!", AlertType.ERROR);
            return;
        }
        
        Alerts.showAlert("Sucesso", null, "Perfil atualizado com sucesso!", AlertType.INFORMATION);
        txtNewPassword.clear();
        txtConfirmPassword.clear();
    }

    @FXML
    public void onBtLogoutAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/LoginView.fxml"));
            Parent parent = loader.load();
            
            Stage stage = (Stage) Main.getMainScene().getWindow();
            stage.setScene(new Scene(parent));
            stage.setTitle("Login");
        } catch (IOException e) {
            Alerts.showAlert("Erro", "Erro ao realizar logout", e.getMessage(), AlertType.ERROR);
        }
    }
}