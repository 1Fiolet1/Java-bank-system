package ru.seleznev.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.seleznev.domain.AccountModel;
import ru.seleznev.dto.accounts.*;
import ru.seleznev.dto.operations.OperationDtoMapper;
import ru.seleznev.services.AccountService;
import ru.seleznev.services.OperationService;

import java.util.List;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final OperationService operationService;
    private final OperationDtoMapper operationDtoMapper;
    private final AccountService accountService;
    private final AccountDtoMapper accountDtoMapper;

    @Autowired
    public AccountController(OperationDtoMapper operationDtoMapper,
                             OperationService operationService,
                             AccountService accountService,
                             AccountDtoMapper accountDtoMapper)
    {
        this.operationDtoMapper = operationDtoMapper;
        this.operationService = operationService;
        this.accountService = accountService;
        this.accountDtoMapper = accountDtoMapper;
    }

    @GetMapping
    @Operation(summary = "Get all accounts")
    @ApiResponse(responseCode = "200", description = "Ok")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    public List<AccountResponse> getAllAccounts() {
        return accountService.getAllAccounts().stream()
                .map(accountDtoMapper::toResponse)
                .toList();
    }

    @GetMapping("/by-user/{userId}")
    @Operation(summary = "Get user accounts")
    @ApiResponse(responseCode = "200", description = "Ok")
    @ApiResponse(responseCode = "400", description = "Bad request")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    public List<AccountResponse> getAccountByUserId(@PathVariable Long userId) {
        return accountService.getAccountByUserId(userId).stream()
                .map(accountDtoMapper::toResponse)
                .toList();
    }

    @GetMapping("/{accountId}")
    @Operation(summary = "Get account with operations")
    @ApiResponse(responseCode = "200", description = "Ok")
    @ApiResponse(responseCode = "400", description = "Bad request")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @ApiResponse(responseCode = "404", description = "Not found")
    public AccountWithOperationsResponse getAccountById(@PathVariable Long accountId) {
        AccountModel account = accountService.getAccountById(accountId);

        return new AccountWithOperationsResponse(
                account.getId(),
                account.getBalance(),
                account.getOwner().getId(),
                operationService.getOperations(null, accountId).stream()
                        .map(operationDtoMapper::toResponse)
                        .toList()
        );
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create account")
    @ApiResponse(responseCode = "201", description = "Created")
    @ApiResponse(responseCode = "400", description = "Bad request")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @ApiResponse(responseCode = "404", description = "Not found")
    public AccountResponse createAccount(@RequestBody CreateAccountRequest request) {
        AccountModel account = accountService.createAccount(request.getUserId());

        return accountDtoMapper.toResponse(account);
    }
}
