package ch.vd.ptep.mrq.compiler.model.expression.functional;

import ch.vd.ptep.mrq.compiler.model.Expression;
import ch.vd.ptep.mrq.compiler.model.schema.Identifier;
import ch.vd.ptep.mrq.compiler.model.schema.literal.Literal;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = FunctionCall.class, name = "ArgFunction"),
    @JsonSubTypes.Type(value = Identifier.class, name = "ArgIdent"),
    @JsonSubTypes.Type(value = Literal.class, name = "ArgValue"),
})
public interface FunctionArgument extends Expression {

}
