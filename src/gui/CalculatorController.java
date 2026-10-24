package gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class CalculatorController {

    @FXML
    private TextField txtDisplay;

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
            txtDisplay.setText(txtDisplay.getText() + value);
        }
    }

    @FXML
    public void onBtnOperationPressed(ActionEvent event) {
        String value = ((Button) event.getSource()).getText();
        if (!operator.isEmpty()) {
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

    private void calculate() {
        double number2 = Double.parseDouble(txtDisplay.getText());
        double result = 0;
        switch (operator) {
            case "+":
                result = number1 + number2;
                break;
            case "-":
                result = number1 - number2;
                break;
            case "*":
                result = number1 * number2;
                break;
            case "/":
                if (number2 != 0) {
                    result = number1 / number2;
                } else {
                    txtDisplay.setText("Erro");
                    return;
                }
                break;
        }
        if (result == (long) result) {
            txtDisplay.setText(String.valueOf((long) result));
        } else {
            txtDisplay.setText(String.valueOf(result));
        }
        number1 = result;
    }
}