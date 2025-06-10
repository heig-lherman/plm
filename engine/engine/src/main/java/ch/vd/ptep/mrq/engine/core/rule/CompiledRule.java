package ch.vd.ptep.mrq.engine.core.rule;

import ch.vd.ptep.mrq.compiler.model.Statement;
import ch.vd.ptep.mrq.engine.core.runtime.RuntimeEvaluationService;
import ch.vd.ptep.mrq.model.rule.Rule;
import ch.vd.ptep.mrq.model.rule.RuleContext;
import ch.vd.ptep.mrq.model.rule.RuleMetadata;
import ch.vd.ptep.mrq.model.rule.RuleResult;
import ch.vd.ptep.mrq.model.rule.RuleStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;

@Slf4j
@RequiredArgsConstructor
public final class CompiledRule<T> implements Rule<T> {

    private final String name;
    private final String description;
    private final Class<T> rootObjectClass;
    private final RuleMetadata metadata;
    private final Statement compiledStatement;
    private final RuntimeEvaluationService runtimeService;

    @Override
    public String name() {
        return name;
    }

    @Override
    public String description() {
        return description;
    }

    @Override
    public RuleMetadata metadata() {
        return metadata;
    }

    @Override
    public Class<T> getRootClass() {
        return rootObjectClass;
    }

    @Override
    public RuleResult validate(@NonNull T object, @NonNull RuleContext context) {
        try {
            return runtimeService.validate(
                compiledStatement,
                name,
                metadata,
                context
            );
        } catch (Exception e) {
            log.error("Error executing rule '{}': {}", name, e.getMessage(), e);
            return new RuleResult(
                name,
                metadata,
                RuleStatus.TECHNICAL_ERROR
            );
        }
    }
}
