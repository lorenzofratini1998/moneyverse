package it.moneyverse.transaction.runtime.controllers;

import it.moneyverse.transaction.model.dto.*;
import it.moneyverse.transaction.services.SubscriptionService;
import it.moneyverse.transaction.services.TagService;
import it.moneyverse.transaction.services.TransactionService;
import it.moneyverse.transaction.services.TransferService;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "${spring.security.base-path}")
@Validated
public class TransactionManagementController
    implements TransactionOperations, TransferOperations, TagOperations, SubscriptionOperations {

  private final TransactionService transactionService;
  private final TransferService transferService;
  private final TagService tagService;
  private final SubscriptionService subscriptionService;

  public TransactionManagementController(
      TransactionService transactionService,
      TransferService transferService,
      TagService tagService,
      SubscriptionService subscriptionService) {
    this.transactionService = transactionService;
    this.transferService = transferService;
    this.tagService = tagService;
    this.subscriptionService = subscriptionService;
  }

  @Override
  @PostMapping("/transactions")
  @ResponseStatus(HttpStatus.CREATED)
  @PreAuthorize("@securityService.isAuthenticatedUserOwner(#request.userId())")
  public List<TransactionDto> createTransaction(@RequestBody TransactionRequestDto request) {
    return transactionService.createTransactions(request);
  }

  @Override
  @GetMapping("/transactions/users/{userId}")
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize("@securityService.isAuthenticatedUserOwner(#userId)")
  public List<TransactionDto> getTransactions(
      @PathVariable UUID userId, TransactionCriteria criteria) {
    return transactionService.getTransactions(userId, criteria);
  }

  @Override
  @GetMapping("/transactions/{transactionId}")
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize(
      "@transactionRepository.existsByUserIdAndTransactionId(@securityService.getAuthenticatedUserId(), #transactionId)")
  public TransactionDto getTransaction(@PathVariable UUID transactionId) {
    return transactionService.getTransaction(transactionId);
  }

  @Override
  @PutMapping("/transactions/{transactionId}")
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize(
      "@transactionRepository.existsByUserIdAndTransactionId(@securityService.getAuthenticatedUserId(), #transactionId)")
  public TransactionDto updateTransaction(
      @PathVariable UUID transactionId, @RequestBody TransactionUpdateRequestDto request) {
    return transactionService.updateTransaction(transactionId, request);
  }

  @Override
  @DeleteMapping("/transactions/{transactionId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PreAuthorize(
      "@transactionRepository.existsByUserIdAndTransactionId(@securityService.getAuthenticatedUserId(), #transactionId)")
  public void deleteTransaction(@PathVariable UUID transactionId) {
    transactionService.deleteTransaction(transactionId);
  }

  @Override
  @PostMapping("/transfer")
  @ResponseStatus(HttpStatus.CREATED)
  @PreAuthorize("@securityService.isAuthenticatedUserOwner(#request.userId())")
  public TransferDto createTransfer(@RequestBody TransferRequestDto request) {
    return transferService.createTransfer(request);
  }

  @Override
  @PutMapping("/transfer/{transferId}")
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize(
      "@transferRepository.existsByTransactionFrom_UserIdAndTransactionTo_UserIdAndTransferId(@securityService.getAuthenticatedUserId(), @securityService.getAuthenticatedUserId(), #transferId)")
  public TransferDto updateTransfer(
      @PathVariable UUID transferId, @RequestBody TransferUpdateRequestDto request) {
    return transferService.updateTransfer(transferId, request);
  }

  @Override
  @DeleteMapping("/transfer/{transferId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PreAuthorize(
      "@transferRepository.existsByTransactionFrom_UserIdAndTransactionTo_UserIdAndTransferId(@securityService.getAuthenticatedUserId(), @securityService.getAuthenticatedUserId(), #transferId)")
  public void deleteTransfer(@PathVariable UUID transferId) {
    transferService.deleteTransfer(transferId);
  }

  @Override
  @GetMapping("/transfer/{transferId}")
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize(
      "@transferRepository.existsByTransactionFrom_UserIdAndTransactionTo_UserIdAndTransferId(@securityService.getAuthenticatedUserId(), @securityService.getAuthenticatedUserId(), #transferId)")
  public TransferDto getTransactionsByTransferId(@PathVariable UUID transferId) {
    return transferService.getTransactionsByTransferId(transferId);
  }

  @Override
  @PostMapping("/tags")
  @ResponseStatus(HttpStatus.CREATED)
  @PreAuthorize("@securityService.isAuthenticatedUserOwner(#request.userId())")
  public TagDto createTag(@RequestBody TagRequestDto request) {
    return tagService.createTag(request);
  }

  @Override
  @GetMapping("/tags/users/{userId}")
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize("@securityService.isAuthenticatedUserOwner(#userId)")
  public List<TagDto> getUserTags(@PathVariable UUID userId) {
    return tagService.getUserTags(userId);
  }

  @Override
  @GetMapping("/tags/{tagId}")
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize(
      "@tagRepository.existsByTagIdAndUserId(#tagId, @securityService.getAuthenticatedUserId())")
  public TagDto getTag(@PathVariable UUID tagId) {
    return tagService.getTagById(tagId);
  }

  @Override
  @PutMapping("/tags/{tagId}")
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize(
      "@tagRepository.existsByTagIdAndUserId(#tagId, @securityService.getAuthenticatedUserId())")
  public TagDto updateTag(@PathVariable UUID tagId, @RequestBody TagUpdateRequestDto request) {
    return tagService.updateTag(tagId, request);
  }

  @Override
  @DeleteMapping("/tags/{tagId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PreAuthorize(
      "@tagRepository.existsByTagIdAndUserId(#tagId, @securityService.getAuthenticatedUserId())")
  public void deleteTag(@PathVariable UUID tagId) {
    tagService.deleteTag(tagId);
  }

  @Override
  @PostMapping("/subscriptions")
  @ResponseStatus(HttpStatus.CREATED)
  @PreAuthorize("@securityService.isAuthenticatedUserOwner(#request.userId())")
  public SubscriptionDto createSubscription(@RequestBody SubscriptionRequestDto request) {
    return subscriptionService.createSubscription(request);
  }

  @Override
  @GetMapping("/subscriptions/{subscriptionId}")
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize(
      "@subscriptionRepository.existsBySubscriptionIdAndUserId(#subscriptionId, @securityService.getAuthenticatedUserId())")
  public SubscriptionDto getSubscription(@PathVariable UUID subscriptionId) {
    return subscriptionService.getSubscription(subscriptionId);
  }

  @Override
  @GetMapping("/subscriptions/users/{userId}")
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize("@securityService.isAuthenticatedUserOwner(#userId)")
  public List<SubscriptionDto> getSubscriptions(@PathVariable UUID userId) {
    return subscriptionService.getSubscriptionsByUserId(userId);
  }

  @Override
  @DeleteMapping("/subscriptions/{subscriptionId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PreAuthorize(
      "@subscriptionRepository.existsBySubscriptionIdAndUserId(#subscriptionId, @securityService.getAuthenticatedUserId())")
  public void deleteSubscription(@PathVariable UUID subscriptionId) {
    subscriptionService.deleteSubscription(subscriptionId);
  }
}
