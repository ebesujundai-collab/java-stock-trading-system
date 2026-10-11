package com.example.trading.service;

import com.example.trading.model.Trade;
import com.example.trading.model.TradeType;
import com.example.trading.repository.TradeHistoryRepository;

import java.io.IOException;
import java.math.BigInteger;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/** 検証済みの入力を受け取り、取引時間の判定と保存を行う。 */
public class TradingService {
    private final TradeHistoryRepository repository;
    private final TradingHoursService tradingHoursService;
    private final Clock clock;

    public TradingService(TradeHistoryRepository repository,
                          TradingHoursService tradingHoursService, Clock clock) {
        this.repository = Objects.requireNonNull(repository);
        this.tradingHoursService = Objects.requireNonNull(tradingHoursService);
        this.clock = Objects.requireNonNull(clock);
    }

    public Trade execute(String stockCode, TradeType tradeType, BigInteger quantity)
            throws IOException {
        LocalDateTime now = LocalDateTime.ofInstant(clock.instant(), TradingHoursService.JAPAN_ZONE);
        tradingHoursService.validate(now);

        Trade trade = new Trade(
                now.truncatedTo(ChronoUnit.SECONDS),
                stockCode,
                tradeType,
                quantity
        );

        repository.append(trade);
        return trade;
    }
}
