package com.example.trading.model;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Objects;

/** 1件の取引を表す。生成後に値を変更するメソッドは持たない。 */
public final class Trade {
    private final LocalDateTime tradeDateTime;
    private final String stockCode;
    private final TradeType tradeType;
    private final BigInteger quantity;

    public Trade(LocalDateTime tradeDateTime, String stockCode, TradeType tradeType,
                 BigInteger quantity) {
        this.tradeDateTime = Objects.requireNonNull(tradeDateTime, "取引日時は必須です。");
        this.stockCode = Objects.requireNonNull(stockCode, "銘柄コードは必須です。");
        this.tradeType = Objects.requireNonNull(tradeType, "売買区分は必須です。");
        this.quantity = Objects.requireNonNull(quantity, "株数は必須です。");
    }

    public LocalDateTime getTradeDateTime() {
        return tradeDateTime;
    }

    public String getStockCode() {
        return stockCode;
    }

    public TradeType getTradeType() {
        return tradeType;
    }

    public BigInteger getQuantity() {
        return quantity;
    }
}
