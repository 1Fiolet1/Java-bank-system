package ru.seleznev.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.seleznev.domain.AccountModel;
import ru.seleznev.dto.RateUpdate;
import ru.seleznev.dto.accounts.AccountDtoMapper;
import ru.seleznev.dto.accounts.AccountResponse;
import ru.seleznev.dto.accounts.CurrencyBalanceResponse;
import ru.seleznev.dto.accounts.MoneyOperationRequest;
import ru.seleznev.dto.transfers.TransferRequest;
import ru.seleznev.dto.users.UserDtoMapper;
import ru.seleznev.dto.users.UserResponse;
import ru.seleznev.services.RateExchangeService;
import ru.seleznev.security.CurrentUserService;
import ru.seleznev.services.AccountService;
import ru.seleznev.services.TransferService;
import ru.seleznev.services.UserService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@RestController
@RequestMapping("/me")
public class MeController {
    private final UserDtoMapper userDtoMapper;
    private final AccountService accountService;
    private final AccountDtoMapper accountDtoMapper;
    private final TransferService transferService;
    private final UserService userService;
    private final CurrentUserService currentUserService;
    private final RateExchangeService rateExchangeService;

    @Autowired
    public MeController(UserDtoMapper userDtoMapper,
                        AccountService accountService,
                        AccountDtoMapper accountDtoMapper,
                        TransferService transferService,
                        UserService userService,
                        CurrentUserService currentUserService,
                        RateExchangeService rateExchangeService)
    {
        this.userDtoMapper = userDtoMapper;
        this.accountService = accountService;
        this.accountDtoMapper = accountDtoMapper;
        this.transferService = transferService;
        this.userService = userService;
        this.currentUserService = currentUserService;
        this.rateExchangeService = rateExchangeService;
    }

    @GetMapping
    @Operation(summary = "Get current client")
    @ApiResponse(responseCode = "200",description = "OK")
    @ApiResponse(responseCode = "401",description = "Unauthorized")
    @ApiResponse(responseCode = "403",description = "Forbidden")
    public UserResponse getMe(Authentication authentication) {
        Long userId = currentUserService.getCurrentClientId(authentication);

        return userDtoMapper.toResponse(userService.getUserById(userId));
    }

    @GetMapping("/accounts")
    @Operation(summary = "Get current client accounts")
    @ApiResponse(responseCode = "200",description = "OK")
    @ApiResponse(responseCode = "401",description = "Unauthorized")
    @ApiResponse(responseCode = "403",description = "Forbidden")
    public List<AccountResponse> getMyAccounts(Authentication authentication) {
        Long userId = currentUserService.getCurrentClientId(authentication);

        return accountService.getAccountByUserId(userId).stream()
                .map(accountDtoMapper::toResponse)
                .toList();
    }

    @GetMapping("/accounts/{accountId}/balance")
    @Operation(summary = "Get account balance in currency")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "400", description = "Bad request")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @ApiResponse(responseCode = "404", description = "Not found")
    public CurrencyBalanceResponse getAccountBalanceInCurrency(@PathVariable Long accountId,
                                                               @RequestParam String currency,
                                                               Authentication authentication)
    {
        checkAccountOwner(accountId, authentication);

        AccountModel account = accountService.getAccountById(accountId);
        BigDecimal balanceInRub = account.getBalance();

        RateUpdate rate = rateExchangeService.getRate(currency);
        BigDecimal converted = balanceInRub.divide(rate.getRateToRub(), 4, RoundingMode.HALF_UP);

        return new CurrencyBalanceResponse(
                accountId,
                rate.getCurrency(),
                converted,
                rate.getRateToRub(),
                rate.getTimestamp()
        );
    }

    @GetMapping("/accounts/{accountId}")
    @Operation(summary = "Get current client account by id")
    @ApiResponse(responseCode = "200",description = "OK")
    @ApiResponse(responseCode = "401",description = "Unauthorized")
    @ApiResponse(responseCode = "403",description = "Forbidden")
    @ApiResponse(responseCode = "404",description = "Not found")
    public AccountResponse getMyAccountById(@PathVariable Long accountId, Authentication authentication) {
        checkAccountOwner(accountId, authentication);

        AccountModel account = accountService.getAccountById(accountId);

        return accountDtoMapper.toResponse(account);
    }

    @PostMapping("/accounts/{accountId}/deposit")
    @Operation(summary = "Deposit money to current client account")
    @ApiResponse(responseCode = "200",description = "OK")
    @ApiResponse(responseCode = "400",description = "Bad request")
    @ApiResponse(responseCode = "401",description = "Unauthorized")
    @ApiResponse(responseCode = "403",description = "Forbidden")
    @ApiResponse(responseCode = "404",description = "Not found")
    public AccountResponse depositToMyAccount(@PathVariable Long accountId,
                                              @RequestBody MoneyOperationRequest request,
                                              Authentication authentication)
    {
        checkAccountOwner(accountId,authentication);

        return accountDtoMapper.toResponse(accountService.deposit(accountId, request.getAmount()));
    }

    @PostMapping("/accounts/{accountId}/withdraw")
    @Operation(summary = "Withdraw money from current client account")
    @ApiResponse(responseCode = "200",description = "OK")
    @ApiResponse(responseCode = "400",description = "Bad request")
    @ApiResponse(responseCode = "401",description = "Unauthorized")
    @ApiResponse(responseCode = "403",description = "Forbidden")
    @ApiResponse(responseCode = "404",description = "Not found")
    public AccountResponse withdrawFromMyAccount(@PathVariable Long accountId,
                                                 @RequestBody MoneyOperationRequest request,
                                                 Authentication authentication)
    {
        checkAccountOwner(accountId, authentication);

        return accountDtoMapper.toResponse(accountService.withdraw(accountId, request.getAmount()));
    }

    @PostMapping("/transfers")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Transfer money from current client account")
    @ApiResponse(responseCode = "204",description = "No content")
    @ApiResponse(responseCode = "400",description = "Bad request")
    @ApiResponse(responseCode = "401",description = "Unauthorized")
    @ApiResponse(responseCode = "403",description = "Forbidden")
    @ApiResponse(responseCode = "404",description = "Not found")
    public void transferFromMyAccount(@RequestBody TransferRequest request, Authentication authentication) {
        checkAccountOwner(request.getFromAccountId(), authentication);

        transferService.transfer(
                request.getFromAccountId(),
                request.getToAccountId(),
                request.getAmount()
        );
    }

    @PostMapping("/friends/{friendId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Add friend to current client")
    @ApiResponse(responseCode = "204",description = "No content")
    @ApiResponse(responseCode = "400",description = "Bad request")
    @ApiResponse(responseCode = "401",description = "Unauthorized")
    @ApiResponse(responseCode = "403",description = "Forbidden")
    @ApiResponse(responseCode = "404",description = "Not found")
    public void addMyFriend(@PathVariable Long friendId, Authentication authentication) {
        Long userId = currentUserService.getCurrentClientId(authentication);

        userService.addFriend(userId, friendId);
    }

    @DeleteMapping("/friends/{friendId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Remove friend from current client")
    @ApiResponse(responseCode = "204",description = "No content")
    @ApiResponse(responseCode = "400",description = "Bad request")
    @ApiResponse(responseCode = "401",description = "Unauthorized")
    @ApiResponse(responseCode = "403",description = "Forbidden")
    @ApiResponse(responseCode = "404",description = "Not found")
    public void removeMyFriend(@PathVariable Long friendId, Authentication authentication) {
        Long userId = currentUserService.getCurrentClientId(authentication);

        userService.removeFriend(userId, friendId);
    }

    private void checkAccountOwner(Long accountId, Authentication authentication) {
        Long userId = currentUserService.getCurrentClientId(authentication);

        AccountModel account = accountService.getAccountById(accountId);

        if (!account.getOwner().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }
}
