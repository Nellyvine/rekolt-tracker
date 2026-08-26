package mu.rekolt.model;

public abstract class Produce {
    private final String code;
    private final String name;
    private final double basePricePerKg;

    protected Produce(String code, String name, double basePricePerKg) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Produce code cannot be empty");
        }
        if (basePricePerKg <= 0) {
            throw new IllegalArgumentException("Base price must be positive");
        }
        this.code = code;
        this.name = name;
        this.basePricePerKg = basePricePerKg;
    }

    public String getCode() { return code; }
    public String getName() { return name; }
    public double getBasePricePerKg() { return basePricePerKg; }

    public abstract double getCategoryMultiplier();

    public double valuate(double massKg, Grade grade) {
        double baseValue = massKg * basePricePerKg;
        double afterGrade = baseValue * grade.getMultiplier();
        return afterGrade * getCategoryMultiplier();
    }

    @Override
    public String toString() {
        return code + " (" + name + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return code.equals(((Produce) o).code);
    }

    @Override
    public int hashCode() {
        return code.hashCode();
    }

}
