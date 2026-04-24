package util;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PDFGenerator {

    private static final float MARGIN = 50;
    private static final float LINE_HEIGHT = 20;

    public static byte[] generateReceipt(String studentName, int studentId,
                                         double amount, String date,
                                         String transactionId) throws Exception {

        PDDocument document = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);
        document.addPage(page);

        float pageWidth = page.getMediaBox().getWidth();
        float pageHeight = page.getMediaBox().getHeight();
        float y = pageHeight - MARGIN;

        PDPageContentStream content = new PDPageContentStream(document, page);

        // Header background
        content.setNonStrokingColor(33 / 255.0f, 37 / 255.0f, 41 / 255.0f);
        content.addRect(0, pageHeight - 120, pageWidth, 120);
        content.fill();

        // Header text
        content.setNonStrokingColor(255 / 255.0f, 255 / 255.0f, 255 / 255.0f);
        content.beginText();
        content.setFont(PDType1Font.HELVETICA_BOLD, 24);
        content.newLineAtOffset(MARGIN, y - 15);
        content.showText("PAYMENT RECEIPT");
        content.endText();

        content.beginText();
        content.setFont(PDType1Font.HELVETICA, 11);
        content.newLineAtOffset(MARGIN, y - 40);
        content.showText("Student Fee Payment System");
        content.endText();

        content.beginText();
        content.setFont(PDType1Font.HELVETICA, 10);
        content.newLineAtOffset(MARGIN, y - 58);
        content.showText("Generated: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        content.endText();

        // Accent line
        y = pageHeight - 130;
        content.setNonStrokingColor(67 / 255.0f, 97 / 255.0f, 238 / 255.0f);
        content.addRect(MARGIN, y, pageWidth - 2 * MARGIN, 3);
        content.fill();

        // Transaction title
        y -= 40;
        content.setNonStrokingColor(33 / 255.0f, 37 / 255.0f, 41 / 255.0f);

        content.beginText();
        content.setFont(PDType1Font.HELVETICA_BOLD, 14);
        content.newLineAtOffset(MARGIN, y);
        content.showText("Transaction Details");
        content.endText();

        y -= 10;
        content.setNonStrokingColor(200 / 255.0f, 200 / 255.0f, 200 / 255.0f);
        content.addRect(MARGIN, y, pageWidth - 2 * MARGIN, 0.5f);
        content.fill();

        // Details
        content.setNonStrokingColor(33 / 255.0f, 37 / 255.0f, 41 / 255.0f);
        y -= 30;
        drawRow(content, MARGIN, y, "Transaction ID:", transactionId);
        y -= LINE_HEIGHT + 8;
        drawRow(content, MARGIN, y, "Date:", date);
        y -= LINE_HEIGHT + 8;
        drawRow(content, MARGIN, y, "Student Name:", studentName);
        y -= LINE_HEIGHT + 8;
        drawRow(content, MARGIN, y, "Student ID:", String.valueOf(studentId));
        y -= LINE_HEIGHT + 8;

        // Amount box
        y -= 15;
        content.setNonStrokingColor(240 / 255.0f, 245 / 255.0f, 255 / 255.0f);
        content.addRect(MARGIN, y - 15, pageWidth - 2 * MARGIN, 45);
        content.fill();

        content.setNonStrokingColor(33 / 255.0f, 37 / 255.0f, 41 / 255.0f);
        content.beginText();
        content.setFont(PDType1Font.HELVETICA_BOLD, 13);
        content.newLineAtOffset(MARGIN + 15, y + 5);
        content.showText("Amount Paid:");
        content.endText();

        content.setNonStrokingColor(67 / 255.0f, 97 / 255.0f, 238 / 255.0f);
        content.beginText();
        content.setFont(PDType1Font.HELVETICA_BOLD, 18);
        content.newLineAtOffset(pageWidth - MARGIN - 150, y + 3);
        content.showText("$" + String.format("%.2f", amount));
        content.endText();

        // Status
        y -= 50;
        content.setNonStrokingColor(6 / 255.0f, 214 / 255.0f, 160 / 255.0f);
        content.beginText();
        content.setFont(PDType1Font.HELVETICA_BOLD, 14);
        content.newLineAtOffset(MARGIN, y);
        content.showText("STATUS: PAID");
        content.endText();

        // Footer
        float footerY = MARGIN + 40;
        content.setNonStrokingColor(200 / 255.0f, 200 / 255.0f, 200 / 255.0f);
        content.addRect(MARGIN, footerY, pageWidth - 2 * MARGIN, 0.5f);
        content.fill();

        content.setNonStrokingColor(130 / 255.0f, 130 / 255.0f, 130 / 255.0f);
        content.beginText();
        content.setFont(PDType1Font.HELVETICA, 9);
        content.newLineAtOffset(MARGIN, footerY - 18);
        content.showText("This is a computer-generated receipt. No signature required.");
        content.endText();

        content.beginText();
        content.setFont(PDType1Font.HELVETICA, 9);
        content.newLineAtOffset(MARGIN, footerY - 33);
        content.showText("Student Fee Payment System - All Rights Reserved");
        content.endText();

        content.close();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        document.save(baos);
        document.close();

        return baos.toByteArray();
    }

    private static void drawRow(PDPageContentStream content, float x, float y,
                                String label, String value) throws Exception {

        content.beginText();
        content.setFont(PDType1Font.HELVETICA_BOLD, 11);
        content.newLineAtOffset(x, y);
        content.showText(label);
        content.endText();

        content.beginText();
        content.setFont(PDType1Font.HELVETICA, 11);
        content.newLineAtOffset(x + 160, y);
        content.showText(value);
        content.endText();
    }
}