package com.example.trading.stock;

public class StockMaster {

    private final String symbol;
    private final String productName;
    private final String exchangeMarket;
    private final Long sharesIssued;

    public StockMaster(
            String symbol,
            String productName,
            String exchangeMarket,
            Long sharesIssued
    ) {
        this.symbol = symbol;
        this.productName = productName;
        this.exchangeMarket = exchangeMarket;
        this.sharesIssued = sharesIssued;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getProductName() {
        return productName;
    }

    public String getExchangeMarket() {
        return exchangeMarket;
    }

    public Long getSharesIssued() {
        return sharesIssued;
    }
}
