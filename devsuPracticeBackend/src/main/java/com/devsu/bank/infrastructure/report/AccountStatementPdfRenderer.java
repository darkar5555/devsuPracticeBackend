package com.devsu.bank.infrastructure.report;

import com.devsu.bank.application.report.AccountStatement;
import com.devsu.bank.application.report.AccountStatement.AccountSummary;
import com.devsu.bank.domain.model.AccountType;
import com.devsu.bank.domain.model.Transaction;
import com.devsu.bank.domain.model.TransactionType;
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

        document.add(new Paragraph("Estado de cuenta", TITLE));
        document.add(new Paragraph("Cliente: " + statement.customer().getName()
                + " (" + statement.customer().getIdentification() + ")", NORMAL));
        document.add(new Paragraph("Periodo: " + DATE.format(statement.from())
                + " al " + DATE.format(statement.to()), NORMAL));

        for (AccountSummary summary : statement.accounts()) {
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Cuenta " + summary.account().getAccountNumber()
                    + " - " + label(summary.account().getAccountType()), HEADING));
            document.add(new Paragraph("Saldo inicial: " + money(summary.account().getInitialBalance())
                    + "   Saldo disponible: " + money(summary.currentBalance())
                    + "   Créditos: " + money(summary.totalCredits())
                    + "   Débitos: " + money(summary.totalDebits())
                    + "   Estado: " + (Boolean.TRUE.equals(summary.account().getStatus()) ? "activa" : "inactiva"),
                    NORMAL));
            if (summary.transactions().isEmpty()) {
                document.add(new Paragraph("Sin movimientos en el periodo", NORMAL));
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
        for (String header : new String[] {"Fecha", "Tipo", "Valor", "Saldo"}) {
            PdfPCell cell = new PdfPCell(new Paragraph(header, TABLE_HEADER));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        }
        for (Transaction transaction : summary.transactions()) {
            table.addCell(new Paragraph(DATE_TIME.format(transaction.getDate()), TABLE_CELL));
            table.addCell(new Paragraph(label(transaction.getTransactionType()), TABLE_CELL));
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

    private static String label(AccountType type) {
        return switch (type) {
            case SAVINGS -> "Ahorros";
            case CHECKING -> "Corriente";
        };
    }

    private static String label(TransactionType type) {
        return switch (type) {
            case DEPOSIT -> "Depósito";
            case WITHDRAWAL -> "Retiro";
        };
    }

    private static String money(BigDecimal value) {
        return value.setScale(2).toPlainString();
    }
}
