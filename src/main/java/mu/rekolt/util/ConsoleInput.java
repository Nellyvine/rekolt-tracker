package mu.rekolt.util;

import java.util.Scanner;

public class ConsoleInput {
    private final Scanner scanner;

    public ConsoleInput(Scanner scanner) {
        this.scanner = scanner;
    }

    public String readMemberId() {
        while (true) {
            System.out.print("Member identifier              : ");
            String input = scanner.nextLine().trim();
            if (input.matches("^M-\\d{4}$")) return input;
            System.out.println("  Member ID must look like M-0042. Please try again.");
        }
    }

    public String readName() {
        while (true) {
            System.out.print("Member name                    : ");
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) return input;
            System.out.println("  Name cannot be empty. Please try again.");
        }
    }

    public String readProduceCode() {
        while (true) {
            System.out.print("Produce code (MZE/BNS/POT/TEA) : ");
            String input = scanner.nextLine().trim().toUpperCase();
            if (input.equals("MZE") || input.equals("BNS") || input.equals("POT") || input.equals("TEA")) return input;
            System.out.println("  Produce code must be MZE, BNS, POT or TEA. Please try again.");
        }
    }

    public double readMass() {
        while (true) {
            System.out.print("Mass in kg                     : ");
            String input = scanner.nextLine();
            try {
                double mass = Double.parseDouble(input);
                if (mass > 0 && mass <= 5000) return mass;
                System.out.println("  Mass must be above 0 and not more than 5000. Please try again.");
            } catch (NumberFormatException e) {
                System.out.println("  That's not a valid number. Please try again.");
            }
        }
    }

    public int readQualityScore() {
        while (true) {
            System.out.print("Quality score (0-100)          : ");
            String input = scanner.nextLine();
            try {
                int score = Integer.parseInt(input);
                if (score >= 0 && score <= 100) return score;
                System.out.println("  Quality score must be between 0 and 100. Please try again.");
            } catch (NumberFormatException e) {
                System.out.println("  That's not a whole number. Please try again.");
            }
        }
    }

    public int readWeek() {
        while (true) {
            System.out.print("Week of delivery (1-20)        : ");
            String input = scanner.nextLine();
            try {
                int week = Integer.parseInt(input);
                if (week >= 1 && week <= 20) return week;
                System.out.println("  Week must be between 1 and 20. Please try again.");
            } catch (NumberFormatException e) {
                System.out.println("  That's not a whole number. Please try again.");
            }
        }
    }

    public int readMenuOption() {
        while (true) {
            System.out.print("Choose an option: ");
            String input = scanner.nextLine().trim();
            try {
                int choice = Integer.parseInt(input);
                if (choice >= 1 && choice <= 4) return choice;
                System.out.println("  Please enter a number from 1 to 4.");
            } catch (NumberFormatException e) {
                System.out.println("  Please enter a number from 1 to 4.");
            }
        }
    }
}