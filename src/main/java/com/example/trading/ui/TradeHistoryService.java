package com.example.trading.ui;

import com.example.trading.repository.Trade;
import com.example.trading.repository.TradeHistoryRepository;
import com.example.trading.validator.TradingHoursService;

import java.io.IOException;
import java.time.Clock;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/** CSVから得た履歴を、日本の日付で絞り込み、表示する順序に並べる。 */
public final class TradeHistoryService {
    private final TradeHistoryRepository repository;
    private final Clock clock;

    public TradeHistoryService(TradeHistoryRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "履歴の保存先が必要です。");
        this.clock = Objects.requireNonNull(clock, "時計が必要です。");
    }

    public List<Trade> findByPeriod(HistoryPeriod period) throws IOException {
        Objects.requireNonNull(period, "履歴の表示期間が必要です。");
        LocalDate today = LocalDate.now(clock.withZone(TradingHoursService.JAPAN_ZONE));
        List<Trade> result = new ArrayList<>();

        for (Trade trade : repository.findAll()) {
            LocalDate tradeDate = trade.getTradeDateTime().toLocalDate();
            boolean matches = switch (period) {
                case ALL -> true;
                case TODAY -> tradeDate.equals(today);
                case YESTERDAY -> tradeDate.equals(today.minusDays(1));
            };
            if (matches) {
                result.add(trade);
            }
        }

        // List.sortは同じ日時の要素の順序を保つ。CSVには書き戻さない。
        result.sort(Comparator.comparing(Trade::getTradeDateTime).reversed());
        return result;
    }
}
