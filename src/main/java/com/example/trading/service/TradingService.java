package com.example.trading.service;

import com.example.trading.model.Trade;
import com.example.trading.model.TradeType;
import com.example.trading.repository.TradeHistoryRepository;
import com.example.trading.validator.QuantityValidator;
import com.example.trading.validator.StockCodeValidator;

import java.io.IOException;
import java.math.BigInteger;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/** 入力の検証から取引条件の判定、保存までを順番に進める。 */
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

    public Trade execute(String stockCodeInput, String sideInput, String quantityInput)
            throws IOException {
        String stockCode = StockCodeValidator.validate(stockCodeInput);
        TradeType side = TradeType.fromInput(sideInput);
        BigInteger quantity = QuantityValidator.parse(quantityInput);

        // 時計の設定地域に依存せず、日本の現在日時を一度だけ取得する。
        LocalDateTime now = LocalDateTime.ofInstant(clock.instant(), TradingHoursService.JAPAN_ZONE);
        tradingHoursService.validate(now);

        // 判定後に保存日時を秒単位へそろえ、終了直後の時刻を誤って許可しない。
        Trade trade = new Trade(now.truncatedTo(ChronoUnit.SECONDS), stockCode, side, quantity);
        repository.append(trade);
        return trade;
    }
}
