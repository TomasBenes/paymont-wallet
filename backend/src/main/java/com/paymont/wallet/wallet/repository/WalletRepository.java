package com.paymont.wallet.wallet.repository;

import com.paymont.wallet.wallet.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
}
