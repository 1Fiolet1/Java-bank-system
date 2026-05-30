package ru.seleznev.adapters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.seleznev.domain.AccountModel;
import ru.seleznev.mappers.AccountEntityMapper;
import ru.seleznev.repositories.AccountRepository;
import ru.seleznev.springdata.SpringDataAccountRepository;

import java.util.List;
import java.util.Optional;

@Repository
public class AccountRepositoryAdapter implements AccountRepository {

    private final SpringDataAccountRepository accountRepository;
    private final AccountEntityMapper mapper;

    @Autowired
    public AccountRepositoryAdapter(SpringDataAccountRepository accountRepository, AccountEntityMapper mapper) {
        this.accountRepository = accountRepository;
        this.mapper = mapper;
    }

    @Override
    public AccountModel save(AccountModel account) {
        return mapper.toModel(accountRepository.save(mapper.toEntity(account)));
    }

    @Override
    public Optional<AccountModel> findById(Long id) {
        return accountRepository.findById(id).map(mapper::toModel);
    }

    @Override
    public List<AccountModel> findAll() {
        return accountRepository.findAll().stream()
                .map(mapper::toModel)
                .toList();
    }

    @Override
    public List<AccountModel> findByOwnerId(Long ownerId) {
        return accountRepository.findByOwnerId(ownerId).stream()
                .map(mapper::toModel)
                .toList();
    }

    @Override
    public void deleteById(Long id) {
        accountRepository.deleteById(id);
    }
}
