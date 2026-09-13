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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransferServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private OperationRepository operationRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TransferService transferService;

    @Test
    void transferBetweenOwnAccountsMovesMoneyWithoutCommission() {
        UserModel owner = user(1L);
        AccountModel fromAccount = account(10L, "100.00", owner);
        AccountModel toAccount = account(20L, "30.00", owner);

        when(accountRepository.findById(10L)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findById(20L)).thenReturn(Optional.of(toAccount));
        when(userRepository.findWithFriendsById(1L)).thenReturn(Optional.of(owner));
        when(accountRepository.save(any(AccountModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        transferService.transfer(10L, 20L, new BigDecimal("25.00"));

        assertBigDecimalEquals("75.00", fromAccount.getBalance());
        assertBigDecimalEquals("55.00", toAccount.getBalance());

        List<OperationModel> operations = savedOperations();
        assertOperation(operations.get(0), OperationType.TRANSFER_OUT, "25.00", "0.0000", 10L);
        assertOperation(operations.get(1), OperationType.TRANSFER_IN, "25.00", "0", 20L);
    }

    @Test
    void transferToFriendUsesThreePercentCommission() {
        UserModel sender = user(1L);
        UserModel recipient = user(2L);
        sender.addFriend(recipient);

        AccountModel fromAccount = account(10L, "100.00", sender);
        AccountModel toAccount = account(20L, "30.00", recipient);

        when(accountRepository.findById(10L)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findById(20L)).thenReturn(Optional.of(toAccount));
        when(userRepository.findWithFriendsById(1L)).thenReturn(Optional.of(sender));
        when(accountRepository.save(any(AccountModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        transferService.transfer(10L, 20L, new BigDecimal("10.00"));

        assertBigDecimalEquals("89.70", fromAccount.getBalance());
        assertBigDecimalEquals("40.00", toAccount.getBalance());

        List<OperationModel> operations = savedOperations();
        assertOperation(operations.get(0), OperationType.TRANSFER_OUT, "10.00", "0.3000", 10L);
        assertOperation(operations.get(1), OperationType.TRANSFER_IN, "10.00", "0", 20L);
    }

    @Test
    void transferToOtherUserUsesTenPercentCommission() {
        UserModel sender = user(1L);
        UserModel recipient = user(2L);

        AccountModel fromAccount = account(10L, "100.00", sender);
        AccountModel toAccount = account(20L, "30.00", recipient);

        when(accountRepository.findById(10L)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findById(20L)).thenReturn(Optional.of(toAccount));
        when(userRepository.findWithFriendsById(1L)).thenReturn(Optional.of(sender));
        when(accountRepository.save(any(AccountModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        transferService.transfer(10L, 20L, new BigDecimal("10.00"));

        assertBigDecimalEquals("89.00", fromAccount.getBalance());
        assertBigDecimalEquals("40.00", toAccount.getBalance());

        List<OperationModel> operations = savedOperations();
        assertOperation(operations.get(0), OperationType.TRANSFER_OUT, "10.00", "1.0000", 10L);
        assertOperation(operations.get(1), OperationType.TRANSFER_IN, "10.00", "0", 20L);
    }

    @Test
    void transferRejectsNonPositiveAmount() {
        assertThrows(IllegalArgumentException.class, () -> transferService.transfer(10L, 20L, BigDecimal.ZERO));

        verifyNoInteractions(accountRepository, userRepository, operationRepository);
    }

    @Test
    void transferThrowsWhenSenderAccountDoesNotExist() {
        when(accountRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(
                EntityNotFoundException.class,
                () -> transferService.transfer(10L, 20L, new BigDecimal("10.00"))
        );

        verify(accountRepository, never()).save(any());
        verifyNoInteractions(userRepository, operationRepository);
    }

    @Test
    void transferThrowsWhenBalanceIsInsufficientForAmountAndCommission() {
        UserModel sender = user(1L);
        UserModel recipient = user(2L);
        AccountModel fromAccount = account(10L, "10.50", sender);
        AccountModel toAccount = account(20L, "30.00", recipient);

        when(accountRepository.findById(10L)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findById(20L)).thenReturn(Optional.of(toAccount));
        when(userRepository.findWithFriendsById(1L)).thenReturn(Optional.of(sender));

        assertThrows(
                InsufficientFundsException.class,
                () -> transferService.transfer(10L, 20L, new BigDecimal("10.00"))
        );

        assertBigDecimalEquals("10.50", fromAccount.getBalance());
        assertBigDecimalEquals("30.00", toAccount.getBalance());
        verify(accountRepository, never()).save(any());
        verifyNoInteractions(operationRepository);
    }

    private List<OperationModel> savedOperations() {
        ArgumentCaptor<OperationModel> operationCaptor = ArgumentCaptor.forClass(OperationModel.class);
        verify(operationRepository, times(2)).save(operationCaptor.capture());
        return operationCaptor.getAllValues();
    }

    private static UserModel user(Long id) {
        UserModel user = new UserModel();
        user.setId(id);
        return user;
    }

    private static AccountModel account(Long id, String balance, UserModel owner) {
        return new AccountModel(id, new BigDecimal(balance), owner);
    }

    private static void assertOperation(
            OperationModel operation,
            OperationType expectedType,
            String expectedAmount,
            String expectedCommission,
            Long expectedAccountId
    ) {
        assertEquals(expectedType, operation.getType());
        assertBigDecimalEquals(expectedAmount, operation.getAmount());
        assertBigDecimalEquals(expectedCommission, operation.getCommission());
        assertEquals(expectedAccountId, operation.getAccountId());
    }

    private static void assertBigDecimalEquals(String expected, BigDecimal actual) {
        assertEquals(0, new BigDecimal(expected).compareTo(actual));
    }
}
