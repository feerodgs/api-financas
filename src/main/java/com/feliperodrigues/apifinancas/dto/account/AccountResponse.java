package com.feliperodrigues.apifinancas.dto.account;

import com.feliperodrigues.apifinancas.domain.Account;
import com.feliperodrigues.apifinancas.domain.enums.AccountType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AccountResponse(
        Long id,
        String name,
        AccountType type,
        BigDecimal balance,
        LocalDateTime createdAt
) {
    public static AccountResponse from(Account account) {
        return new AccountResponse(
                account.getId(),
                account.getName(),
                account.getType(),
                account.getBalance(),
                account.getCreatedAt()
        );
    }
}
