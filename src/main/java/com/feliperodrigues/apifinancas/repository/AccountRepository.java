package com.feliperodrigues.apifinancas.repository;

import com.feliperodrigues.apifinancas.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findAllByUserId(Long userId);
    Optional<Account> findByIdAndUserId(Long id, Long userId);
}
