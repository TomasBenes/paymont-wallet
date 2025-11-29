package com.paymont.wallet.wallet.repository;

import com.paymont.wallet.wallet.Currency;
import com.paymont.wallet.wallet.Wallet;
import com.paymont.wallet.wallet.WalletBalance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WalletBalanceRepository extends JpaRepository<WalletBalance, Long> {

    Optional<WalletBalance> findByWalletAndCurrency(Wallet wallet, Currency currency);
}
