package com.paymont.wallet.wallet.api;

import com.paymont.wallet.wallet.Currency;
import com.paymont.wallet.wallet.Wallet;
import com.paymont.wallet.wallet.WalletTransaction;
import com.paymont.wallet.wallet.api.dto.*;
import com.paymont.wallet.wallet.service.WalletService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/wallets")
@Validated
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    /**
     * Vytvoření nové peněženky.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WalletResponse createWallet(@Valid @RequestBody CreateWalletRequest request) {
        Wallet wallet = walletService.createWallet(request.getName());
        Map<Currency, BigDecimal> balances = walletService.getBalances(wallet.getId());
        return new WalletResponse(wallet.getId(), wallet.getName(), wallet.getCreatedAt(), balances);
    }

    /**
     * Přehled zůstatků peněženky.
     */
    @GetMapping("/{walletId}/balances")
    public List<BalanceResponse> getBalances(@PathVariable Long walletId) {
        Map<Currency, BigDecimal> balances = walletService.getBalances(walletId);
        return balances.entrySet().stream()
                .map(e -> new BalanceResponse(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }

    /**
     * Nabití peněženky.
     */
    @PostMapping("/{walletId}/deposit")
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponse deposit(@PathVariable Long walletId,
                                       @Valid @RequestBody DepositRequest request) {
        WalletTransaction tx = walletService.deposit(
                walletId,
                request.getCurrency(),
                request.getAmount(),
                request.getDescription()
        );
        return toTransactionResponse(tx);
    }

    /**
     * Výběr prostředků.
     */
    @PostMapping("/{walletId}/withdraw")
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponse withdraw(@PathVariable Long walletId,
                                        @Valid @RequestBody WithdrawRequest request) {
        WalletTransaction tx = walletService.withdraw(
                walletId,
                request.getCurrency(),
                request.getAmount(),
                request.getTargetAccount(),
                request.getDescription()
        );
        return toTransactionResponse(tx);
    }

    /**
     * Historie transakcí.
     */
    @GetMapping("/{walletId}/transactions")
    public List<TransactionResponse> getTransactions(@PathVariable Long walletId) {
        List<WalletTransaction> txs = walletService.getTransactions(walletId);
        return txs.stream()
                .map(this::toTransactionResponse)
                .collect(Collectors.toList());
    }

    private TransactionResponse toTransactionResponse(WalletTransaction tx) {
        return new TransactionResponse(
                tx.getId(),
                tx.getType(),
                tx.getStatus(),
                tx.getCurrency(),
                tx.getAmount(),
                tx.getTargetAccount(),
                tx.getDescription(),
                tx.getBalanceAfter(),
                tx.getCreatedAt()
        );
    }
}
