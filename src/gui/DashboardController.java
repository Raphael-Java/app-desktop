package gui;

import java.net.URL;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import model.entities.Department;
import model.entities.Seller;
import model.services.DepartmentService;
import model.services.SellerService;

public class DashboardController implements Initializable {

    @FXML
    private Label lblTotalSellers;

    @FXML
    private Label lblTotalDepartments;

    @FXML
    private Label lblTotalPayroll;

    @FXML
    private Label lblTicketMedio;

    @FXML
    private Label lblRoiEquipe;

    @FXML
    private PieChart pieChartDepartments;

    @FXML
    private BarChart<String, Number> barChartSalaries;

    @FXML
    private LineChart<String, Number> lineChartSalesHistory;

    private SellerService sellerService = new SellerService();
    private DepartmentService departmentService = new DepartmentService();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadDashboardData();
    }

    public void loadDashboardData() {
        try {
            if (sellerService == null || departmentService == null) {
                return;
            }

            List<Seller> sellers = sellerService.findAll();
            List<Department> departments = departmentService.findAll();

            if (sellers == null || departments == null) {
                return;
            }

            int totalSellers = sellers.size();
            int totalDepartments = departments.size();

            // 1. Atualiza KPIs Padrão
            if (lblTotalSellers != null) {
                lblTotalSellers.setText(String.valueOf(totalSellers));
            }
            if (lblTotalDepartments != null) {
                lblTotalDepartments.setText(String.valueOf(totalDepartments));
            }

            double totalPayroll = sellers.stream()
                    .filter(Objects::nonNull)
                    .mapToDouble(s -> s.getBaseSalary() != null ? s.getBaseSalary() : 0.0)
                    .sum();
            
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

            if (lblTotalPayroll != null) {
                lblTotalPayroll.setText(currencyFormat.format(totalPayroll));
            }

            // 2. Cálculo dos KPIs de Faturamento
            double totalSales = 180000.00; 

            if (lblTicketMedio != null) {
                double ticketMedio = totalSellers > 0 ? totalSales / totalSellers : 0.0;
                lblTicketMedio.setText(currencyFormat.format(ticketMedio));
            }

            if (lblRoiEquipe != null) {
                double roi = totalPayroll > 0 ? totalSales / totalPayroll : 0.0;
                lblRoiEquipe.setText(String.format(Locale.US, "%.1fx", roi));
            }

            // 3. Preenche o Gráfico de Pizza
            Map<String, Long> sellersPerDep = sellers.stream()
                    .filter(s -> s != null && s.getDepartment() != null && s.getDepartment().getName() != null)
                    .collect(Collectors.groupingBy(s -> s.getDepartment().getName(), Collectors.counting()));

            if (pieChartDepartments != null) {
                ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
                sellersPerDep.forEach((depName, count) -> pieData.add(new PieChart.Data(depName, count)));
                pieChartDepartments.setData(pieData);
            }

            // 4. Preenche o Gráfico de Barras (Média Salarial)
            Map<String, Double> avgSalaryPerDep = sellers.stream()
                    .filter(s -> s != null && s.getDepartment() != null && s.getDepartment().getName() != null)
                    .collect(Collectors.groupingBy(
                            s -> s.getDepartment().getName(),
                            Collectors.averagingDouble(s -> s.getBaseSalary() != null ? s.getBaseSalary() : 0.0)
                    ));

            if (barChartSalaries != null) {
                barChartSalaries.getData().clear();
                XYChart.Series<String, Number> series = new XYChart.Series<>();
                series.setName("Média Salarial");

                avgSalaryPerDep.forEach((depName, avgSalary) -> {
                    series.getData().add(new XYChart.Data<>(depName, avgSalary));
                });

                barChartSalaries.getData().add(series);
            }

            // 5. Preenche o Gráfico de Linhas (Evolução do Faturamento)
            loadSalesHistoryData();

        } catch (Exception e) {
            System.err.println("Erro ao carregar dados do Dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadSalesHistoryData() {
        if (lineChartSalesHistory != null) {
            lineChartSalesHistory.getData().clear();

            XYChart.Series<String, Number> salesSeries = new XYChart.Series<>();
            salesSeries.setName("Faturamento (R$)");

            // Exemplo de histórico mensal (no futuro, integre com a sua tabela de Vendas/Sales)
            salesSeries.getData().add(new XYChart.Data<>("Jan", 110000.00));
            salesSeries.getData().add(new XYChart.Data<>("Fev", 125000.00));
            salesSeries.getData().add(new XYChart.Data<>("Mar", 140000.00));
            salesSeries.getData().add(new XYChart.Data<>("Abr", 135000.00));
            salesSeries.getData().add(new XYChart.Data<>("Mai", 155000.00));
            salesSeries.getData().add(new XYChart.Data<>("Jun", 150000.00));
            salesSeries.getData().add(new XYChart.Data<>("Jul", 168000.00));
            salesSeries.getData().add(new XYChart.Data<>("Ago", 175000.00));
            salesSeries.getData().add(new XYChart.Data<>("Set", 180000.00));

            lineChartSalesHistory.getData().add(salesSeries);
        }
    }
}