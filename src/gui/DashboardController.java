package gui;

import java.net.URL;
import java.util.List;
import java.util.Map;
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
        loadDashboardData();
    }

    public void loadDashboardData() {
        List<Seller> sellers = sellerService.findAll();
        List<Department> departments = departmentService.findAll();

        // 1. Atualiza os Cards de KPIs
        lblTotalSellers.setText(String.valueOf(sellers.size()));
        lblTotalDepartments.setText(String.valueOf(departments.size()));

        double totalPayroll = sellers.stream()
                .mapToDouble(Seller::getBaseSalary)
                .sum();
        lblTotalPayroll.setText(String.format("R$ %.2f", totalPayroll));

        // 2. Preenche o Gráfico de Pizza (Vendedores por Departamento)
        Map<String, Long> sellersPerDep = sellers.stream()
                .collect(Collectors.groupingBy(s -> s.getDepartment().getName(), Collectors.counting()));

        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
        sellersPerDep.forEach((depName, count) -> pieData.add(new PieChart.Data(depName, count)));
        pieChartDepartments.setData(pieData);

        // 3. Preenche o Gráfico de Barras (Média Salarial por Departamento)
        Map<String, Double> avgSalaryPerDep = sellers.stream()
                .collect(Collectors.groupingBy(
                        s -> s.getDepartment().getName(),
                        Collectors.averagingDouble(Seller::getBaseSalary)
                ));

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Média Salarial");

        avgSalaryPerDep.forEach((depName, avgSalary) -> {
            series.getData().add(new XYChart.Data<>(depName, avgSalary));
        });

        barChartSalaries.getData().clear();
        barChartSalaries.getData().add(series);
    }
}