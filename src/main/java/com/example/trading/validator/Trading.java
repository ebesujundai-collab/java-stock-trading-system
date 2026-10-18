package com.example.trading.validator;

import java.time.LocalDateTime;

/** 1件の取引を表す。生成後に値を変更するメソッドは持たない。 */
public final class Trading {

    private final LocalDateTime tradeDateTime;
    private final String stockCode;
    private final TradeType tradeType;
    private final String quantity;
    private final String price;

    public Trading(
            LocalDateTime tradeDateTime,
            String stockCode,
            TradeType tradeType,
            String quantity,
            String price
    ) {
        this.tradeDateTime = tradeDateTime;
        this.stockCode = stockCode;
        this.tradeType = tradeType;
        this.quantity = quantity;
        this.price = price;
    }

    public LocalDateTime getTradeDateTime() {return tradeDateTime;}
    public String getStockCode() {return stockCode;}
    public TradeType getTradeType() {return tradeType;}
    public String getQuantity() {return quantity;}
    public String getPrice() {return price;}
}