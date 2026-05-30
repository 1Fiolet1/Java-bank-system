package ru.seleznev.dto.accounts;

import org.springframework.stereotype.Component;
import ru.seleznev.domain.AccountModel;


@Component
public class AccountDtoMapper {

    public AccountResponse toResponse(AccountModel account) {
        return new AccountResponse(
                account.getId(),
                account.getBalance(),
                account.getOwner().getId()
        );
    }
}
