package ch.vd.ptep.mrq.engine.core.relation;

import ch.vd.ptep.mrq.model.rule.RuleContext;
import ch.vd.ptep.mrq.model.rule.RuleObject;
import java.util.List;

public interface RelationObjectResolver<T> {

    RuleObject sourceObject();

    RuleObject targetObject();

    List<T> resolve(RuleContext context);
}
