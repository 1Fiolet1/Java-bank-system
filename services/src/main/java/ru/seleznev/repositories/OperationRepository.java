package ru.seleznev.repositories;

import ru.seleznev.domain.OperationModel;
import ru.seleznev.enums.OperationType;

import java.util.List;
import java.util.Optional;

public interface OperationRepository {

    OperationModel save(OperationModel operation);


    Optional<OperationModel> findById(Long id);

    List<OperationModel> findAll();

    List<OperationModel> findByAccountId(Long accountId);

    List<OperationModel> findByType(OperationType type);

    List<OperationModel> findByTypeAndAccountId(OperationType type, Long accountId);
}
