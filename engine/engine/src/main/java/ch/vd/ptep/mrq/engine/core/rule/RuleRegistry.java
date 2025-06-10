package ch.vd.ptep.mrq.engine.core.rule;

import ch.vd.ptep.mrq.model.rule.Rule;
import ch.vd.ptep.mrq.model.rule.RuleObject;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public final class RuleRegistry {

    private final ApplicationContext applicationContext;

    @SuppressWarnings("unchecked")
    public <T> List<Rule<T>> getRulesForObject(RuleObject object, Class<T> objectClass) {
        var ruleBeans = applicationContext.getBeansOfType(Rule.class);
        List<Rule<T>> applicableRules = ruleBeans.values().stream()
            .filter(rule -> rule.metadata().object() == object && objectClass.isAssignableFrom(rule.getRootClass()))
            .map(rule -> (Rule<T>) rule)
            .toList();

        log.debug("Found {} applicable rules for object type {} ({})",
            applicableRules.size(), object, objectClass.getSimpleName());

        return applicableRules;
    }

    @SuppressWarnings("unchecked")
    public <T> Optional<Rule<T>> getRule(String name, Class<T> objectClass) {
        try {
            Rule<T> rule = applicationContext.getBean("rule_" + name, Rule.class);
            if (objectClass.isAssignableFrom(rule.getRootClass())) {
                return Optional.of(rule);
            }
            log.warn("Rule {} is not compatible with class {}", name, objectClass);
            return Optional.empty();
        } catch (BeansException e) {
            log.debug("Rule {} not found", name);
            return Optional.empty();
        }
    }
}
