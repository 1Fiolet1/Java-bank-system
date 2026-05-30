package ru.seleznev.dto.operations;

import org.springframework.stereotype.Component;
import ru.seleznev.domain.OperationModel;

@Component
public class OperationDtoMapper {

    public OperationResponse toResponse(OperationModel operation) {
        return new OperationResponse(
                operation.getId(),
                operation.getType(),
                operation.getAmount(),
                operation.getCommission(),
                operation.getCreatedAt(),
                operation.getAccountId()
        );
    }
}
