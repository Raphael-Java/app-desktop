package model.services;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class BalanceService {

    private static double balance = 15000.00;

    public static double getBalance() {
        return balance;
    }

    public static double paySalary(double salary) {

        if (salary > balance) {
            throw new IllegalArgumentException("Saldo insuficiente!");
        }

        balance -= salary;

        return balance;
    }

    public static void saveHistory(String employeeName,
                                   double salary,
                                   double oldBalance,
                                   double newBalance) {

        String path = System.getProperty("user.home") + "\\balance.txt";

        try (BufferedWriter bw =
                     new BufferedWriter(new FileWriter(path, true))) {

            bw.write("========== PAGAMENTO ==========");
            bw.newLine();
            bw.write("Funcionário: " + employeeName);
            bw.newLine();
            bw.write(String.format("Salário: R$ %.2f", salary));
            bw.newLine();
            bw.write(String.format("Saldo anterior: R$ %.2f", oldBalance));
            bw.newLine();
            bw.write(String.format("Saldo atualizado: R$ %.2f", newBalance));
            bw.newLine();
            bw.newLine();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void openFile() {

        String path = System.getProperty("user.home") + "\\balance.txt";

        try {
            java.awt.Desktop.getDesktop().open(new File(path));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}