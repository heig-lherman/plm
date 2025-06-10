package ch.vd.ptep.mrq.model.rule;

public record RuleResult(
    String name,
    RuleMetadata metadata,
    RuleStatus status
) {

}
