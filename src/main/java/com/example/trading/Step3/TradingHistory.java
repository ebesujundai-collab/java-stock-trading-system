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

            allTradings.sort(Comparator.comparing(Trading::getTradeDateTime).reversed());

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

        result.sort(Comparator.comparing(Trading::getTradeDateTime).reversed());
        return result;
    }
}