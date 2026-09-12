package gui;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import gui.util.Alerts;
import gui.util.DataBackup;
import gui.AppSettings;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;

public class SettingsViewController implements Initializable {

    @FXML private RadioButton radioLight;
    @FXML private RadioButton radioDark;
    @FXML private ComboBox<String> comboCurrency;
    @FXML private ComboBox<String> comboLanguage;
    @FXML private Button btSave;
    @FXML private Button btCancel;
    
    @FXML
    public void onBtBackupAction() {
        DataBackup.executeBackup();
    }

    @FXML
    public void onBtRestoreAction() {
        Optional<ButtonType> result = Alerts.showConfirmation("Atenção", "Deseja realmente sobrescrever os dados atuais pelo backup?");
        if (result.isPresent() && result.get() == ButtonType.OK) {
            DataBackup.restoreBackup();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Opções dos ComboBoxes
        comboCurrency.setItems(FXCollections.observableArrayList("Real (R$)", "Dólar (US$)", "Euro (€)"));
        comboLanguage.setItems(FXCollections.observableArrayList("Português (BR)", "English (US)", "Español"));

        // Carrega o estado atual salvo
        if ("Escuro".equals(AppSettings.getTheme())) {
            radioDark.setSelected(true);
        } else {
            radioLight.setSelected(true);
        }

        comboCurrency.setValue(AppSettings.getCurrency());
        comboLanguage.setValue(AppSettings.getLanguage());
    }

    @FXML
    public void onBtSaveAction(ActionEvent event) {
        String selectedTheme = radioDark.isSelected() ? "Escuro" : "Claro";
        AppSettings.setTheme(selectedTheme);
        AppSettings.setCurrency(comboCurrency.getValue());
        AppSettings.setLanguage(comboLanguage.getValue());

        Alerts.showAlert("Sucesso", null, "Configurações salvas com sucesso!", AlertType.INFORMATION);
    }

    @FXML
    public void onBtCancelAction(ActionEvent event) {
        MainViewController mainController = new MainViewController();
        mainController.onMenuItemDashboardAction();
    }
}