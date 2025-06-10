package ch.vd.ptep.mrq.compiler.model;


import ch.vd.ptep.mrq.compiler.model.Statement.FilteredStatement;
import ch.vd.ptep.mrq.compiler.model.Statement.SimpleStatement;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

/**
 * Statements are the top-level elements of the StandardQL syntax tree. A statement can be a simple expression or a
 * filtered expression (with WHERE clause)
 */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.SIMPLE_NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = SimpleStatement.class, name = "SimpleStatement"),
    @JsonSubTypes.Type(value = FilteredStatement.class, name = "FilteredStatement")
})
public sealed abstract class Statement {

    @Getter
    @Builder
    @Jacksonized
    @EqualsAndHashCode(callSuper = false)
    public final static class SimpleStatement extends Statement {

        private final @JsonAlias("expr") Expression expression;

        public SimpleStatement(Expression expression) {
            this.expression = expression;
        }
    }

    @Getter
    @Builder
    @Jacksonized
    @EqualsAndHashCode(callSuper = false)
    public final static class FilteredStatement extends Statement {

        private final @JsonAlias("expr") Expression expression;
        private final Expression filter;

        public FilteredStatement(Expression expression, Expression filter) {
            this.expression = expression;
            this.filter = filter;
        }
    }
}
