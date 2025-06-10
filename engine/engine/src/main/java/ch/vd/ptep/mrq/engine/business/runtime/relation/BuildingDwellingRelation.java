package ch.vd.ptep.mrq.engine.business.runtime.relation;

import ch.vd.ptep.mrq.engine.core.relation.RelationObjectResolver;
import ch.vd.ptep.mrq.model.Dwelling;
import ch.vd.ptep.mrq.model.Entrance;
import ch.vd.ptep.mrq.model.rule.RuleContext;
import ch.vd.ptep.mrq.model.rule.RuleObject;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public final class BuildingDwellingRelation implements RelationObjectResolver<Dwelling> {

    @Override
    public RuleObject sourceObject() {
        return RuleObject.BUILDING;
    }

    @Override
    public RuleObject targetObject() {
        return RuleObject.DWELLING;
    }

    @Override
    public List<Dwelling> resolve(RuleContext context) {
        return context
            .building()
            .entrances()
            .stream()
            .map(Entrance::dwellings)
            .flatMap(List::stream)
            .toList();
    }
}
