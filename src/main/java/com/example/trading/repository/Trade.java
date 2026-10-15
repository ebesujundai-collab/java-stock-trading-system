package com.example.trading.repository;

import com.example.trading.validator.TradeType;

import java.math.BigInteger;
import java.time.LocalDateTime;

/** 1件の取引を表す。生成後に値を変更するメソッドは持たない。 */
public final class Trade {

    private final LocalDateTime tradeDateTime;
    private final String stockCode;
    private final TradeType tradeType;
    private final BigInteger quantity;

    public Trade(
            LocalDateTime tradeDateTime,
            String stockCode,
            TradeType tradeType,
            BigInteger quantity
    ) {
        this.tradeDateTime = tradeDateTime;
        this.stockCode = stockCode;
        this.tradeType = tradeType;
        this.quantity = quantity;
    }

    public LocalDateTime getTradeDateTime() {return tradeDateTime;}
    public String getStockCode() {return stockCode;}
    public TradeType getTradeType() {return tradeType;}
    public BigInteger getQuantity() {return quantity;}
}