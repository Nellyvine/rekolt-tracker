package mu.rekolt.model;

public class CashCropProduce extends Produce{
    public CashCropProduce(String code, String name, double basePricePerKg) {
        super(code, name, basePricePerKg);
    }

    @Override
    public double getCategoryMultiplier() {
        return 1.10;
    }
}
