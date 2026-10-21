package com.example.trading.validator;

import java.math.BigDecimal;
import java.time.LocalDateTime;


public final class Trading {

    private final LocalDateTime tradeDateTime;
    private final String stockCode;
    private final Side side;
    private final Long quantity;
    private final BigDecimal price;

    public Trading(
            LocalDateTime tradeDateTime,
            String stockCode,
            Side side,
            Long quantity,
            BigDecimal price
    ) {
        this.tradeDateTime = tradeDateTime;
        this.stockCode = stockCode;
        this.side = side;
        this.quantity = quantity;
        this.price = price;
    }

    public LocalDateTime getTradeDateTime() {return tradeDateTime;}
    public String getStockCode() {return stockCode;}
    public Side getTradeType() {return side;}
    public Long getQuantity() {return quantity;}
    public BigDecimal getPrice() {return price;}
}