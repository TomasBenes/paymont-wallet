package com.paymont.wallet.wallet.service;

import com.paymont.wallet.wallet.*;
import com.paymont.wallet.wallet.exception.InsufficientFundsException;
import com.paymont.wallet.wallet.exception.WalletNotFoundException;
import com.paymont.wallet.wallet.repository.WalletBalanceRepository;
import com.paymont.wallet.wallet.repository.WalletRepository;
import com.paymont.wallet.wallet.repository.WalletTransactionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;
    private final WalletBalanceRepository balanceRepository;
    private final WalletTransactionRepository transactionRepository;

    /**
     * Vytvoří novou peněženku se zůstatkem 0 pro CZK a EUR.
     */
    @Transactional
    public Wallet createWallet(String name) {
        Wallet wallet = Wallet.builder()
                .name(name)
                .build();

        wallet = walletRepository.save(wallet);

        // Inicializace zůstatků pro CZK a EUR
        for (Currency currency : Currency.values()) {
            WalletBalance balance = WalletBalance.builder()
                    .wallet(wallet)
                    .currency(currency)
                    .amount(BigDecimal.ZERO)
                    .build();
            balanceRepository.save(balance);
            wallet.getBalances().add(balance);
        }

        return wallet;
    }

    public Wallet getWalletById(Long walletId) {
        return walletRepository.findById(walletId)
                .orElseThrow(() -> new WalletNotFoundException(walletId));
    }

    /**
     * Vrátí mapu zůstatků podle měny pro danou peněženku.
     */
    public Map<Currency, BigDecimal> getBalances(Long walletId) {
        Wallet wallet = getWalletById(walletId);

        Map<Currency, BigDecimal> result = new EnumMap<>(Currency.class);
        for (WalletBalance balance : wallet.getBalances()) {
            result.put(balance.getCurrency(), balance.getAmount());
        }
        return result;
    }

    /**
     * Nabití peněženky (DEPOSIT).
     */
    @Transactional
    public WalletTransaction deposit(Long walletId,
                                     Currency currency,
                                     BigDecimal amount,
                                     String description) {

        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        Wallet wallet = getWalletById(walletId);

        WalletBalance balance = balanceRepository
                .findByWalletAndCurrency(wallet, currency)
                .orElseThrow(() -> new IllegalStateException("Balance for currency " + currency + " not found"));

        BigDecimal newAmount = balance.getAmount().add(amount);
        balance.setAmount(newAmount);
        balanceRepository.save(balance);

        WalletTransaction tx = WalletTransaction.builder()
                .wallet(wallet)
                .currency(currency)
                .type(TransactionType.DEPOSIT)
                .amount(amount)
                .description(description)
                .status(TransactionStatus.SUCCESS)
                .balanceAfter(newAmount)
                .build();

        return transactionRepository.save(tx);
    }

    /**
     * Výběr prostředků (WITHDRAWAL).
     */
    @Transactional
    public WalletTransaction withdraw(Long walletId,
                                      Currency currency,
                                      BigDecimal amount,
                                      String targetAccount,
                                      String description) {

        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        if (targetAccount == null || targetAccount.isBlank()) {
            throw new IllegalArgumentException("Target account is required for withdrawal");
        }

        Wallet wallet = getWalletById(walletId);

        WalletBalance balance = balanceRepository
                .findByWalletAndCurrency(wallet, currency)
                .orElseThrow(() -> new IllegalStateException("Balance for currency " + currency + " not found"));

        if (balance.getAmount().compareTo(amount) < 0) {
            throw new InsufficientFundsException(walletId, currency, amount, balance.getAmount());
        }

        BigDecimal newAmount = balance.getAmount().subtract(amount);
        balance.setAmount(newAmount);
        balanceRepository.save(balance);

        WalletTransaction tx = WalletTransaction.builder()
                .wallet(wallet)
                .currency(currency)
                .type(TransactionType.WITHDRAWAL)
                .amount(amount)
                .targetAccount(targetAccount)
                .description(description)
                .status(TransactionStatus.SUCCESS)
                .balanceAfter(newAmount)
                .build();

        return transactionRepository.save(tx);
    }

    /**
     * Historie transakcí dané peněženky.
     */
    public List<WalletTransaction> getTransactions(Long walletId) {
        Wallet wallet = getWalletById(walletId);
        return transactionRepository.findByWalletOrderByCreatedAtDesc(wallet);
    }
}
