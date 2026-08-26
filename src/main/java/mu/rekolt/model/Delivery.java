package mu.rekolt.model;

public class Delivery implements Payable, Reportable, Comparable<Delivery> {
    private static int counter = 1000;

    private final String id;
    private final Member member;
    private final Produce produce;
    private final double massKg;
    private final int qualityScore;
    private final int week;
    private final Grade grade;

    public Delivery(Member member, Produce produce, double massKg, int qualityScore, int week) {
        if (massKg <= 0 || massKg > 5000) {
            throw new IllegalArgumentException("Mass must be between 0 and 5000 kg");
        }
        if (qualityScore < 0 || qualityScore > 100) {
            throw new IllegalArgumentException("Quality score must be 0-100");
        }
        if (week < 1 || week > 20) {
            throw new IllegalArgumentException("Week must be 1-20");
        }
        this.id = "D-" + (++counter);
        this.member = member;
        this.produce = produce;
        this.massKg = massKg;
        this.qualityScore = qualityScore;
        this.week = week;
        this.grade = Grade.fromScore(qualityScore);
    }

    public String getId() { return id; }
    public Member getMember() { return member; }
    public Produce getProduce() { return produce; }
    public double getMassKg() { return massKg; }
    public int getQualityScore() { return qualityScore; }
    public int getWeek() { return week; }
    public Grade getGrade() { return grade; }

    public double getValueAfterCategory() {
        return produce.valuate(massKg, grade);
    }

    public double getCommission() {
        return getValueAfterCategory() * 0.05;
    }

    public double getTransportLevy() {
        return massKg * 2.0;
    }

    @Override
    public double calculateNetPayable() {
        if (grade == Grade.REJECT) {
            return 0.0;
        }
        return getValueAfterCategory() - getCommission() - getTransportLevy();
    }

    @Override
    public String toReportLine() {
        return String.format("%s  %s  %s  %.1f kg  %s  %,.2f",
                id, member.getId(), produce.getCode(), massKg, grade, calculateNetPayable());
    }

    @Override
    public int compareTo(Delivery other) {
        return this.id.compareTo(other.id);
    }

    @Override
    public String toString() {
        return toReportLine();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return id.equals(((Delivery) o).id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
