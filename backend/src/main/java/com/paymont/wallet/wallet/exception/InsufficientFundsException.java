package com.paymont.wallet.wallet.exception;

import com.paymont.wallet.wallet.Currency;

import java.math.BigDecimal;

public class InsufficientFundsException extends RuntimeException {

    public InsufficientFundsException(Long walletId, Currency currency, BigDecimal requested, BigDecimal available) {
        super("Insufficient funds in wallet " + walletId + " for currency " + currency +
                ": requested " + requested + ", available " + available);
    }
}
