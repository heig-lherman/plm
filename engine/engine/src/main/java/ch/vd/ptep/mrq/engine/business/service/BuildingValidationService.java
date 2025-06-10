package ch.vd.ptep.mrq.engine.business.service;

import ch.vd.ptep.mrq.engine.core.rule.RuleRegistry;
import ch.vd.ptep.mrq.model.Building;
import ch.vd.ptep.mrq.model.rule.Rule;
import ch.vd.ptep.mrq.model.rule.RuleContext;
import ch.vd.ptep.mrq.model.rule.RuleObject;
import ch.vd.ptep.mrq.model.rule.RuleResult;
import ch.vd.ptep.mrq.model.rule.RuleStatus;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BuildingValidationService {

    private final JmsTemplate jmsTemplate;
    private final RuleRegistry ruleRegistry;

    private final ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();

    @JmsListener(destination = "building.validation.queue")
    public void validateBuilding(Building building) {
        log.info("Received building for validation: EGID={}", building.EGID());
        List<Rule<Building>> applicableRules = ruleRegistry.getRulesForObject(
            RuleObject.BUILDING,
            Building.class
        );

        log.debug("Found {} applicable rules for building", applicableRules.size());

        // Execute all rules in parallel
        var context = new RuleContext();
        try (var _a = context.putScoped(Building.class, building)) {
            // NOTE: only check building rules for now, but the context could be passed down the chain to related objects
            var futures = applicableRules.stream().map(rule -> CompletableFuture.supplyAsync(
                () -> {
                    log.debug("Executing rule: {}", rule.name());
                    return rule.validate(building, context);
                },
                executorService
            )).toList();

            // Collect results
            var results = futures.stream()
                .map(CompletableFuture::join)
                .toList();

            // Process results
            processValidationResults(building, results);
        }
    }

    private void processValidationResults(Building building, List<RuleResult> results) {
        long failures = results.stream()
            .filter(r -> r.status() == RuleStatus.INVALID)
            .count();

        long errors = results.stream()
            .filter(r -> r.status() == RuleStatus.NOT_APPLIED || r.status() == RuleStatus.TECHNICAL_ERROR)
            .count();

        if (failures > 0 || errors > 0) {
            log.warn("Building {} failed {} rules and had {} errors", building.EGID(), failures, errors);
            results.forEach(r -> log.warn("  - {}: {} ({}): {}",
                r.status().name(),
                r.name(),
                r.metadata().label(),
                r.metadata().errorMessage())
            );
        } else {
            log.info("Building {} passed all {} validation rules", building.EGID(), results.size());
        }

        ValidationSummary summary = new ValidationSummary(
            building.EGID(),
            results.stream().collect(Collectors.toMap(RuleResult::name, RuleResult::status)),
            results.size(),
            results.stream().filter(r -> r.status() == RuleStatus.VALID).count(),
            failures,
            errors
        );

        log.debug("Validation summary for building {}: {}", building.EGID(), summary);

        jmsTemplate.convertAndSend(
            "building.validation.summary.queue",
            summary
        );
    }

    public record ValidationSummary(
        Long buildingId,
        Map<String, RuleStatus> results,
        long totalRules,
        long passed,
        long failed,
        long errors
    ) {

    }
}
