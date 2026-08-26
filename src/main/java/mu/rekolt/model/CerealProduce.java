package mu.rekolt.model;

public class CerealProduce extends Produce {
    public CerealProduce(String code, String name, double basePricePerKg) {
        super(code, name, basePricePerKg);
    }

    @Override
    public double getCategoryMultiplier() {
        return 1.00;
    }
}
