package gui;

import java.io.IOException;
import application.Main;
import gui.util.Alerts;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    @FXML
    private TextField txtUsername;

    @FXML
    private PasswordField txtPassword;

    @FXML
    public void onBtLoginAction(ActionEvent event) {
        String username = txtUsername.getText();
        String password = txtPassword.getText();

        if (username.isEmpty() || password.isEmpty()) {
            Alerts.showAlert("Aviso", null, "Preencha todos os campos!", AlertType.WARNING);
            return;
        }

        // Exemplo simples de validação (substitua pela checagem no Banco de Dados)
        if ("admin".equals(username) && "1234".equals(password)) {
            loadMainScreen(event);
        } else {
            Alerts.showAlert("Erro de Autenticação", null, "Usuário ou senha incorretos.", AlertType.ERROR);
        }
    }

    private void loadMainScreen(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/MainView.fxml"));
            Parent mainView = loader.load();

            Scene mainScene = new Scene(mainView);
            mainScene.getStylesheets().add(getClass().getResource("/application/application.css").toExternalForm());
            
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();

            stage.setScene(mainScene);
            stage.setTitle("Sistema Principal");
            stage.centerOnScreen();
            stage.show();

            // Salva a cena principal na classe Main caso precise acessar depois
            Main.setMainScene(mainScene);

        } catch (IOException e) {
            Alerts.showAlert("Erro de Carregamento", "Falha ao carregar a tela principal", e.getMessage(), AlertType.ERROR);
        }
    }
}