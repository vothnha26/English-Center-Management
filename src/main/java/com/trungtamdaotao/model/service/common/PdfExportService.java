package com.trungtamdaotao.model.service.common;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import java.awt.*;
import java.io.FileOutputStream;
import java.util.List;

/**
 * Service xuất Phiếu điểm (Transcript) ra định dạng PDF.
 */
public class PdfExportService {

    public void exportTranscript(String filePath, String studentName, String className, String score, String grade, String comment) {
        Document document = new Document(PageSize.A5); // Khổ A5 cho gọn gàng
        try {
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            // Font Tiếng Việt (Sử dụng Font hệ thống hoặc mặc định)
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.decode("#FF6B35"));
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
            Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);

            // Header
            Paragraph title = new Paragraph("PHIEU KET QUA HOC TAP", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph("MIS ENGLISH CENTER", normalFont));
            document.add(new Chunk("\n"));

            // Student Info
            document.add(new Paragraph("Hoc vien: " + studentName, boldFont));
            document.add(new Paragraph("Lop hoc: " + className, normalFont));
            document.add(new Chunk("\n"));

            // Result Table
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            
            table.addCell(createCell("DIEM SO", true));
            table.addCell(createCell(score, false));
            
            table.addCell(createCell("XEP LOAI", true));
            table.addCell(createCell(grade, false));
            
            document.add(table);
            document.add(new Chunk("\n"));

            // Teacher Comment
            document.add(new Paragraph("Nhan xet cua giao vien:", boldFont));
            document.add(new Paragraph(comment, normalFont));

            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private PdfPCell createCell(String text, boolean isHeader) {
        PdfPCell cell = new PdfPCell(new Phrase(text));
        cell.setPadding(8);
        if (isHeader) {
            cell.setBackgroundColor(Color.LIGHT_GRAY);
        }
        return cell;
    }
}
