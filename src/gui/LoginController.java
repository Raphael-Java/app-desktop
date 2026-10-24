package gui;

import java.io.IOException;

import application.Main;
import application.UserSession;
import gui.util.Alerts;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.entities.Department;
import model.entities.Seller;
import model.services.SellerService;

public class LoginController {

    @FXML
    private TextField txtUsername;

    @FXML
    private PasswordField txtPassword;

    private SellerService sellerService = new SellerService();

    @FXML
    public void onBtLoginAction(ActionEvent event) {
        String username = txtUsername.getText();
        String password = txtPassword.getText();

        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            Alerts.showAlert("Aviso", null, "Preencha todos os campos!", AlertType.WARNING);
            return;
        }

        // 1. Tenta autenticar pelo Banco de Dados
        Seller userLogged = authenticateUser(username, password);

        if (userLogged != null) {
            // Salva o usuário autenticado na sessão global
            UserSession.initSession(userLogged);
            
            // Abre a tela principal trocando a cena atual
            loadMainScreen(event);
        } else {
            Alerts.showAlert("Erro de Autenticação", null, "Usuário ou senha incorretos.", AlertType.ERROR);
        }
    }

    /**
     * Valida as credenciais digitadas.
     * Atualmente possui fallback mockado para o login 'admin'/'1234'.
     */
    private Seller authenticateUser(String username, String password) {
        // Tenta buscar no banco se o login for um e-mail cadastrado
        try {
            Seller seller = sellerService.findByEmail(username);
            if (seller != null) {
                // Aqui você pode checar a senha quando tiver a coluna no banco:
                // if (seller.getPassword().equals(password)) return seller;
                return seller;
            }
        } catch (Exception e) {
            System.out.println("Aviso: Consulta de login no banco falhou, usando autenticação mock.");
        }

        // Mock para teste rápido de Admin/User caso não ache no banco
        if ("admin".equalsIgnoreCase(username) && "1234".equals(password)) {
        	Seller adminUser = new Seller(1, "Administrador", "admin@gmail.com", null, 5000.0, new Department(1, "TI", "Gerente TI"));
            adminUser.setRole("ADMIN");
            return adminUser;
        } else if ("user".equalsIgnoreCase(username) && "1234".equals(password)) {
        	Seller commonUser = new Seller(2, "Vendedor Comum", "user@gmail.com", null, 2500.0, new Department(2, "Vendas", "Gerente Vendas"));
            commonUser.setRole("USER");
            return commonUser;
        }

        return null;
    }

    private void loadMainScreen(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/MainView.fxml"));
            Parent mainView = loader.load();

            Scene mainScene = new Scene(mainView);
            
            // Tenta adicionar o CSS se o arquivo existir
            try {
                mainScene.getStylesheets().add(getClass().getResource("/application/application.css").toExternalForm());
            } catch (Exception e) {
                System.out.println("Aviso: application.css não encontrado.");
            }

            // Pega a janela atual através do evento do botão
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            stage.setScene(mainScene);
            stage.setTitle("Sistema Principal");
            stage.centerOnScreen();
            stage.show();

            // Atualiza a cena principal global na classe Main
            Main.setMainScene(mainScene);

        } catch (IOException e) {
            Alerts.showAlert("Erro de Carregamento", "Falha ao carregar a tela principal", e.getMessage(), AlertType.ERROR);
        }
    }
}