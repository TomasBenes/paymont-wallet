package com.paymont.wallet.wallet.api.dto;

import com.paymont.wallet.wallet.Currency;
import com.paymont.wallet.wallet.TransactionStatus;
import com.paymont.wallet.wallet.TransactionType;

import java.math.BigDecimal;
import java.time.Instant;

public class TransactionResponse {

    private Long id;
    private TransactionType type;
    private TransactionStatus status;
    private Currency currency;
    private BigDecimal amount;
    private String targetAccount;
    private String description;
    private BigDecimal balanceAfter;
    private Instant createdAt;

    public TransactionResponse(Long id,
                               TransactionType type,
                               TransactionStatus status,
                               Currency currency,
                               BigDecimal amount,
                               String targetAccount,
                               String description,
                               BigDecimal balanceAfter,
                               Instant createdAt) {
        this.id = id;
        this.type = type;
        this.status = status;
        this.currency = currency;
        this.amount = amount;
        this.targetAccount = targetAccount;
        this.description = description;
        this.balanceAfter = balanceAfter;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public TransactionType getType() {
        return type;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public Currency getCurrency() {
        return currency;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getTargetAccount() {
        return targetAccount;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getBalanceAfter() {
        return balanceAfter;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
