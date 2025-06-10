package ch.vd.ptep.mrq.compiler.model.schema.literal;

public interface NumericLiteral<T extends Number, S extends NumericLiteral<T, ?>> extends Literal, Comparable<S> {

    boolean hasFloatingPoint();

    T value();
}
