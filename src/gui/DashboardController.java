package gui;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
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
    private PieChart pieChartDepartments;

    @FXML
    private BarChart<String, Number> barChartSalaries;

    private SellerService sellerService = new SellerService();
    private DepartmentService departmentService = new DepartmentService();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Carrega automaticamente ao inicializar a View
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

            // 1. Atualiza os Cards de KPIs
            if (lblTotalSellers != null) {
                lblTotalSellers.setText(String.valueOf(sellers.size()));
            }
            if (lblTotalDepartments != null) {
                lblTotalDepartments.setText(String.valueOf(departments.size()));
            }

            double totalPayroll = sellers.stream()
                    .filter(Objects::nonNull)
                    .mapToDouble(s -> s.getBaseSalary() != null ? s.getBaseSalary() : 0.0)
                    .sum();
            
            if (lblTotalPayroll != null) {
                lblTotalPayroll.setText(String.format("R$ %.2f", totalPayroll));
            }

            // 2. Preenche o Gráfico de Pizza (Vendedores por Departamento) Protegido contra NULL
            Map<String, Long> sellersPerDep = sellers.stream()
                    .filter(s -> s != null && s.getDepartment() != null && s.getDepartment().getName() != null)
                    .collect(Collectors.groupingBy(s -> s.getDepartment().getName(), Collectors.counting()));

            if (pieChartDepartments != null) {
                ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
                sellersPerDep.forEach((depName, count) -> pieData.add(new PieChart.Data(depName, count)));
                pieChartDepartments.setData(pieData);
            }

            // 3. Preenche o Gráfico de Barras (Média Salarial por Departamento) Protegido contra NULL
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

        } catch (Exception e) {
            System.err.println("Erro ao carregar dados do Dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }
}