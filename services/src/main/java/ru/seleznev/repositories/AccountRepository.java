package ru.seleznev.repositories;


import ru.seleznev.domain.AccountModel;
import java.util.List;
import java.util.Optional;

public interface AccountRepository {

    AccountModel save(AccountModel account);

    Optional<AccountModel> findById(Long id);

    List<AccountModel> findAll();

    List<AccountModel> findByOwnerId(Long ownerId);

    void deleteById(Long id);
}
