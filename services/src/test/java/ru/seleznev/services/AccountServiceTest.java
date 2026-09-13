package ru.seleznev.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OperationRepository operationRepository;

    @InjectMocks
    private AccountService accountService;

    @Test
    void createAccountSavesAccountForExistingUser() {
        UserModel user = user(1L);
        AccountModel savedAccount = account(10L, "0.00", user);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(accountRepository.save(any(AccountModel.class))).thenReturn(savedAccount);

        AccountModel result = accountService.createAccount(1L);

        assertSame(savedAccount, result);

        ArgumentCaptor<AccountModel> accountCaptor = ArgumentCaptor.forClass(AccountModel.class);
        verify(accountRepository).save(accountCaptor.capture());
        assertSame(user, accountCaptor.getValue().getOwner());
        assertBigDecimalEquals("0.00", accountCaptor.getValue().getBalance());
    }

    @Test
    void createAccountThrowsWhenUserDoesNotExist() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> accountService.createAccount(1L));

        verify(accountRepository, never()).save(any());
    }

    @Test
    void getBalanceReturnsAccountBalance() {
        AccountModel account = account(10L, "125.50", user(1L));
        when(accountRepository.findById(10L)).thenReturn(Optional.of(account));

        BigDecimal balance = accountService.getBalance(10L);

        assertBigDecimalEquals("125.50", balance);
    }

    @Test
    void depositIncreasesBalanceAndSavesDepositOperation() {
        AccountModel account = account(10L, "100.00", user(1L));

        when(accountRepository.findById(10L)).thenReturn(Optional.of(account));
        when(accountRepository.save(account)).thenReturn(account);

        AccountModel result = accountService.deposit(10L, new BigDecimal("25.50"));

        assertSame(account, result);
        assertBigDecimalEquals("125.50", result.getBalance());

        ArgumentCaptor<OperationModel> operationCaptor = ArgumentCaptor.forClass(OperationModel.class);
        verify(operationRepository).save(operationCaptor.capture());

        OperationModel operation = operationCaptor.getValue();
        assertEquals(OperationType.DEPOSIT, operation.getType());
        assertBigDecimalEquals("25.50", operation.getAmount());
        assertBigDecimalEquals("0", operation.getCommission());
        assertEquals(10L, operation.getAccountId());
    }

    @Test
    void depositRejectsNonPositiveAmount() {
        assertThrows(IllegalArgumentException.class, () -> accountService.deposit(10L, BigDecimal.ZERO));

        verifyNoInteractions(accountRepository, operationRepository);
    }

    @Test
    void withdrawDecreasesBalanceAndSavesWithdrawOperation() {
        AccountModel account = account(10L, "100.00", user(1L));

        when(accountRepository.findById(10L)).thenReturn(Optional.of(account));
        when(accountRepository.save(account)).thenReturn(account);

        AccountModel result = accountService.withdraw(10L, new BigDecimal("40.00"));

        assertSame(account, result);
        assertBigDecimalEquals("60.00", result.getBalance());

        ArgumentCaptor<OperationModel> operationCaptor = ArgumentCaptor.forClass(OperationModel.class);
        verify(operationRepository).save(operationCaptor.capture());

        OperationModel operation = operationCaptor.getValue();
        assertEquals(OperationType.WITHDRAW, operation.getType());
        assertBigDecimalEquals("40.00", operation.getAmount());
        assertBigDecimalEquals("0", operation.getCommission());
        assertEquals(10L, operation.getAccountId());
    }

    @Test
    void withdrawThrowsWhenBalanceIsInsufficient() {
        AccountModel account = account(10L, "30.00", user(1L));
        when(accountRepository.findById(10L)).thenReturn(Optional.of(account));

        assertThrows(
                InsufficientFundsException.class,
                () -> accountService.withdraw(10L, new BigDecimal("40.00"))
        );

        verify(accountRepository, never()).save(any());
        verifyNoInteractions(operationRepository);
    }

    @Test
    void getAllAccountsDelegatesToRepository() {
        List<AccountModel> accounts = List.of(account(10L, "100.00", user(1L)));
        when(accountRepository.findAll()).thenReturn(accounts);

        assertSame(accounts, accountService.getAllAccounts());
    }

    @Test
    void getAccountByUserIdDelegatesToRepository() {
        List<AccountModel> accounts = List.of(account(10L, "100.00", user(1L)));
        when(accountRepository.findByOwnerId(1L)).thenReturn(accounts);

        assertSame(accounts, accountService.getAccountByUserId(1L));
    }

    private static UserModel user(Long id) {
        UserModel user = new UserModel();
        user.setId(id);
        return user;
    }

    private static AccountModel account(Long id, String balance, UserModel owner) {
        return new AccountModel(id, new BigDecimal(balance), owner);
    }

    private static void assertBigDecimalEquals(String expected, BigDecimal actual) {
        assertEquals(0, new BigDecimal(expected).compareTo(actual));
    }
}
