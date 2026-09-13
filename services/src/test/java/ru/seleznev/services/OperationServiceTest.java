package ru.seleznev.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.seleznev.domain.OperationModel;
import ru.seleznev.enums.OperationType;
import ru.seleznev.repositories.OperationRepository;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OperationServiceTest {

    @Mock
    private OperationRepository operationRepository;

    @InjectMocks
    private OperationService operationService;

    @Test
    void getOperationsByTypeAndAccountIdUsesCombinedFilter() {
        List<OperationModel> operations = List.of(operation(OperationType.DEPOSIT, 10L));
        when(operationRepository.findByTypeAndAccountId(OperationType.DEPOSIT, 10L)).thenReturn(operations);

        List<OperationModel> result = operationService.getOperations(OperationType.DEPOSIT, 10L);

        assertSame(operations, result);
        verify(operationRepository).findByTypeAndAccountId(OperationType.DEPOSIT, 10L);
    }

    @Test
    void getOperationsByTypeUsesTypeFilter() {
        List<OperationModel> operations = List.of(operation(OperationType.WITHDRAW, 10L));
        when(operationRepository.findByType(OperationType.WITHDRAW)).thenReturn(operations);

        assertSame(operations, operationService.getOperations(OperationType.WITHDRAW, null));
    }

    @Test
    void getOperationsByAccountIdUsesAccountFilter() {
        List<OperationModel> operations = List.of(operation(OperationType.TRANSFER_IN, 10L));
        when(operationRepository.findByAccountId(10L)).thenReturn(operations);

        assertSame(operations, operationService.getOperations(null, 10L));
    }

    @Test
    void getOperationsWithoutFiltersReturnsAllOperations() {
        List<OperationModel> operations = List.of(operation(OperationType.TRANSFER_OUT, 10L));
        when(operationRepository.findAll()).thenReturn(operations);

        assertSame(operations, operationService.getOperations(null, null));
    }

    private static OperationModel operation(OperationType type, Long accountId) {
        return new OperationModel(type, BigDecimal.TEN, BigDecimal.ZERO, accountId);
    }
}
