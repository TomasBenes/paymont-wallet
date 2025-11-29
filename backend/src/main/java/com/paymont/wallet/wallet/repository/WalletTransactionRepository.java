package com.paymont.wallet.wallet.repository;

import com.paymont.wallet.wallet.Wallet;
import com.paymont.wallet.wallet.WalletTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, Long> {

    List<WalletTransaction> findByWalletOrderByCreatedAtDesc(Wallet wallet);
}
