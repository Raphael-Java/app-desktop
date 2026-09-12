package gui.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class Exporter {

    public static <T> void exportToCSV(TableView<T> tableView, String defaultFileName) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Salvar Relatório CSV");
        fileChooser.setInitialFileName(defaultFileName + ".csv");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Arquivos CSV (*.csv)", "*.csv"));

        File file = fileChooser.showSaveDialog(new Stage());

        if (file != null) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                // Escreve os cabeçalhos das colunas
                StringBuilder header = new StringBuilder();
                for (int i = 0; i < tableView.getColumns().size(); i++) {
                    TableColumn<T, ?> col = tableView.getColumns().get(i);
                    header.append("\"").append(col.getText()).append("\"");
                    if (i < tableView.getColumns().size() - 1) header.append(",");
                }
                writer.write(header.toString());
                writer.newLine();

                // Escreve os dados de cada linha
                for (T item : tableView.getItems()) {
                    StringBuilder row = new StringBuilder();
                    for (int i = 0; i < tableView.getColumns().size(); i++) {
                        TableColumn<T, ?> col = tableView.getColumns().get(i);
                        Object cellData = col.getCellData(item);
                        String value = cellData != null ? cellData.toString() : "";
                        row.append("\"").append(value.replace("\"", "\"\"")).append("\"");
                        if (i < tableView.getColumns().size() - 1) row.append(",");
                    }
                    writer.write(row.toString());
                    writer.newLine();
                }

                Alerts.showAlert("Sucesso", null, "Relatório exportado com sucesso!", javafx.scene.control.Alert.AlertType.INFORMATION);
            } catch (IOException e) {
                Alerts.showAlert("Erro", "Falha ao exportar", e.getMessage(), javafx.scene.control.Alert.AlertType.ERROR);
            }
        }
    }
}