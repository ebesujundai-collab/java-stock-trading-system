package com.example.trading.ui;

import com.example.trading.model.Trade;

import java.io.PrintWriter;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/** 渡された履歴の順序を保ち、コンソール向けの罫線付き表にする。 */
public class TradeHistoryTablePrinter {
    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss");
    private static final String[] HEADERS = {"取引日時", "銘柄コード", "売買", "株数"};

    private final PrintWriter output;

    public TradeHistoryTablePrinter(PrintWriter output) {
        this.output = output;
    }

    public void print(List<Trade> trades) {
        if (trades.isEmpty()) {
            output.println("該当する取引履歴はありません。");
            output.flush();
            return;
        }

        List<String[]> rows = new ArrayList<>();
        for (Trade trade : trades) {
            rows.add(new String[]{
                    trade.getTradeDateTime().format(DISPLAY_FORMAT),
                    trade.getStockCode(),
                    trade.getTradeType().name(),
                    trade.getQuantity().toString()
            });
        }

        int[] columnWidths = calculateColumnWidths(rows);
        printBorder(columnWidths);
        printRow(HEADERS, columnWidths);
        printBorder(columnWidths);
        for (String[] row : rows) {
            printRow(row, columnWidths);
        }
        printBorder(columnWidths);
        output.flush();
    }

    private int[] calculateColumnWidths(List<String[]> rows) {
        int[] widths = new int[HEADERS.length];
        for (int column = 0; column < HEADERS.length; column++) {
            widths[column] = displayWidth(HEADERS[column]);
            for (String[] row : rows) {
                widths[column] = Math.max(widths[column], displayWidth(row[column]));
            }
        }
        return widths;
    }

    private void printBorder(int[] widths) {
        output.print("+");
        for (int width : widths) {
            output.print("-".repeat(width + 2));
            output.print("+");
        }
        output.println();
    }

    private void printRow(String[] cells, int[] widths) {
        output.print("|");
        for (int column = 0; column < cells.length; column++) {
            String cell = cells[column];
            int padding = widths[column] - displayWidth(cell);
            output.print(" " + cell + " ".repeat(padding) + " |");
        }
        output.println();
    }

    private int displayWidth(String text) {
        // この表の文字はASCIIと日本語の見出し。日本語を2桁幅としてそろえる。
        int width = 0;
        for (int index = 0; index < text.length(); index++) {
            width += text.charAt(index) <= 0x7f ? 1 : 2;
        }
        return width;
    }
}
