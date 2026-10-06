package com.devsu.bank.infrastructure.report;

import com.devsu.bank.application.report.AccountStatement;
import com.devsu.bank.application.report.AccountStatement.AccountSummary;
import com.devsu.bank.domain.model.Transaction;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import org.openpdf.text.Document;
import org.openpdf.text.Element;
import org.openpdf.text.Font;
import org.openpdf.text.FontFactory;
import org.openpdf.text.PageSize;
import org.openpdf.text.Paragraph;
import org.openpdf.text.pdf.PdfPCell;
import org.openpdf.text.pdf.PdfPTable;
import org.openpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Component;

@Component
public class AccountStatementPdfRenderer {

    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final Font TITLE = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
    private static final Font HEADING = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11);
    private static final Font NORMAL = FontFactory.getFont(FontFactory.HELVETICA, 10);
    private static final Font TABLE_HEADER = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9);
    private static final Font TABLE_CELL = FontFactory.getFont(FontFactory.HELVETICA, 9);

    public byte[] render(AccountStatement statement) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, out);
        document.open();

        document.add(new Paragraph("Account Statement", TITLE));
        document.add(new Paragraph("Customer: " + statement.customer().getName()
                + " (" + statement.customer().getIdentification() + ")", NORMAL));
        document.add(new Paragraph("Period: " + DATE.format(statement.from())
                + " to " + DATE.format(statement.to()), NORMAL));

        for (AccountSummary summary : statement.accounts()) {
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Account " + summary.account().getAccountNumber()
                    + " - " + summary.account().getAccountType(), HEADING));
            document.add(new Paragraph("Initial balance: " + money(summary.account().getInitialBalance())
                    + "   Current balance: " + money(summary.currentBalance())
                    + "   Credits: " + money(summary.totalCredits())
                    + "   Debits: " + money(summary.totalDebits())
                    + "   Status: " + (Boolean.TRUE.equals(summary.account().getStatus()) ? "active" : "inactive"),
                    NORMAL));
            if (summary.transactions().isEmpty()) {
                document.add(new Paragraph("No transactions in this period", NORMAL));
            } else {
                document.add(transactionsTable(summary));
            }
        }

        document.close();
        return out.toByteArray();
    }

    private PdfPTable transactionsTable(AccountSummary summary) {
        PdfPTable table = new PdfPTable(new float[] {3, 3, 2, 2});
        table.setWidthPercentage(100);
        table.setSpacingBefore(4);
        for (String header : new String[] {"Date", "Type", "Amount", "Balance"}) {
            PdfPCell cell = new PdfPCell(new Paragraph(header, TABLE_HEADER));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        }
        for (Transaction transaction : summary.transactions()) {
            table.addCell(new Paragraph(DATE_TIME.format(transaction.getDate()), TABLE_CELL));
            table.addCell(new Paragraph(transaction.getTransactionType().name(), TABLE_CELL));
            table.addCell(amountCell(transaction.getAmount()));
            table.addCell(amountCell(transaction.getBalance()));
        }
        return table;
    }

    private static PdfPCell amountCell(BigDecimal value) {
        PdfPCell cell = new PdfPCell(new Paragraph(money(value), TABLE_CELL));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        return cell;
    }

    private static String money(BigDecimal value) {
        return value.setScale(2).toPlainString();
    }
}
