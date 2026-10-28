package gui;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class CadastroClienteController {

    @FXML private TextField txtNome;
    @FXML private TextField txtDocumento;
    @FXML private TextField txtEmail;
    @FXML private TextField txtTelefone;
    @FXML private TextField txtVeiculoInteresse;
    @FXML private TextField txtVeiculoTroca;
    @FXML private ComboBox<String> cbEstagio;
    @FXML private ComboBox<String> cbStatus;
    @FXML private Label lblMensagem;
    @FXML private Button btnCancelar;

    @FXML
    public void initialize() {
        cbStatus.setItems(FXCollections.observableArrayList("Ativo", "Inativo", "Bloqueado"));
        cbStatus.setValue("Ativo");

        cbEstagio.setItems(FXCollections.observableArrayList(
            "1. Lead / Primeiro Contato", 
            "2. Proposta Enviada", 
            "3. Test Drive Realizado", 
            "4. Fechado / Vendido", 
            "5. Perdido"
        ));
        cbEstagio.setValue("1. Lead / Primeiro Contato");
    }

    @FXML
    private void handleSalvar() {
        String nome = txtNome.getText().trim();
        String documento = txtDocumento.getText().trim();

        if (nome.isEmpty() || documento.isEmpty()) {
            lblMensagem.setText("Preencha todos os campos obrigatórios (*).");
            lblMensagem.setVisible(true);
            return;
        }

        System.out.println("Cliente salvo com sucesso na página principal!");
        
        // Aqui você pode chamar um método para retornar à tabela de clientes automaticamente
        voltarParaTabela();
    }

    @FXML
    private void handleCancelar() {
        // Apenas retorna para a tela anterior (tabela) sem salvar
        voltarParaTabela();
    }

    private void voltarParaTabela() {
        // Lógica opcional para recarregar a página da tabela de clientes no painel central
        System.out.println("Voltando para a listagem...");
    }
}