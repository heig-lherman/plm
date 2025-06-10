package ch.vd.ptep.mrq.model.rule;

import ch.vd.ptep.mrq.model.Building;
import ch.vd.ptep.mrq.model.Dwelling;
import ch.vd.ptep.mrq.model.Entrance;
import ch.vd.ptep.mrq.model.Permit;
import ch.vd.ptep.mrq.model.Project;
import ch.vd.ptep.mrq.model.RealEstate;
import ch.vd.ptep.mrq.model.Work;
import com.google.common.collect.ImmutableMultimap;
import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RuleObject {
    PROJECT(Project.class),
    PERMIT(Permit.class),
    WORK(Work.class),
    BUILDING(Building.class),
    REAL_ESTATE(RealEstate.class),
    ENTRANCE(Entrance.class),
    DWELLING(Dwelling.class);

    private final Class<?> objectClass;

    public boolean isInContextFor(RuleObject other) {
        return this == other || CONTEXT_OBJECTS.containsEntry(other, this);
    }

    private static final ImmutableMultimap<RuleObject, RuleObject> CONTEXT_OBJECTS = ImmutableMultimap
        .<RuleObject, RuleObject>builder()
        .put(PROJECT, PERMIT)
        .put(PERMIT, PROJECT)
        .putAll(WORK, PROJECT, PERMIT, BUILDING)
        .putAll(BUILDING, WORK, PROJECT, PERMIT)
        .putAll(REAL_ESTATE, PROJECT, PERMIT)
        .putAll(ENTRANCE, BUILDING, PROJECT, PERMIT, WORK)
        .putAll(DWELLING, ENTRANCE, BUILDING, PROJECT, PERMIT, WORK)
        .build();

    public static RuleObject from(Class<?> objectClass) {
        return Arrays.stream(values())
            .filter(ruleObject -> ruleObject.objectClass.equals(objectClass))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Unknown rule object %s".formatted(objectClass)));
    }
}
