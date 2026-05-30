package ru.seleznev.springdata;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.seleznev.entities.Account;

import java.util.List;

public interface SpringDataAccountRepository extends JpaRepository<Account, Long> {

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, attributePaths = "owner")
    List<Account> findAll();
    @Override
    Account save(Account account);

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, attributePaths = "owner")
    List<Account> findByOwnerId(Long ownerId);
}
