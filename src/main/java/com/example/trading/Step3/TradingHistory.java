package com.example.trading.Step3;

import com.example.trading.repository.TradingReader;
import com.example.trading.validator.Trading;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class TradingHistory {

    private final TradingReader reader;

    public TradingHistory(TradingReader reader) {
        this.reader = reader;
    }

    public List<Trading> getHistory(HistoryType historyType) {

        List<Trading> allTradings = reader.findAll();

        if (historyType == HistoryType.ALL) {
            Comparator<Trading> comparator = Comparator.comparing(trading -> trading.getTradeDateTime());
            Comparator<Trading> reversedComparator = comparator.reversed();
            allTradings.sort(reversedComparator);

            return allTradings;
        }

        List<Trading> result = new ArrayList<>();

        LocalDate today = LocalDate.now();

        for (Trading trading : allTradings) {
            LocalDate tradeDate = trading.getTradeDateTime().toLocalDate();
            if (historyType == HistoryType.TODAY && tradeDate.equals(today)) {
                result.add(trading);
            }
            if (historyType == HistoryType.YESTERDAY && tradeDate.equals(today.minusDays(1))) {
                result.add(trading);
            }
        }
        Comparator<Trading> comparator = Comparator.comparing(trading -> trading.getTradeDateTime());
        Comparator<Trading> reversedComparator = comparator.reversed();
        result.sort(reversedComparator);

        return result;
    }

    public List<Trading> getHistoryByStockCode(String stockcode) {
        List<Trading> allTradings = reader.findAll();
        List<Trading> result = new ArrayList<>();
        Comparator<Trading> comparator = Comparator.comparing(trading -> trading.getTradeDateTime());
        Comparator<Trading> reversedComparator = comparator.reversed();
        result.sort(reversedComparator);
        for (Trading trading : allTradings) {
            if (trading.getStockCode().equals(stockcode)) {
                result.add(trading);
            }
        }
        return result;
    }
}