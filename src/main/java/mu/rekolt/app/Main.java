package mu.rekolt.app;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        String memberId = readValidMemberId(scanner);
        String memberName = readValidName(scanner);
        String produceCode = readValidProduceCode(scanner);
        double massKg = readValidMass(scanner);
        int qualityScore = readValidQualityScore(scanner);
        int week = readValidWeek(scanner);

        double basePricePerKg = getBasePrice(produceCode);      // switch statement
        String grade = getGrade(qualityScore);                   // if/else-if
        double gradeMultiplier = getGradeMultiplier(qualityScore);
        double categoryMultiplier = getCategoryMultiplier(produceCode);

        double baseValue = massKg * basePricePerKg;
        double afterGrade = baseValue * gradeMultiplier;
        double afterCategory = afterGrade * categoryMultiplier;
        double commission = afterCategory * 0.05;
        double transportLevy = massKg * 2.0;
        double netPayable = afterCategory - commission - transportLevy;

        System.out.println();
        System.out.println("Delivery recorded for " + memberId + " (" + memberName + ")  Grade " + grade + ", week " + week);
        System.out.printf("  Base value        %s%n", format(baseValue));
        System.out.printf("  After grade                    = %s%n", format(afterGrade));
        System.out.printf("  After category                 = %s%n", format(afterCategory));
        System.out.printf("  Commission 5%%                  - %s%n", format(commission));
        System.out.printf("  Transport levy                 - %s%n", format(transportLevy));
        System.out.printf("  NET PAYABLE                    = %s MUR%n", format(netPayable));
    }

    private static String format(double value) {
        return String.format("%,.2f", value);
    }

    // ---------- validated input methods ----------

    private static String readValidMemberId(Scanner scanner) {
        while (true) {
            System.out.print("Member identifier              : ");
            String input = scanner.nextLine().trim();
            if (input.matches("^M-\\d{4}$")) {
                return input;
            }
            System.out.println("  Member ID must look like M-0042. Please try again.");
        }
    }

    private static String readValidName(Scanner scanner) {
        while (true) {
            System.out.print("Member name                    : ");
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            }
            System.out.println("  Name cannot be empty. Please try again.");
        }
    }

    private static String readValidProduceCode(Scanner scanner) {
        while (true) {
            System.out.print("Produce code (MZE/BNS/POT/TEA) : ");
            String input = scanner.nextLine().trim().toUpperCase();
            if (input.equals("MZE") || input.equals("BNS") || input.equals("POT") || input.equals("TEA")) {
                return input;
            }
            System.out.println("  Produce code must be MZE, BNS, POT or TEA. Please try again.");
        }
    }

    private static double readValidMass(Scanner scanner) {
        while (true) {
            System.out.print("Mass in kg                     : ");
            String input = scanner.nextLine();
            try {
                double mass = Double.parseDouble(input);
                if (mass > 0 && mass <= 5000) {
                    return mass;
                }
                System.out.println("  Mass must be above 0 and not more than 5000. Please try again.");
            } catch (NumberFormatException e) {
                System.out.println("  That's not a valid number. Please try again.");
            }
        }
    }

    private static int readValidQualityScore(Scanner scanner) {
        while (true) {
            System.out.print("Quality score (0-100)          : ");
            String input = scanner.nextLine();
            try {
                int score = Integer.parseInt(input);
                if (score >= 0 && score <= 100) {
                    return score;
                }
                System.out.println("  Quality score must be between 0 and 100. Please try again.");
            } catch (NumberFormatException e) {
                System.out.println("  That's not a whole number. Please try again.");
            }
        }
    }

    private static int readValidWeek(Scanner scanner) {
        while (true) {
            System.out.print("Week of delivery (1-20)        : ");
            String input = scanner.nextLine();
            try {
                int week = Integer.parseInt(input);
                if (week >= 1 && week <= 20) {
                    return week;
                }
                System.out.println("  Week must be between 1 and 20. Please try again.");
            } catch (NumberFormatException e) {
                System.out.println("  That's not a whole number. Please try again.");
            }
        }
    }

    // ---------- grading and pricing ----------

    // if/else-if: grade boundaries
    private static String getGrade(int qualityScore) {
        if (qualityScore >= 85) {
            return "A";
        } else if (qualityScore >= 70) {
            return "B";
        } else if (qualityScore >= 50) {
            return "C";
        } else {
            return "REJECT";
        }
    }

    private static double getGradeMultiplier(int qualityScore) {
        if (qualityScore >= 85) {
            return 1.15;
        } else if (qualityScore >= 70) {
            return 1.00;
        } else if (qualityScore >= 50) {
            return 0.85;
        } else {
            return 0.00;
        }
    }

    // switch: produce code -> base price
    private static double getBasePrice(String produceCode) {
        switch (produceCode) {
            case "MZE":
                return 30.0;
            case "BNS":
                return 90.0;
            case "POT":
                return 45.0;
            case "TEA":
                return 25.0;
            default:
                throw new IllegalArgumentException("Unknown produce code: " + produceCode);
        }
    }

    // switch: produce code -> category multiplier
    private static double getCategoryMultiplier(String produceCode) {
        switch (produceCode) {
            case "MZE":
            case "BNS":
                return 1.00; // cereal
            case "POT":
                return 0.90; // perishable
            case "TEA":
                return 1.10; // cash crop
            default:
                throw new IllegalArgumentException("Unknown produce code: " + produceCode);
        }
    }
}