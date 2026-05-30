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

@Service
public class TransferService {

    private final BigDecimal OWN_COMMISSION = new BigDecimal("0.00");
    private final BigDecimal FRIEND_COMMISSION = new BigDecimal("0.03");
    private final BigDecimal OTHER_COMMISSION = new BigDecimal("0.10");

    private final AccountRepository accountRepository;
    private final OperationRepository operationRepository;
    private final UserRepository userRepository;

    @Autowired
    public TransferService(AccountRepository accountRepository, OperationRepository operationRepository,UserRepository userRepository) {
        this.accountRepository = accountRepository;
        this.operationRepository = operationRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void transfer(Long fromAccountId, Long toAccountId, BigDecimal amount) {
        if (amount == null) {
            throw new IllegalArgumentException("amount is null");
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("amount must be greater than zero");
        }

        AccountModel fromAccount = accountRepository.findById(fromAccountId)
                .orElseThrow(() -> new EntityNotFoundException("sender account not found"));


        AccountModel toAccount = accountRepository.findById(toAccountId)
                .orElseThrow(() -> new EntityNotFoundException("recipient account not found"));

        BigDecimal commissionCoefficient = calculateCommission(fromAccount, toAccount);
        BigDecimal commission = amount.multiply(commissionCoefficient);
        BigDecimal total = amount.add(commission);

        if (fromAccount.getBalance().compareTo(total) < 0) {
            throw new InsufficientFundsException("insufficient funds for transfer");
        }

        fromAccount.setBalance(fromAccount.getBalance().subtract(total));
        toAccount.setBalance(toAccount.getBalance().add(amount));

        AccountModel savedFromAccount = accountRepository.save(fromAccount);
        AccountModel savedToAccount = accountRepository.save(toAccount);

        OperationModel debitOperation = new OperationModel(
                OperationType.TRANSFER_OUT,
                amount,
                commission,
                savedFromAccount.getId()
        );

        OperationModel creditOperation = new OperationModel(
                OperationType.TRANSFER_IN,
                amount,
                BigDecimal.ZERO,
                savedToAccount.getId()
        );

        operationRepository.save(debitOperation);
        operationRepository.save(creditOperation);
    }

    private BigDecimal calculateCommission(AccountModel fromAccount, AccountModel toAccount) {
        UserModel sender = userRepository.findWithFriendsById(fromAccount.getOwner().getId())
                .orElseThrow(() -> new EntityNotFoundException("sender not found"));

        UserModel recipient = toAccount.getOwner();

        if (sender.getId().equals(recipient.getId())) {
            return OWN_COMMISSION;
        }

        boolean isFriend = sender.getFriends().stream()
                .anyMatch(friend -> friend.getId().equals(recipient.getId()));

        if (isFriend) {
            return FRIEND_COMMISSION;
        }

        return OTHER_COMMISSION;
    }

}
