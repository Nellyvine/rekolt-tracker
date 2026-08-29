package mu.rekolt.service;

import mu.rekolt.model.Delivery;
import mu.rekolt.model.Member;
import mu.rekolt.model.Grade;
import mu.rekolt.util.Formatter;
import org.apache.poi.xwpf.usermodel.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

public class DocumentService {

    private static final String OUTPUT_DIR = "output";
    private static final String REPORT_PATH = OUTPUT_DIR + "/season-report.docx";
    private static final String LOG_PATH = OUTPUT_DIR + "/run-log.txt";

    public void generateSeasonReport(SeasonService season) throws ReportGenerationException {
        ensureOutputDirectoryExists();

        try (XWPFDocument document = new XWPFDocument()) {
            addTitle(document);

            Set<String> memberIds = season.getMemberIds();
            boolean first = true;
            for (String memberId : memberIds) {
                if (!first) {
                    addPageBreak(document);
                }
                addMemberSection(document, season, memberId);
                first = false;
            }

            addPageBreak(document);
            addSeasonTotals(document, season);

            try (OutputStream out = new FileOutputStream(REPORT_PATH)) {
                document.write(out);
            }

        } catch (IOException e) {
            throw new ReportGenerationException(
                    "Could not write the report to " + REPORT_PATH +
                            ". Check that the 'output' folder exists, is writable, and the file isn't open in Word.",
                    e);
        }

        appendRunLog(season.getMemberIds().size());
    }

    private void ensureOutputDirectoryExists() throws ReportGenerationException {
        Path dir = Paths.get(OUTPUT_DIR);
        try {
            if (!Files.exists(dir)) {
                Files.createDirectories(dir);
            }
        } catch (IOException e) {
            throw new ReportGenerationException(
                    "Could not create the 'output' folder. Check your folder permissions.", e);
        }
    }

    private void addTitle(XWPFDocument document) {
        XWPFParagraph title = document.createParagraph();
        title.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun run = title.createRun();
        run.setText("REKOLT Planters' Cooperative - Season 2026 Payment Statements");
        run.setBold(true);
        run.setFontSize(16);
    }

    private void addPageBreak(XWPFDocument document) {
        XWPFParagraph paragraph = document.createParagraph();
        XWPFRun run = paragraph.createRun();
        run.addBreak(BreakType.PAGE);
    }

    private void addMemberSection(XWPFDocument document, SeasonService season, String memberId) {
        Member member = season.getMembers().get(memberId);
        List<Delivery> deliveries = season.getDeliveriesPerMember().get(memberId);
        double totalPayment = season.getTotalPaymentPerMember().get(memberId);

        XWPFParagraph heading = document.createParagraph();
        XWPFRun headingRun = heading.createRun();
        headingRun.setText("Member: " + member.getId() + " - " + member.getName());
        headingRun.setBold(true);
        headingRun.setFontSize(14);

        XWPFTable table = document.createTable(deliveries.size() + 1, 6);
        String[] headers = {"Delivery ID", "Produce", "Mass (kg)", "Grade", "Week", "Net Payable (MUR)"};
        for (int col = 0; col < headers.length; col++) {
            table.getRow(0).getCell(col).setText(headers[col]);
        }

        double commissionTotal = 0;
        double levyTotal = 0;

        for (int i = 0; i < deliveries.size(); i++) {
            Delivery d = deliveries.get(i);
            XWPFTableRow row = table.getRow(i + 1);
            row.getCell(0).setText(d.getId());
            row.getCell(1).setText(d.getProduce().getCode());
            row.getCell(2).setText(String.format("%.1f", d.getMassKg()));
            row.getCell(3).setText(d.getGrade().toString());
            row.getCell(4).setText(String.valueOf(d.getWeek()));
            row.getCell(5).setText(Formatter.money(d.calculateNetPayable()));

            if (d.getGrade() != Grade.REJECT) {
                commissionTotal += d.getCommission();
                levyTotal += d.getTransportLevy();
            }
        }

        document.createParagraph().createRun()
                .setText("Total commission deducted: " + Formatter.money(commissionTotal) + " MUR");
        document.createParagraph().createRun()
                .setText("Total transport levy deducted: " + Formatter.money(levyTotal) + " MUR");

        XWPFParagraph netPara = document.createParagraph();
        XWPFRun netRun = netPara.createRun();
        netRun.setText("NET PAYABLE: " + Formatter.money(totalPayment) + " MUR");
        netRun.setBold(true);
        netRun.setFontSize(13);

        document.createParagraph().createRun().addBreak();

        document.createParagraph().createRun()
                .setText("Signature: ______________________________     Date: ____________");
    }

    private void addSeasonTotals(XWPFDocument document, SeasonService season) {
        XWPFParagraph heading = document.createParagraph();
        XWPFRun headingRun = heading.createRun();
        headingRun.setText("Season Totals");
        headingRun.setBold(true);
        headingRun.setFontSize(14);

        double grandTotal = 0;
        for (double v : season.getTotalPaymentPerMember().values()) {
            grandTotal += Math.round(v * 100.0) / 100.0;
        }

        document.createParagraph().createRun()
                .setText("Number of members: " + season.getMemberIds().size());
        document.createParagraph().createRun()
                .setText("Number of deliveries: " + season.getDeliveries().size());

        XWPFParagraph totalPara = document.createParagraph();
        XWPFRun totalRun = totalPara.createRun();
        totalRun.setText("TOTAL SEASON PAYOUT: " + Formatter.money(grandTotal) + " MUR");
        totalRun.setBold(true);
        totalRun.setFontSize(13);
    }

    private void appendRunLog(int memberCount) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String line = timestamp + " - Season report generated with " + memberCount + " member sections." + System.lineSeparator();
        try (var writer = Files.newBufferedWriter(Paths.get(LOG_PATH),
                StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            writer.write(line);
        } catch (IOException e) {
            System.out.println("Warning: could not write to run-log.txt (" + e.getMessage() + ")");
        }
    }
}