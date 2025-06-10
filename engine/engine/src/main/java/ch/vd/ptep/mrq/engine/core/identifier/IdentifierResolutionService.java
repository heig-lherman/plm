package ch.vd.ptep.mrq.engine.core.identifier;

import ch.vd.ptep.mrq.model.rule.RuleContext;
import ch.vd.ptep.mrq.model.rule.RuleObject;
import java.lang.reflect.RecordComponent;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class IdentifierResolutionService {

    private final Map<RuleObject, Map<String, RecordComponent>> identifierMap;

    public IdentifierResolutionService() {
        identifierMap = Arrays.stream(RuleObject.values()).collect(Collectors.toMap(
            Function.identity(),
            object -> Arrays.stream(object.getObjectClass().getRecordComponents()).collect(Collectors.toMap(
                RecordComponent::getName,
                Function.identity()
            ))
        ));
    }

    public boolean isPartOfObject(RuleObject object, String identifier) {
        return identifierMap.containsKey(object) && identifierMap.get(object).containsKey(identifier);
    }

    public RuleObject findObject(String identifier) {
        return identifierMap.entrySet().stream()
            .filter(entry -> entry.getValue().containsKey(identifier))
            .map(Map.Entry::getKey)
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException(
                "Identifier '%s' does not belong to any RuleObject".formatted(identifier)));
    }

    public Object resolveIdentifier(RuleContext context, RuleObject object, String identifier) {
        if (!isPartOfObject(object, identifier)) {
            throw new IllegalArgumentException(
                "Identifier '%s' is not part of object '%s'".formatted(identifier, object));
        }

        try {
            Object targetObject = context.get(object.getObjectClass());
            RecordComponent component = identifierMap.get(object).get(identifier);
            return component.getAccessor().invoke(targetObject);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(
                "Failed to resolve identifier '%s' for object '%s'".formatted(identifier, object),
                e
            );
        }
    }

    public Object resolveIdentifier(Object targetObject, String identifier) {
        RuleObject ruleObject = RuleObject.from(targetObject.getClass());
        if (!isPartOfObject(ruleObject, identifier)) {
            throw new IllegalArgumentException(
                "Identifier '%s' is not part of object '%s'".formatted(identifier, ruleObject));
        }

        try {
            RecordComponent component = identifierMap.get(ruleObject).get(identifier);
            return component.getAccessor().invoke(targetObject);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(
                "Failed to resolve identifier '%s' for object '%s'".formatted(identifier, ruleObject),
                e
            );
        }
    }
}
