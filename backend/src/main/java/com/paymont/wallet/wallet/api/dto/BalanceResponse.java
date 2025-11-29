package com.paymont.wallet.wallet.api.dto;

import com.paymont.wallet.wallet.Currency;

import java.math.BigDecimal;

public class BalanceResponse {

    private Currency currency;
    private BigDecimal amount;

    public BalanceResponse(Currency currency, BigDecimal amount) {
        this.currency = currency;
        this.amount = amount;
    }

    public Currency getCurrency() {
        return currency;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}
