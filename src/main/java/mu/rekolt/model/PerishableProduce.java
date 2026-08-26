package mu.rekolt.model;

public class PerishableProduce extends Produce {
    public PerishableProduce(String code, String name, double basePricePerKg) {
        super(code, name, basePricePerKg);
    }

    @Override
    public double getCategoryMultiplier() {
        return 0.90;
    }
}
