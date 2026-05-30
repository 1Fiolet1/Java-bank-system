package ru.seleznev.mappers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.seleznev.domain.AccountModel;
import ru.seleznev.entities.Account;

@Component
public class AccountEntityMapper {

    private final UserEntityMapper userEntityMapper;

    @Autowired
    public AccountEntityMapper(UserEntityMapper userEntityMapper) {
        this.userEntityMapper = userEntityMapper;
    }

    public AccountModel toModel(Account account) {
        return new AccountModel(
                account.getId(),
                account.getBalance(),
                userEntityMapper.toModelWithoutFriends(account.getOwner())
        );
    }

    public Account toEntity(AccountModel model) {
        Account account = new Account(
                userEntityMapper.toEntityWithoutFriends(model.getOwner()),
                model.getBalance()
        );

        account.setId(model.getId());

        return account;
    }


}
