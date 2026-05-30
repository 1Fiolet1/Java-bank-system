package ru.seleznev.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.seleznev.domain.OperationModel;
import ru.seleznev.enums.OperationType;
import ru.seleznev.repositories.OperationRepository;

import java.util.List;

@Service
public class OperationService {

    private final OperationRepository operationRepository;

    @Autowired
    public OperationService(OperationRepository operationRepository) {
        this.operationRepository = operationRepository;
    }

    @Transactional(readOnly = true)
    public List<OperationModel> getOperations(OperationType type, Long accountId) {
        if (type != null && accountId != null) {
            return operationRepository.findByTypeAndAccountId(type, accountId);
        }

        if (type != null) {
            return operationRepository.findByType(type);
        }

        if (accountId != null) {
            return operationRepository.findByAccountId(accountId);
        }

        return operationRepository.findAll();
    }
}
