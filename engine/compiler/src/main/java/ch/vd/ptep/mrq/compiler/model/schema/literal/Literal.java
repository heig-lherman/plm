package ch.vd.ptep.mrq.compiler.model.schema.literal;

import ch.vd.ptep.mrq.compiler.model.Expression;
import ch.vd.ptep.mrq.compiler.model.expression.functional.FunctionArgument;
import ch.vd.ptep.mrq.compiler.model.schema.literal.Literal.Deserializer;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;
import java.math.BigDecimal;

@JsonDeserialize(using = Deserializer.class)
public interface Literal extends Expression, FunctionArgument {

    class Deserializer extends StdDeserializer<Literal> {

        public Deserializer() {
            super(Literal.class);
        }

        @Override
        public Literal deserialize(
            JsonParser p,
            DeserializationContext ctxt
        ) throws IOException {
            JsonNode node = p.getCodec().readTree(p);
            JsonNode valueNode = node.get("value");
            if (valueNode == null) {
                throw new IOException("Missing 'value' field");
            }

            JsonNode tagNode = valueNode.get("tag");
            if (tagNode == null) {
                throw new IOException("Missing 'tag' field in value object");
            }

            String tag = tagNode.asText();
            switch (tag) {
                case "StrValue":
                    JsonNode strContents = valueNode.get("contents");
                    if (strContents == null) {
                        throw new IOException("Missing 'contents' field for StrValue");
                    }

                    return new StringLiteral(strContents.asText());

                case "NumValue":
                    JsonNode numContents = valueNode.get("contents");
                    if (numContents == null) {
                        throw new IOException("Missing 'contents' field for NumValue");
                    }

                    if (numContents.isIntegralNumber()) {
                        return new IntegerLiteral(numContents.asLong());
                    } else if (numContents.isFloatingPointNumber()) {
                        return new DoubleLiteral(BigDecimal.valueOf(numContents.asDouble()));
                    } else {
                        throw new IOException("Invalid numeric contents for NumValue");
                    }

                case "NullValue":
                    return new NullLiteral();

                default:
                    throw new IOException("Unknown tag: " + tag);
            }
        }
    }
}
