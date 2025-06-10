package ch.vd.ptep.mrq.engine.business.runtime.relation;

import ch.vd.ptep.mrq.engine.core.relation.RelationObjectResolver;
import ch.vd.ptep.mrq.model.Entrance;
import ch.vd.ptep.mrq.model.rule.RuleContext;
import ch.vd.ptep.mrq.model.rule.RuleObject;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public final class BuildingEntranceRelation implements RelationObjectResolver<Entrance> {

    @Override
    public RuleObject sourceObject() {
        return RuleObject.BUILDING;
    }

    @Override
    public RuleObject targetObject() {
        return RuleObject.ENTRANCE;
    }

    @Override
    public List<Entrance> resolve(RuleContext context) {
        return context.building().entrances();
    }
}
