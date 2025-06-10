package ch.vd.ptep.mrq.engine.business.runtime.relation;

import ch.vd.ptep.mrq.engine.core.relation.RelationObjectResolver;
import ch.vd.ptep.mrq.model.RealEstate;
import ch.vd.ptep.mrq.model.rule.RuleContext;
import ch.vd.ptep.mrq.model.rule.RuleObject;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public final class BuildingRealEstateRelation implements RelationObjectResolver<RealEstate> {

    @Override
    public RuleObject sourceObject() {
        return RuleObject.BUILDING;
    }

    @Override
    public RuleObject targetObject() {
        return RuleObject.REAL_ESTATE;
    }

    @Override
    public List<RealEstate> resolve(RuleContext context) {
        return context.building().realEstates();
    }
}
