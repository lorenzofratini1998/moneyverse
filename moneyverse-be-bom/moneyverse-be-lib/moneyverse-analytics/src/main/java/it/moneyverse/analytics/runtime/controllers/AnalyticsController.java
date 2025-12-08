package it.moneyverse.analytics.runtime.controllers;

import it.moneyverse.analytics.model.dto.*;
import it.moneyverse.analytics.services.AccountAnalyticsService;
import it.moneyverse.analytics.services.CategoryAnalyticsService;
import it.moneyverse.analytics.services.OverviewAnalyticsService;
import it.moneyverse.analytics.services.TransactionAnalyticsService;
import it.moneyverse.core.model.events.SseEmitterRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping(value = "${spring.security.base-path}")
@Validated
public class AnalyticsController implements AccountAnalyticsOperations, CategoryAnalyticsOperations, TransactionAnalyticsOperations, OverviewAnalyticsOperations {

    private final AccountAnalyticsService accountAnalyticsService;
    private final CategoryAnalyticsService categoryAnalyticsService;
    private final TransactionAnalyticsService transactionAnalyticsService;
    private final OverviewAnalyticsService overviewAnalyticsService;
    private final SseEmitterRepository sseEmitterRepository;

    public AnalyticsController(AccountAnalyticsService accountAnalyticsService, CategoryAnalyticsService categoryAnalyticsService, TransactionAnalyticsService transactionAnalyticsService, OverviewAnalyticsService overviewAnalyticsService, SseEmitterRepository sseEmitterRepository) {
        this.accountAnalyticsService = accountAnalyticsService;
        this.categoryAnalyticsService = categoryAnalyticsService;
        this.transactionAnalyticsService = transactionAnalyticsService;
        this.overviewAnalyticsService = overviewAnalyticsService;
        this.sseEmitterRepository = sseEmitterRepository;
    }

    @Override
    @PostMapping("/accounts/kpi")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@securityService.isAuthenticatedUserOwner(#filter.userId)")
    public AccountAnalyticsKpiDto calculateAccountKpi(@RequestBody FilterDto filter) {
        return accountAnalyticsService.calculateKpi(filter);
    }

    @Override
    @PostMapping("/accounts/distribution")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@securityService.isAuthenticatedUserOwner(#filter.userId)")
    public List<AccountAnalyticsDistributionDto> calculateAccountDistribution(@RequestBody FilterDto filter) {
        return accountAnalyticsService.calculateDistribution(filter);
    }

    @Override
    @PostMapping("/accounts/trend")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@securityService.isAuthenticatedUserOwner(#filter.userId)")
    public List<AccountAnalyticsTrendDto> calculateAccountTrend(@RequestBody FilterDto filter) {
        return accountAnalyticsService.calculateTrend(filter);
    }

    @Override
    @PostMapping("/categories/kpi")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@securityService.isAuthenticatedUserOwner(#filter.userId)")
    public CategoryAnalyticsKpiDto calculateCategoryKpi(@RequestBody FilterDto filter) {
        return categoryAnalyticsService.calculateKpi(filter);
    }

    @Override
    @PostMapping("/categories/distribution")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@securityService.isAuthenticatedUserOwner(#filter.userId)")
    public List<CategoryAnalyticsDistributionDto> calculateCategoryDistribution(@RequestBody FilterDto filter) {
        return categoryAnalyticsService.calculateDistribution(filter);
    }

    @Override
    @PostMapping("/categories/trend")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@securityService.isAuthenticatedUserOwner(#filter.userId)")
    public List<CategoryAnalyticsTrendDto> calculateCategoryTrend(@RequestBody FilterDto filter) {
        return categoryAnalyticsService.calculateTrend(filter);
    }

    @Override
    @PostMapping("/transactions/kpi")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@securityService.isAuthenticatedUserOwner(#filter.userId)")
    public TransactionAnalyticsKpiDto calculateTransactionKpi(@RequestBody FilterDto filter) {
        return transactionAnalyticsService.calculateKpi(filter);
    }

    @Override
    @PostMapping("/transactions/distribution")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@securityService.isAuthenticatedUserOwner(#filter.userId)")
    public TransactionAnalyticsDistributionDto calculateTransactionDistribution(@RequestBody FilterDto filter) {
        return transactionAnalyticsService.calculateDistribution(filter);
    }

    @Override
    @PostMapping("/transactions/trend")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@securityService.isAuthenticatedUserOwner(#filter.userId)")
    public TransactionAnalyticsTrendDto calculateTransactionTrend(@RequestBody FilterDto filter) {
        return transactionAnalyticsService.calculateTrend(filter);
    }

    @Override
    @PostMapping("/overview")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@securityService.isAuthenticatedUserOwner(#request.userId)")
    public List<OverviewAnalyticsDto> calculateOverview(@RequestBody UserIdRequest request) {
        return overviewAnalyticsService.calculateOverview(request.userId());
    }

    @GetMapping(value = "/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public SseEmitter subscribe(@RequestParam UUID userId) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        sseEmitterRepository.add(userId, emitter);

        emitter.onCompletion(() -> sseEmitterRepository.remove(userId, emitter));
        emitter.onTimeout(() -> sseEmitterRepository.remove(userId, emitter));
        emitter.onError((error) -> sseEmitterRepository.remove(userId, emitter));

        return emitter;
    }
}
