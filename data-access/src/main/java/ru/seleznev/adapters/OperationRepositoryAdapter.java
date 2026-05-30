package ru.seleznev.adapters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.seleznev.domain.OperationModel;
import ru.seleznev.entities.Account;
import ru.seleznev.enums.OperationType;
import ru.seleznev.exceptions.EntityNotFoundException;
import ru.seleznev.mappers.OperationEntityMapper;
import ru.seleznev.repositories.OperationRepository;
import ru.seleznev.springdata.SpringDataAccountRepository;
import ru.seleznev.springdata.SpringDataOperationRepository;

import java.util.List;
import java.util.Optional;

@Component
public class OperationRepositoryAdapter implements OperationRepository {

    private final SpringDataAccountRepository accountRepository;
    private final SpringDataOperationRepository operationRepository;
    private final OperationEntityMapper mapper;

    @Autowired
    public OperationRepositoryAdapter(SpringDataAccountRepository accountRepository, SpringDataOperationRepository operationRepository, OperationEntityMapper mapper) {
        this.accountRepository = accountRepository;
        this.operationRepository = operationRepository;
        this.mapper = mapper;
    }


    @Override
    public OperationModel save(OperationModel operation) {
        Account account = accountRepository.findById(operation.getAccountId())
                .orElseThrow(() -> new EntityNotFoundException("account not found"));

        return mapper.toModel(operationRepository.save(mapper.toEntity(operation, account)));
    }

    @Override
    public Optional<OperationModel> findById(Long id) {
        return operationRepository.findById(id).map(mapper::toModel);
    }

    @Override
    public List<OperationModel> findAll() {
        return operationRepository.findAll().stream()
                .map(mapper::toModel)
                .toList();
    }

    @Override
    public List<OperationModel> findByAccountId(Long accountId) {
        return operationRepository.findByAccountId(accountId).stream()
                .map(mapper::toModel)
                .toList();
    }

    @Override
    public List<OperationModel> findByType(OperationType type) {
        return operationRepository.findByType(type).stream()
                .map(mapper::toModel)
                .toList();
    }

    @Override
    public List<OperationModel> findByTypeAndAccountId(OperationType type, Long accountId) {
        return operationRepository.findByTypeAndAccountId(type, accountId).stream()
                .map(mapper::toModel)
                .toList();
    }
}
