package ch.vd.ptep.mrq.engine.configuration;

import ch.vd.ptep.mrq.compiler.core.CompilerService;
import ch.vd.ptep.mrq.engine.core.identifier.IdentifierResolutionService;
import ch.vd.ptep.mrq.engine.core.runtime.RuntimeEvaluationService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.ConfigurableEnvironment;

@PropertySource(value = "classpath:rules.yaml", factory = YamlPropertySourceFactory.class)
@Configuration(proxyBeanMethods = false)
public class RuleConfiguration {

    @Bean
    public IdentifierResolutionService identifierResolutionService() {
        return new IdentifierResolutionService();
    }

    @Bean
    public RuntimeEvaluationService runtimeEvaluationService(
        ApplicationContext applicationContext,
        IdentifierResolutionService identifierResolutionService
    ) {
        return new RuntimeEvaluationService(
            applicationContext,
            identifierResolutionService
        );
    }

    @Bean
    public RuleBeanInitializer ruleBeanInitializer(
        ConfigurableEnvironment environment,
        CompilerService compilerService,
        RuntimeEvaluationService runtimeService
    ) {
        return new RuleBeanInitializer(environment, compilerService, runtimeService);
    }
}
