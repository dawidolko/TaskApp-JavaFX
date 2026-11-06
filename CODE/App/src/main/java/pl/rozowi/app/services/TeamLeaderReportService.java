package pl.rozowi.app.services;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.awt.Color;

/**
 * Generates PDF reports for the team leader.
 * Handles reports on team members and assigned tasks.
 * Uses the iText library to create PDF documents.
 */
public class TeamLeaderReportService {

    private Font titleFont;
    private Font subtitleFont;
    private Font sectionFont;
    private Font normalFont;
    private Font smallFont;
    private Font tableHeaderFont;
    private Font tableDataFont;
    private Font filterFont;

    private Color primaryColor = new Color(0, 123, 255);

    /**
     * Initializes the fonts used in the PDF document.
     * If loading the base font fails, the default font is used.
     */
    public TeamLeaderReportService() {
        try {
            BaseFont base = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1250, BaseFont.EMBEDDED);
            titleFont = new Font(base, 18, Font.BOLD, primaryColor);
            subtitleFont = new Font(base, 14, Font.BOLD, primaryColor);
            sectionFont = new Font(base, 12, Font.BOLD);
            normalFont = new Font(base, 10, Font.NORMAL);
            smallFont = new Font(base, 8, Font.NORMAL);
            tableHeaderFont = new Font(base, 10, Font.BOLD, Color.WHITE);
            tableDataFont = new Font(base, 9, Font.NORMAL);
            filterFont = new Font(base, 10, Font.ITALIC, new Color(100, 100, 100));
        } catch (Exception e) {
            titleFont = new Font(Font.HELVETICA, 18, Font.BOLD, primaryColor);
            subtitleFont = new Font(Font.HELVETICA, 14, Font.BOLD, primaryColor);
            sectionFont = new Font(Font.HELVETICA, 12, Font.BOLD);
            normalFont = new Font(Font.HELVETICA, 10, Font.NORMAL);
            smallFont = new Font(Font.HELVETICA, 8, Font.NORMAL);
            tableHeaderFont = new Font(Font.HELVETICA, 10, Font.BOLD, Color.WHITE);
            tableDataFont = new Font(Font.HELVETICA, 9, Font.NORMAL);
            filterFont = new Font(Font.HELVETICA, 10, Font.ITALIC, new Color(100, 100, 100));
        }
    }

    /**
     * Generates a PDF report with the list of team members.
     *
     * @param filename full path and name of the PDF file to save
     * @param content input data as formatted text (e.g. with filters and the
     *         member list)
     * @throws IOException if writing the PDF file fails
     */
    public void generateTeamMembersReportPdf(String filename, String content) throws IOException {
        Document doc = new Document(PageSize.A4);
        try {
            PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream(filename));
            writer.setPageEvent(new FooterEvent());
            doc.open();

            String title = "Członkowie Zespołu";
            if (content.startsWith("RAPORT:")) {
                String[] lines = content.split("\n", 2);
                if (lines.length > 0) {
                    title = lines[0].replace("RAPORT:", "").trim();
                }
            }

            addReportHeader(doc, "Raport: " + title);

            String[] lines = content.split("\n");

            boolean inFiltersSection = false;
            boolean passedFilters = false;
            List<String> filters = new ArrayList<>();

            for (int i = 0; i < lines.length; i++) {
                String line = lines[i];

                if (line.startsWith("RAPORT:") || line.startsWith("Data wygenerowania:")) {
                    continue;
                }

                if (line.equals("Zastosowane filtry:")) {
                    inFiltersSection = true;
                    continue;
                }

                if (inFiltersSection) {
                    if (line.trim().isEmpty()) {
                        inFiltersSection = false;
                        passedFilters = true;

                        if (!filters.isEmpty()) {
                            Paragraph filtersHeader = new Paragraph("Zastosowane filtry:", sectionFont);
                            filtersHeader.setSpacingBefore(5);
                            filtersHeader.setSpacingAfter(5);
                            doc.add(filtersHeader);

                            for (String filter : filters) {
                                Paragraph p = new Paragraph(filter, filterFont);
                                p.setIndentationLeft(10);
                                doc.add(p);
                            }

                            doc.add(new Paragraph(" "));
                            addSeparator(doc);
                            doc.add(new Paragraph(" "));
                        }
                        continue;
                    }
                    filters.add(line);
                    continue;
                }

                if (passedFilters) {
                    if (line.startsWith("=== ZESPÓŁ:")) {
                        Paragraph teamHeader = new Paragraph(line.replace("===", "").trim(), subtitleFont);
                        teamHeader.setSpacingBefore(10);
                        teamHeader.setSpacingAfter(5);
                        doc.add(teamHeader);
                    } else if (line.startsWith("Projekt:")) {
                        Paragraph projectLine = new Paragraph(line, normalFont);
                        projectLine.setSpacingAfter(5);
                        doc.add(projectLine);
                    } else if (line.startsWith("Liczba członków:")) {
                        Paragraph membersCount = new Paragraph(line, normalFont);
                        membersCount.setSpacingAfter(5);
                        doc.add(membersCount);
                    } else if (line.equals("CZŁONKOWIE ZESPOŁU:")) {
                        Paragraph membersHeader = new Paragraph(line, sectionFont);
                        membersHeader.setSpacingBefore(5);
                        membersHeader.setSpacingAfter(5);
                        doc.add(membersHeader);
                    } else if (line.startsWith("- ")) {
                        Paragraph listItem = new Paragraph(line, normalFont);
                        listItem.setIndentationLeft(10);
                        doc.add(listItem);
                    } else if (line.startsWith("Brak członków")) {
                        Paragraph noMembers = new Paragraph(line, normalFont);
                        noMembers.setIndentationLeft(10);
                        doc.add(noMembers);
                    } else if (!line.trim().isEmpty()) {
                        Paragraph p = new Paragraph(line, normalFont);
                        doc.add(p);
                    } else if (line.trim().isEmpty()) {
                        doc.add(new Paragraph(" "));
                    }
                }
            }

            addReportFooter(doc);
        } catch (DocumentException de) {
            throw new IOException(de.getMessage(), de);
        } finally {
            if (doc.isOpen()) doc.close();
        }
    }

    /**
     * Generates a PDF report on team tasks.
     *
     * @param filename full path and name of the PDF file to save
     * @param content input data as formatted text (e.g. with the task list and
     *         a summary)
     * @throws IOException if writing the PDF file fails
     */
    public void generateTeamTasksReportPdf(String filename, String content) throws IOException {
        Document doc = new Document(PageSize.A4);
        try {
            PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream(filename));
            writer.setPageEvent(new FooterEvent());
            doc.open();

            String title = "Zadania Zespołu";
            if (content.startsWith("RAPORT:")) {
                String[] lines = content.split("\n", 2);
                if (lines.length > 0) {
                    title = lines[0].replace("RAPORT:", "").trim();
                }
            }

            addReportHeader(doc, "Raport: " + title);

            String[] lines = content.split("\n");

            boolean inFiltersSection = false;
            boolean passedFilters = false;
            List<String> filters = new ArrayList<>();

            for (int i = 0; i < lines.length; i++) {
                String line = lines[i];

                if (line.startsWith("RAPORT:") || line.startsWith("Data wygenerowania:")) {
                    continue;
                }

                if (line.equals("Zastosowane filtry:")) {
                    inFiltersSection = true;
                    continue;
                }

                if (inFiltersSection) {
                    if (line.trim().isEmpty()) {
                        inFiltersSection = false;
                        passedFilters = true;

                        if (!filters.isEmpty()) {
                            Paragraph filtersHeader = new Paragraph("Zastosowane filtry:", sectionFont);
                            filtersHeader.setSpacingBefore(5);
                            filtersHeader.setSpacingAfter(5);
                            doc.add(filtersHeader);

                            for (String filter : filters) {
                                Paragraph p = new Paragraph(filter, filterFont);
                                p.setIndentationLeft(10);
                                doc.add(p);
                            }

                            doc.add(new Paragraph(" "));
                            addSeparator(doc);
                            doc.add(new Paragraph(" "));
                        }
                        continue;
                    }
                    filters.add(line);
                    continue;
                }

                if (passedFilters) {
                    if (line.startsWith("=== ZESPÓŁ:")) {
                        Paragraph teamHeader = new Paragraph(line.replace("===", "").trim(), subtitleFont);
                        teamHeader.setSpacingBefore(10);
                        teamHeader.setSpacingAfter(5);
                        doc.add(teamHeader);
                    } else if (line.startsWith("Projekt:")) {
                        Paragraph projectLine = new Paragraph(line, normalFont);
                        projectLine.setSpacingAfter(5);
                        doc.add(projectLine);
                    } else if (line.startsWith("ZADANIA ZESPOŁU")) {
                        Paragraph tasksHeader = new Paragraph("Zadania zespołu:", sectionFont);
                        tasksHeader.setSpacingBefore(5);
                        tasksHeader.setSpacingAfter(5);
                        doc.add(tasksHeader);
                    } else if (line.equals("PODSUMOWANIE STATUSÓW:")) {
                        Paragraph summaryHeader = new Paragraph(line, sectionFont);
                        summaryHeader.setSpacingBefore(10);
                        summaryHeader.setSpacingAfter(5);
                        doc.add(summaryHeader);
                    } else if (line.startsWith("- ")) {
                        Paragraph listItem = new Paragraph(line, normalFont);
                        listItem.setIndentationLeft(10);
                        doc.add(listItem);
                    } else if (line.startsWith("  Przypisane do:")) {
                        Paragraph assignee = new Paragraph(line, normalFont);
                        assignee.setIndentationLeft(20);
                        doc.add(assignee);
                    } else if (line.startsWith("Brak zadań")) {
                        Paragraph noTasks = new Paragraph(line, normalFont);
                        noTasks.setIndentationLeft(10);
                        doc.add(noTasks);
                    } else if (!line.trim().isEmpty()) {
                        Paragraph p = new Paragraph(line, normalFont);
                        doc.add(p);
                    } else if (line.trim().isEmpty()) {
                        doc.add(new Paragraph(" "));
                    }
                }
            }

            addReportFooter(doc);
        } catch (DocumentException de) {
            throw new IOException(de.getMessage(), de);
        } finally {
            if (doc.isOpen()) doc.close();
        }
    }

    /**
     * Adds the report header to the PDF document.
     *
     * @param doc PDF document object
     * @param title report title shown at the top of the page
     * @throws DocumentException if adding to the document fails
     */
    private void addReportHeader(Document doc, String title) throws DocumentException {
        Paragraph p = new Paragraph(title, titleFont);
        p.setAlignment(Element.ALIGN_CENTER);
        p.setSpacingAfter(10);
        doc.add(p);
        String date = "Wygenerowano: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        Paragraph d = new Paragraph(date, normalFont);
        d.setAlignment(Element.ALIGN_CENTER);
        d.setSpacingAfter(15);
        doc.add(d);
    }

    /**
     * Adds a horizontal separator (line) to the PDF document.
     *
     * @param doc PDF document object
     * @throws DocumentException if adding to the document fails
     */
    private void addSeparator(Document doc) throws DocumentException {
        Chunk line = new Chunk(new com.lowagie.text.pdf.draw.LineSeparator(0.5f, 100, primaryColor, Element.ALIGN_CENTER, -2));
        Paragraph para = new Paragraph();
        para.add(line);
        doc.add(para);
    }

    /**
     * Event called at the end of each page, adding a footer with the page number.
     */
    private class FooterEvent extends PdfPageEventHelper {
        @Override
        public void onEndPage(PdfWriter writer, Document doc) {
            PdfContentByte cb = writer.getDirectContent();
            cb.setLineWidth(0.5f);
            cb.setColorStroke(primaryColor);
            float y = doc.bottom() - 5;
            cb.moveTo(doc.left(), y);
            cb.lineTo(doc.right(), y);
            cb.stroke();
            Phrase f = new Phrase("TaskApp - Strona " + writer.getPageNumber(), smallFont);
            ColumnText.showTextAligned(cb, Element.ALIGN_CENTER, f, (doc.left() + doc.right())/2, doc.bottom()-20, 0);
        }
    }

    /**
     * Adds the report footer with information about the generation source to the
     * PDF document.
     *
     * @param doc PDF document object
     * @throws DocumentException if adding to the document fails
     */
    private void addReportFooter(Document doc) throws DocumentException {
        addSeparator(doc);
        Paragraph f = new Paragraph(
                "Ten raport został wygenerowany automatycznie przez system TaskApp.", smallFont
        );
        f.setAlignment(Element.ALIGN_CENTER);
        f.setSpacingAfter(5);
        doc.add(f);
    }
}