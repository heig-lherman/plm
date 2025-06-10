package ch.vd.ptep.mrq.model.rule;

import ch.vd.ptep.mrq.model.Building;
import ch.vd.ptep.mrq.model.Dwelling;
import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.MutableClassToInstanceMap;
import java.util.Optional;
import org.jspecify.annotations.NonNull;

public class RuleContext {

    private final ClassToInstanceMap<Object> objects = MutableClassToInstanceMap.create();

    public <T> ContextCleaner putScoped(Class<T> type, T object) {
        objects.putInstance(type, object);
        return () -> objects.remove(type, object);
    }

    public <T> @NonNull T get(Class<T> type) {
        var obj = objects.getInstance(type);
        if (obj == null) {
            throw new IllegalStateException("%s not present in the rule context".formatted(type.getSimpleName()));
        }

        return obj;
    }

    public <T> Optional<T> getOptional(Class<T> type) {
        return Optional.ofNullable(objects.getInstance(type));
    }

    public @NonNull Building building() {
        return get(Building.class);
    }

    public @NonNull Dwelling dwelling() {
        return get(Dwelling.class);
    }

    public interface ContextCleaner extends AutoCloseable {

        @Override
        void close();
    }
}
