package gui.util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

import application.Main;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;
import javafx.stage.Window;

public class PdfExporter {

    public static <T> void exportToPDF(TableView<T> tableView, String title, String defaultFileName) {
        if (tableView == null || tableView.getItems().isEmpty()) {
            Alerts.showAlert("Aviso", "Sem dados para exportar", "A tabela selecionada está vazia ou ainda não foi carregada.", AlertType.WARNING);
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Salvar Relatório PDF");
        fileChooser.setInitialFileName(defaultFileName + ".pdf");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Arquivos PDF (*.pdf)", "*.pdf"));

        // Obtém a janela ativa da cena principal em vez de instanciar um new Stage()
        Window parentWindow = Main.getMainScene() != null ? Main.getMainScene().getWindow() : null;
        File file = fileChooser.showSaveDialog(parentWindow);

        if (file != null) {
            try {
                PdfWriter writer = new PdfWriter(file);
                PdfDocument pdf = new PdfDocument(writer);
                Document document = new Document(pdf, PageSize.A4);
                document.setMargins(20, 20, 20, 20);

                // 1. Cabeçalho do Documento
                Paragraph header = new Paragraph(title.toUpperCase())
                        .setFontSize(18)
                        .setBold()
                        .setFontColor(new DeviceRgb(44, 62, 80))
                        .setTextAlignment(TextAlignment.CENTER);
                document.add(header);

                // Data de geração
                String dateStr = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date());
                Paragraph subHeader = new Paragraph("Gerado em: " + dateStr)
                        .setFontSize(10)
                        .setFontColor(new DeviceRgb(127, 140, 141))
                        .setTextAlignment(TextAlignment.CENTER)
                        .setMarginBottom(15);
                document.add(subHeader);

                // 2. Criando a Tabela PDF dinamicamente
                int colCount = tableView.getColumns().size();
                Table pdfTable = new Table(UnitValue.createPercentArray(colCount)).useAllAvailableWidth();

                // Cores estilizadas
                DeviceRgb headerBg = new DeviceRgb(41, 128, 185); // Azul
                DeviceRgb zebraBg = new DeviceRgb(245, 247, 250);  // Cinza claro

                // Escreve os Títulos das Colunas
                for (TableColumn<T, ?> col : tableView.getColumns()) {
                    Cell cell = new Cell()
                            .add(new Paragraph(col.getText()).setBold().setFontColor(new DeviceRgb(255, 255, 255)))
                            .setBackgroundColor(headerBg)
                            .setTextAlignment(TextAlignment.CENTER)
                            .setPadding(6);
                    pdfTable.addHeaderCell(cell);
                }

                // Escreve as Linhas dos Dados (com efeito Zebrado)
                int rowIndex = 0;
                for (T item : tableView.getItems()) {
                    DeviceRgb currentBg = (rowIndex % 2 == 0) ? new DeviceRgb(255, 255, 255) : zebraBg;
                    
                    for (TableColumn<T, ?> col : tableView.getColumns()) {
                        Object cellData = col.getCellData(item);
                        String value = cellData != null ? cellData.toString() : "";

                        Cell cell = new Cell()
                                .add(new Paragraph(value).setFontSize(9))
                                .setBackgroundColor(currentBg)
                                .setPadding(5);
                        pdfTable.addCell(cell);
                    }
                    rowIndex++;
                }

                document.add(pdfTable);
                document.close();
                pdf.close();

                Alerts.showAlert("Sucesso", null, "Relatório em PDF gerado com sucesso!", AlertType.INFORMATION);

            } catch (Exception e) {
                Alerts.showAlert("Erro", "Falha ao gerar PDF", e.getMessage(), AlertType.ERROR);
                e.printStackTrace();
            }
        }
    }
}