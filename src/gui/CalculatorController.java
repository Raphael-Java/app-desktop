package gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class CalculatorController {

    @FXML
    private TextField txtDisplay;

    @FXML
    private ListView<String> historyListView;

    private double number1 = 0;
    private String operator = "";
    private boolean startNewValue = true;

    @FXML
    public void onBtnNumberPressed(ActionEvent event) {
        String value = ((Button) event.getSource()).getText();
        if (startNewValue) {
            txtDisplay.setText(value);
            startNewValue = false;
        } else {
            if (txtDisplay.getText().equals("0")) {
                txtDisplay.setText(value);
            } else {
                txtDisplay.setText(txtDisplay.getText() + value);
            }
        }
    }

    @FXML
    public void onBtnOperationPressed(ActionEvent event) {
        String value = ((Button) event.getSource()).getText();
        if (!operator.isEmpty() && !startNewValue) {
            calculate();
        }
        number1 = Double.parseDouble(txtDisplay.getText());
        operator = value;
        startNewValue = true;
    }

    @FXML
    public void onBtnEqualsPressed(ActionEvent event) {
        if (operator.isEmpty()) {
            return;
        }
        calculate();
        operator = "";
        startNewValue = true;
    }

    @FXML
    public void onBtnDecimalPressed(ActionEvent event) {
        if (startNewValue) {
            txtDisplay.setText("0.");
            startNewValue = false;
        } else if (!txtDisplay.getText().contains(".")) {
            txtDisplay.setText(txtDisplay.getText() + ".");
        }
    }

    @FXML
    public void onBtnClearPressed(ActionEvent event) {
        txtDisplay.setText("0");
        number1 = 0;
        operator = "";
        startNewValue = true;
    }

    @FXML
    public void onBtnBackSpacePressed(ActionEvent event) {
        if (!startNewValue) {
            String currentText = txtDisplay.getText();
            if (currentText.length() > 1) {
                txtDisplay.setText(currentText.substring(0, currentText.length() - 1));
            } else {
                txtDisplay.setText("0");
                startNewValue = true;
            }
        }
    }

    @FXML
    public void onBtnClearHistoryPressed(ActionEvent event) {
        if (historyListView != null) {
            historyListView.getItems().clear();
        }
    }

    @FXML
    public void onBtnCommercialPressed(ActionEvent event) {
        String actionText = ((Button) event.getSource()).getText();
        double currentVal = Double.parseDouble(txtDisplay.getText());
        double result = currentVal;
        String description = "";

        switch (actionText) {
            case "-10%":
                result = currentVal * 0.90;
                description = currentVal + " - 10% desc.";
                break;
            case "-20%":
                result = currentVal * 0.80;
                description = currentVal + " - 20% desc.";
                break;
            case "+15%":
                result = currentVal * 1.15;
                description = currentVal + " + 15% margem";
                break;
            case "+23% IVA":
                result = currentVal * 1.23;
                description = currentVal + " + 23% IVA";
                break;
        }

        String resultStr = formatNumber(result);
        
        if (historyListView != null) {
            historyListView.getItems().add(description + " = " + resultStr);
        }

        txtDisplay.setText(resultStr);
        number1 = result;
        startNewValue = true;
    }

    private void calculate() {
        double number2 = Double.parseDouble(txtDisplay.getText());
        double result = 0;
        String opSymbol = operator;
        
        switch (operator) {
            case "+":
                result = number1 + number2;
                break;
            case "-":
                result = number1 - number2;
                break;
            case "×":
                result = number1 * number2;
                break;
            case "÷":
                if (number2 != 0) {
                    result = number1 / number2;
                } else {
                    txtDisplay.setText("Erro");
                    startNewValue = true;
                    operator = "";
                    return;
                }
                break;
        }

        String resultStr = formatNumber(result);
        
        if (historyListView != null) {
            String historyEntry = formatNumber(number1) + " " + opSymbol + " " + formatNumber(number2) + " = " + resultStr;
            historyListView.getItems().add(historyEntry);
        }

        txtDisplay.setText(resultStr);
        number1 = result;
    }

    private String formatNumber(double d) {
        if (d == (long) d) {
            return String.valueOf((long) d);
        }
        return String.valueOf(d);
    }
}