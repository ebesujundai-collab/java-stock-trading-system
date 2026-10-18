//ここからStep3

package com.example.trading.Step3;

import com.example.trading.validator.Trading;
import com.example.trading.repository.TradingRepository;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TradingHistory {

    private final TradingRepository repository;

    public TradingHistory(TradingRepository repository) {
        this.repository = repository;
    }

    public List<Trading> getHistory(HistoryType historyType)
            throws IOException {

        List<Trading> allTradings = repository.findAll();

        if (historyType == HistoryType.ALL) {
            return allTradings;
        }

        List<Trading> result = new ArrayList<>();

        LocalDate today = LocalDate.now();

        for (Trading trading : allTradings) {

            LocalDate tradeDate =
                    trading.getTradeDateTime().toLocalDate();

            if (historyType == HistoryType.TODAY
                    && tradeDate.equals(today)) {

                result.add(trading);
            }

            if (historyType == HistoryType.YESTERDAY
                    && tradeDate.equals(today.minusDays(1))) {

                result.add(trading);
            }
        }

        return result;
    }
}