package mu.rekolt.app;

public class Main {
    public static void main(String[] args) {
        double massKg = 236;
        int qualityScore = 91;
        double basePricePerKg = 90.0;

        double baseValue = massKg * basePricePerKg;

        double gradeMultiplier;
        if (qualityScore >= 85) {
            gradeMultiplier = 1.15;
        } else if (qualityScore >= 70) {
            gradeMultiplier = 1.00;
        } else if (qualityScore >= 50) {
            gradeMultiplier = 0.85;
        } else {
            gradeMultiplier = 0.00;
        }
        double afterGrade = baseValue * gradeMultiplier;
        double afterCategory = afterGrade * 1.00;
        double commission = afterCategory * 0.05;
        double transportLevy = massKg * 2.0;
        double netPayable = afterCategory - commission - transportLevy;

        System.out.printf("Base value     : %,.2f MUR%n", baseValue);
        System.out.printf("After grade    : %,.2f MUR%n", afterGrade);
        System.out.printf("After category : %,.2f MUR%n", afterCategory);
        System.out.printf("Commission     : %,.2f MUR%n", commission);
        System.out.printf("Transport levy : %,.2f MUR%n", transportLevy);
        System.out.printf("NET PAYABLE    : %,.2f MUR%n", netPayable);
    }
}