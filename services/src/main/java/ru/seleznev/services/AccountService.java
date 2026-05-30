package ru.seleznev.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.seleznev.domain.AccountModel;
import ru.seleznev.domain.OperationModel;
import ru.seleznev.domain.UserModel;
import ru.seleznev.enums.OperationType;
import ru.seleznev.exceptions.EntityNotFoundException;
import ru.seleznev.exceptions.InsufficientFundsException;
import ru.seleznev.repositories.AccountRepository;
import ru.seleznev.repositories.OperationRepository;
import ru.seleznev.repositories.UserRepository;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final OperationRepository operationRepository;

    @Autowired
    public AccountService(AccountRepository accountRepository,
                          UserRepository userRepository,
                          OperationRepository operationRepository
                          )
    {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
        this.operationRepository = operationRepository;
    }

    @Transactional
    public AccountModel createAccount(Long userId) {
        UserModel user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("user not found"));

        AccountModel account = new AccountModel(user);

        return accountRepository.save(account);
    }

    @Transactional(readOnly = true)
    public BigDecimal getBalance(Long accountId) {
        AccountModel account = accountRepository.findById(accountId)
                .orElseThrow(() -> new EntityNotFoundException("account not found"));

        return account.getBalance();
    }

    @Transactional
    public AccountModel deposit(Long accountId, BigDecimal amount) {
        if (amount == null) {
            throw new IllegalArgumentException("amount is null");
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("amount less than zero");
        }

        AccountModel account = accountRepository.findById(accountId)
                .orElseThrow(() -> new EntityNotFoundException("account not found"));

        account.setBalance(account.getBalance().add(amount));
        AccountModel savedAccount = accountRepository.save(account);

        OperationModel operation = new OperationModel(
                OperationType.DEPOSIT,
                amount,
                BigDecimal.ZERO,
                savedAccount.getId()
        );

        operationRepository.save(operation);

        return savedAccount;
    }

    @Transactional
    public AccountModel withdraw(Long accountId, BigDecimal amount) {
        if (amount == null) {
            throw new IllegalArgumentException("amount is null");
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("amount less than zero");
        }

        AccountModel account = accountRepository.findById(accountId)
                .orElseThrow(() -> new EntityNotFoundException("account not found"));

        if (account.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException("insufficient funds");
        }

        account.setBalance(account.getBalance().subtract(amount));
        AccountModel savedAccount = accountRepository.save(account);

        OperationModel operation = new OperationModel(
                OperationType.WITHDRAW,
                amount,
                BigDecimal.ZERO,
                savedAccount.getId()
        );

        operationRepository.save(operation);

        return savedAccount;
    }

    @Transactional(readOnly = true)
    public AccountModel getAccountById(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new EntityNotFoundException("account not found"));
    }

    @Transactional(readOnly = true)
    public List<AccountModel> getAllAccounts() {
        return accountRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<AccountModel> getAccountByUserId(Long userId) {
        return accountRepository.findByOwnerId(userId);
    }
}
