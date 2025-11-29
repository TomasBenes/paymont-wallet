package com.paymont.wallet.wallet.api.dto;

import com.paymont.wallet.wallet.Currency;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

public class WalletResponse {

    private Long id;
    private String name;
    private Instant createdAt;
    private Map<Currency, BigDecimal> balances;

    public WalletResponse(Long id, String name, Instant createdAt, Map<Currency, BigDecimal> balances) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
        this.balances = balances;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Map<Currency, BigDecimal> getBalances() {
        return balances;
    }
}
