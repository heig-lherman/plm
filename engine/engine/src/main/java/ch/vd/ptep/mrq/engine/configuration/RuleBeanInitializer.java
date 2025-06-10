package ch.vd.ptep.mrq.engine.configuration;

import ch.vd.ptep.mrq.compiler.core.CompilerService;
import ch.vd.ptep.mrq.compiler.exception.CompilerException;
import ch.vd.ptep.mrq.compiler.model.Statement;
import ch.vd.ptep.mrq.engine.core.rule.CompiledRule;
import ch.vd.ptep.mrq.engine.core.runtime.RuntimeEvaluationService;
import ch.vd.ptep.mrq.model.rule.RuleMetadata;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.core.env.ConfigurableEnvironment;

@Slf4j
public class RuleBeanInitializer implements BeanDefinitionRegistryPostProcessor {

    private final CompilerService compilerService;
    private final RuntimeEvaluationService runtimeService;

    private final Map<String, RuleMetadata> rules;
    private final Map<String, CompiledRule<?>> compiledRules = new ConcurrentHashMap<>();

    RuleBeanInitializer(
        ConfigurableEnvironment environment,
        CompilerService compilerService,
        RuntimeEvaluationService runtimeService
    ) {
        log.info("RuleBeanInitializer initializing");
        this.rules = Binder.get(environment).bind("rules", Bindable.mapOf(String.class, RuleMetadata.class)).get();
        this.compilerService = compilerService;
        this.runtimeService = runtimeService;
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        // We need to wait for other beans to be defined first
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        try {
            if (rules == null || rules.isEmpty()) {
                log.warn("No rules found in configuration");
                return;
            }

            log.info("Initializing {} rules from configuration", rules.size());

            for (Map.Entry<String, RuleMetadata> entry : rules.entrySet()) {
                String ruleId = entry.getKey();
                RuleMetadata definition = entry.getValue();

                try {
                    var rule = createCompiledRule(
                        ruleId,
                        definition,
                        compilerService,
                        runtimeService
                    );

                    // Register as singleton
                    beanFactory.registerSingleton("rule_" + ruleId, rule);
                    compiledRules.put(ruleId, rule);

                    log.info("Successfully registered rule: {} for object type {}", ruleId, definition.object());
                } catch (Exception e) {
                    log.error("Failed to create rule '{}': {}", ruleId, e.getMessage(), e);
                }
            }

            log.info("Rule initialization complete. {} rules registered.", compiledRules.size());

        } catch (Exception e) {
            log.error("Error during rule initialization: {}", e.getMessage(), e);
            throw new BeansException("Failed to initialize rules", e) {
            };
        }
    }

    private CompiledRule<?> createCompiledRule(
        String ruleId,
        RuleMetadata definition,
        CompilerService compilerService,
        RuntimeEvaluationService runtimeService
    ) throws CompilerException {
        // Compile the expression
        log.debug("Compiling expression for rule {}: {}", ruleId, definition.expression());
        Statement compiledStatement = compilerService.compile(definition.expression());

        // Create the compiled rule
        return new CompiledRule<>(
            ruleId,
            "Compiled rule for %s: %s".formatted(ruleId, definition.expression()),
            definition.object().getObjectClass(),
            definition,
            compiledStatement,
            runtimeService
        );
    }
}
