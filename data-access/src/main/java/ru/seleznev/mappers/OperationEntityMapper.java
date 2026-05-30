package ru.seleznev.mappers;

import org.springframework.stereotype.Component;
import ru.seleznev.domain.OperationModel;
import ru.seleznev.entities.Account;
import ru.seleznev.entities.Operation;

@Component
public class OperationEntityMapper {

    public OperationModel toModel(Operation operation) {
        return new OperationModel(
                operation.getId(),
                operation.getType(),
                operation.getAmount(),
                operation.getCommission(),
                operation.getCreatedAt(),
                operation.getAccount().getId()
        );
    }

    public Operation toEntity(OperationModel model, Account account) {
        Operation operation = new Operation(
                model.getType(),
                model.getAmount(),
                model.getCommission()
        );

        operation.setId(model.getId());
        operation.setCreatedAt(model.getCreatedAt());
        operation.setAccount(account);

        return operation;
    }
}
