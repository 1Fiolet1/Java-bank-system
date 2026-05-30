package ru.seleznev.springdata;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.seleznev.entities.Operation;
import ru.seleznev.enums.OperationType;


import java.util.List;

public interface SpringDataOperationRepository extends JpaRepository<Operation, Long>{

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, attributePaths = "account")
    List<Operation> findAll();
    Operation save(Operation operation);

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, attributePaths = "account")
    List<Operation> findByAccountId(Long accountId);

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, attributePaths = "account")
    List<Operation> findByType(OperationType type);

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, attributePaths = "account")
    List<Operation> findByTypeAndAccountId(OperationType type, Long accountId);
}
