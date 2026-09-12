package model.services;

public class ShippingService {

    /**
     * Calcula o valor do frete com base na região e peso.
     * Concede frete grátis para compras acima de R$ 250,00.
     */
    public double calculateShipping(String region, double weightKg, double totalAmount) {
        if (totalAmount >= 250.0) {
            return 0.0; // Frete Grátis
        }

        double baseRate;
        switch (region.toUpperCase()) {
            case "SUL":
            case "SUDESTE":
                baseRate = 15.00;
                break;
            case "CENTRO-OESTE":
                baseRate = 25.00;
                break;
            case "NORTE":
            case "NORDESTE":
                baseRate = 35.00;
                break;
            default:
                baseRate = 20.00;
                break;
        }

        // Adiciona R$ 2,50 por kg adicional acima de 1kg
        double extraWeightFee = 0.0;
        if (weightKg > 1.0) {
            extraWeightFee = (weightKg - 1.0) * 2.50;
        }

        return baseRate + extraWeightFee;
    }
}