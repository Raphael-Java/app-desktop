package gui.util;

import java.io.File;
import java.io.IOException;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class DataBackup {

    // Configurações do seu banco de dados
    private static final String DB_NAME = "db_sistema"; // Altere para o nome do seu banco
    private static final String DB_USER = "root";       // Altere para seu usuário
    private static final String DB_PASS = "1234";       // Altere para sua senha

    /**
     * Exporta o banco de dados para um arquivo .sql
     */
    public static void executeBackup() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Salvar Backup do Banco de Dados");
        fileChooser.setInitialFileName("backup_" + DB_NAME + ".sql");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Arquivos SQL (*.sql)", "*.sql"));

        File file = fileChooser.showSaveDialog(new Stage());

        if (file != null) {
            // Comando do mysqldump (garanta que o mysqldump esteja no PATH do sistema)
            String command = String.format("mysqldump -u%s -p%s %s -r \"%s\"", 
                    DB_USER, DB_PASS, DB_NAME, file.getAbsolutePath());

            try {
                Process process = Runtime.getRuntime().exec(command);
                int processComplete = process.waitFor();

                if (processComplete == 0) {
                    Alerts.showAlert("Sucesso", null, "Backup realizado com sucesso em:\n" + file.getAbsolutePath(), AlertType.INFORMATION);
                } else {
                    Alerts.showAlert("Erro", null, "Falha ao gerar o backup. Verifique as credenciais do banco.", AlertType.ERROR);
                }
            } catch (IOException | InterruptedException e) {
                Alerts.showAlert("Erro", "Falha de Execução", e.getMessage(), AlertType.ERROR);
            }
        }
    }

    /**
     * Restaura o banco de dados a partir de um arquivo .sql
     */
    public static void restoreBackup() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecionar Arquivo de Backup para Restauração");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Arquivos SQL (*.sql)", "*.sql"));

        File file = fileChooser.showOpenDialog(new Stage());

        if (file != null) {
            // Comando para restaurar via cliente MySQL
            String[] command = new String[]{
                "mysql",
                "-u" + DB_USER,
                "-p" + DB_PASS,
                DB_NAME,
                "-e",
                "source " + file.getAbsolutePath().replace("\\", "/")
            };

            try {
                Process process = Runtime.getRuntime().exec(command);
                int processComplete = process.waitFor();

                if (processComplete == 0) {
                    Alerts.showAlert("Sucesso", null, "Banco de dados restaurado com sucesso!", AlertType.INFORMATION);
                } else {
                    Alerts.showAlert("Erro", null, "Falha ao restaurar o banco de dados.", AlertType.ERROR);
                }
            } catch (IOException | InterruptedException e) {
                Alerts.showAlert("Erro", "Falha de Execução", e.getMessage(), AlertType.ERROR);
            }
        }
    }
}