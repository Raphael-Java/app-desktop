package gui;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class CadastroClienteController {

    @FXML private TextField txtNome;
    @FXML private TextField txtDocumento;
    @FXML private TextField txtEmail;
    @FXML private TextField txtTelefone;
    @FXML private ComboBox<String> cbStatus;
    @FXML private Label lblMensagem;
    @FXML private Button btnCancelar;

    @FXML
    public void initialize() {
        // Inicializa as opções do ComboBox
        cbStatus.setItems(FXCollections.observableArrayList("Ativo", "Inativo", "Bloqueado"));
        cbStatus.setValue("Ativo");
    }

    @FXML
    private void handleSalvar() {
        String nome = txtNome.getText().trim();
        String documento = txtDocumento.getText().trim();

        // Validação simples
        if (nome.isEmpty() || documento.isEmpty()) {
            lblMensagem.setText("Preencha todos os campos obrigatórios (*).");
            lblMensagem.setVisible(true);
            return;
        }

        // Aqui você insere a lógica de salvar no banco de dados (DAO / Repository)
        System.out.println("Salvando Cliente: " + nome + " - " + documento);

        // Fecha a janela após salvar
        fecharJanela();
    }

    @FXML
    private void handleCancelar() {
        fecharJanela();
    }

    private void fecharJanela() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }
}