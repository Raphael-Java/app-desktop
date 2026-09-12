package gui;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.services.ShippingService;

public class ShippingModalController implements Initializable {

    @FXML private ComboBox<String> comboBoxRegion;
    @FXML private TextField txtWeight;
    @FXML private TextField txtTotalAmount;
    @FXML private Label lblShippingCost;

    private ShippingService shippingService = new ShippingService();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if (comboBoxRegion != null) {
            comboBoxRegion.setItems(FXCollections.observableArrayList(
                "Sudeste", "Sul", "Centro-Oeste", "Nordeste", "Norte"
            ));
        }
    }

    @FXML
    public void onBtCalculateAction() {
        try {
            String region = comboBoxRegion.getValue();
            if (region == null) {
                lblShippingCost.setText("Selecione a região!");
                return;
            }

            double weight = txtWeight.getText().isEmpty() ? 1.0 : Double.parseDouble(txtWeight.getText().replace(",", "."));
            double total = txtTotalAmount.getText().isEmpty() ? 0.0 : Double.parseDouble(txtTotalAmount.getText().replace(",", "."));

            double cost = shippingService.calculateShipping(region, weight, total);

            if (cost == 0.0) {
                lblShippingCost.setText("FRETE GRÁTIS 🎉");
            } else {
                lblShippingCost.setText(String.format("R$ %.2f", cost));
            }
        } catch (NumberFormatException e) {
            lblShippingCost.setText("Valor/Peso inválido");
        }
    }

    @FXML
    public void onBtCloseAction() {
        Stage stage = (Stage) lblShippingCost.getScene().getWindow();
        stage.close();
    }
}