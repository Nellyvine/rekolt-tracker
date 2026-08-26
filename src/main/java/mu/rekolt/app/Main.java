package mu.rekolt.app;

import mu.rekolt.model.*;
import mu.rekolt.service.ProduceCatalog;
import mu.rekolt.service.SeasonService;
import mu.rekolt.util.ConsoleInput;
import mu.rekolt.util.Formatter;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ConsoleInput input = new ConsoleInput(scanner);
        ProduceCatalog catalog = new ProduceCatalog();
        SeasonService season = new SeasonService();

        boolean running = true;
        while (running) {
            System.out.println();
            System.out.println("REKOLT PRODUCE TRACKER  -  season 2026");
            System.out.println("1. Record a delivery          3. Generate the season report");
            System.out.println("2. Season figures on screen   4. Exit");
            System.out.println();

            int choice = input.readMenuOption();

            switch (choice) {
                case 1:
                    recordDelivery(input, catalog, season);
                    break;
                case 2:
                    printSeasonFigures(season);
                    break;
                case 3:
                    System.out.println("(Word report generation is next on our list.)");
                    break;
                case 4:
                    System.out.println("Goodbye.");
                    running = false;
                    break;
            }
        }
    }

    private static void recordDelivery(ConsoleInput input, ProduceCatalog catalog, SeasonService season) {
        String memberId = input.readMemberId();
        String memberName = input.readName();
        String produceCode = input.readProduceCode();
        double massKg = input.readMass();
        int qualityScore = input.readQualityScore();
        int week = input.readWeek();

        Member member = new Member(memberId, memberName);
        Produce produce = catalog.get(produceCode);
        Delivery delivery = new Delivery(member, produce, massKg, qualityScore, week);

        season.recordDelivery(delivery);

        System.out.println();
        System.out.println("Delivery " + delivery.getId() + " recorded.  Grade " + delivery.getGrade());
        System.out.printf("  NET PAYABLE                          = %s MUR%n", Formatter.money(delivery.calculateNetPayable()));
    }

    private static void printSeasonFigures(SeasonService season) {
        System.out.println();
        System.out.println("Total payment per member (MUR)");
        for (Map.Entry<String, Double> entry : season.getTotalPaymentPerMember().entrySet()) {
            Member member = season.getMembers().get(entry.getKey());
            System.out.printf("  %s  %-20s  %s%n", entry.getKey(), member.getName(), Formatter.money(entry.getValue()));
        }

        System.out.println();
        System.out.println("Weekly volume grid (kg)");
        String[] codes = season.getProduceOrder();
        System.out.printf("%6s", "Week");
        for (String code : codes) System.out.printf("%9s", code);
        System.out.printf("%9s%n", "Total");

        double[][] grid = season.getWeeklyGrid();
        for (int week = 0; week < grid.length; week++) {
            double rowTotal = 0;
            boolean hasData = false;
            for (int col = 0; col < grid[week].length; col++) {
                if (grid[week][col] > 0) hasData = true;
                rowTotal += grid[week][col];
            }
            if (!hasData) continue;
            System.out.printf("%6d", week + 1);
            for (int col = 0; col < grid[week].length; col++) System.out.printf("%9.1f", grid[week][col]);
            System.out.printf("%9.1f%n", rowTotal);
        }

        System.out.println();
        System.out.println("Top five deliveries by value");
        List<Delivery> top = season.topDeliveriesByValue(5);
        int rank = 1;
        for (Delivery d : top) {
            System.out.println("  " + rank + ". " + d.toReportLine());
            rank++;
        }
    }
}