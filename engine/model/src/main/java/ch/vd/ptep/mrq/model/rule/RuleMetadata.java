package ch.vd.ptep.mrq.model.rule;

public record RuleMetadata(
    RuleObject object,
    RuleLevel level,
    Field field,

    String label,
    String errorMessage,

    String expression
) {

    public record Field(
        String name
    ) {

    }
}
