package ch.vd.ptep.mrq.engine.core.runtime;

import ch.vd.ptep.mrq.compiler.model.Statement;
import ch.vd.ptep.mrq.compiler.model.expression.arithmetic.AddOperation;
import ch.vd.ptep.mrq.compiler.model.expression.arithmetic.DivideOperation;
import ch.vd.ptep.mrq.compiler.model.expression.arithmetic.MultiplyOperation;
import ch.vd.ptep.mrq.compiler.model.expression.arithmetic.SubtractOperation;
import ch.vd.ptep.mrq.compiler.model.expression.functional.FunctionCall;
import ch.vd.ptep.mrq.compiler.model.expression.operator.AndExpression;
import ch.vd.ptep.mrq.compiler.model.expression.operator.NotExpression;
import ch.vd.ptep.mrq.compiler.model.expression.operator.OrExpression;
import ch.vd.ptep.mrq.compiler.model.expression.relational.EqualsTo;
import ch.vd.ptep.mrq.compiler.model.expression.relational.GreaterThan;
import ch.vd.ptep.mrq.compiler.model.expression.relational.GreaterThanOrEquals;
import ch.vd.ptep.mrq.compiler.model.expression.relational.LessThan;
import ch.vd.ptep.mrq.compiler.model.expression.relational.LessThanOrEquals;
import ch.vd.ptep.mrq.compiler.model.expression.relational.NotEqualsTo;
import ch.vd.ptep.mrq.compiler.model.schema.Identifier;
import ch.vd.ptep.mrq.compiler.model.schema.literal.DoubleLiteral;
import ch.vd.ptep.mrq.compiler.model.schema.literal.IntegerLiteral;
import ch.vd.ptep.mrq.compiler.model.schema.literal.StringLiteral;
import ch.vd.ptep.mrq.compiler.model.support.ExpressionVisitorAdapter;
import ch.vd.ptep.mrq.engine.core.function.RuntimeFunction;
import ch.vd.ptep.mrq.engine.core.identifier.IdentifierResolutionService;
import ch.vd.ptep.mrq.engine.core.relation.RelationObjectResolver;
import ch.vd.ptep.mrq.model.rule.RuleContext;
import ch.vd.ptep.mrq.model.rule.RuleMetadata;
import ch.vd.ptep.mrq.model.rule.RuleResult;
import ch.vd.ptep.mrq.model.rule.RuleStatus;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;

@Slf4j
@RequiredArgsConstructor
public class RuntimeEvaluationService {

    private final ApplicationContext applicationContext;
    private final IdentifierResolutionService identifierResolutionService;

    public RuleResult validate(
        Statement statement,
        String ruleName,
        RuleMetadata metadata,
        RuleContext context
    ) {
        try {
            var evaluator = new ExpressionEvaluator(context, metadata);
            var result = evaluator.evaluateStatement(statement);

            return new RuleResult(
                ruleName,
                metadata,
                result ? RuleStatus.VALID : RuleStatus.INVALID
            );
        } catch (Exception e) {
            log.error("Error evaluating rule {}: {}", metadata.label(), e.getMessage(), e);
            return new RuleResult(
                ruleName,
                metadata,
                RuleStatus.TECHNICAL_ERROR
            );
        }
    }

    @SuppressWarnings("rawtypes")
    private class ExpressionEvaluator extends ExpressionVisitorAdapter<Object> {

        private final RuleContext context;
        private final RuleMetadata metadata;
        private final Map<String, RelationObjectResolver> relationResolvers;

        public ExpressionEvaluator(RuleContext context, RuleMetadata metadata) {
            this.context = context;
            this.metadata = metadata;
            this.relationResolvers = loadRelationResolvers();
        }

        private Map<String, RelationObjectResolver> loadRelationResolvers() {
            return applicationContext.getBeansOfType(RelationObjectResolver.class);
        }

        public boolean evaluateStatement(Statement statement) {
            return switch (statement) {
                case Statement.SimpleStatement simple -> toBoolean(simple.getExpression().accept(this));
                case Statement.FilteredStatement filtered -> {
                    // First evaluate the filter condition
                    var filterResult = toBoolean(filtered.getFilter().accept(this));
                    if (!filterResult) {
                        // If filter doesn't match, the rule doesn't apply
                        yield true;
                    }

                    // If filter matches, evaluate the main expression
                    yield toBoolean(filtered.getExpression().accept(this));
                }
            };
        }

        @Override
        public Object visit(AndExpression andExpression) {
            var left = toBoolean(andExpression.getLeftExpression().accept(this));
            // Short-circuit evaluation
            if (!left) {
                return false;
            }
            return toBoolean(andExpression.getRightExpression().accept(this));
        }

        @Override
        public Object visit(OrExpression orExpression) {
            var left = toBoolean(orExpression.getLeftExpression().accept(this));
            // Short-circuit evaluation
            if (left) {
                return true;
            }
            return toBoolean(orExpression.getRightExpression().accept(this));
        }

        @Override
        public Object visit(NotExpression notExpression) {
            return !toBoolean(notExpression.getInnerExpression().accept(this));
        }

        @Override
        public Object visit(EqualsTo equalsTo) {
            var left = equalsTo.getLeftExpression().accept(this);
            var right = equalsTo.getRightExpression().accept(this);
            return compareValues(left, right) == 0;
        }

        @Override
        public Object visit(NotEqualsTo notEqualsTo) {
            var left = notEqualsTo.getLeftExpression().accept(this);
            var right = notEqualsTo.getRightExpression().accept(this);
            return compareValues(left, right) != 0;
        }

        @Override
        public Object visit(LessThan lessThan) {
            var left = lessThan.getLeftExpression().accept(this);
            var right = lessThan.getRightExpression().accept(this);
            return compareValues(left, right) < 0;
        }

        @Override
        public Object visit(LessThanOrEquals lessThanOrEquals) {
            var left = lessThanOrEquals.getLeftExpression().accept(this);
            var right = lessThanOrEquals.getRightExpression().accept(this);
            return compareValues(left, right) <= 0;
        }

        @Override
        public Object visit(GreaterThan greaterThan) {
            var left = greaterThan.getLeftExpression().accept(this);
            var right = greaterThan.getRightExpression().accept(this);
            return compareValues(left, right) > 0;
        }

        @Override
        public Object visit(GreaterThanOrEquals greaterThanOrEquals) {
            var left = greaterThanOrEquals.getLeftExpression().accept(this);
            var right = greaterThanOrEquals.getRightExpression().accept(this);
            return compareValues(left, right) >= 0;
        }

        @Override
        public Object visit(AddOperation addition) {
            var left = addition.getLeftExpression().accept(this);
            var right = addition.getRightExpression().accept(this);
            return performArithmetic(left, right, Double::sum);
        }

        @Override
        public Object visit(SubtractOperation subtraction) {
            var left = subtraction.getLeftExpression().accept(this);
            var right = subtraction.getRightExpression().accept(this);
            return performArithmetic(left, right, (a, b) -> a - b);
        }

        @Override
        public Object visit(MultiplyOperation multiplication) {
            var left = multiplication.getLeftExpression().accept(this);
            var right = multiplication.getRightExpression().accept(this);
            return performArithmetic(left, right, (a, b) -> a * b);
        }

        @Override
        public Object visit(DivideOperation division) {
            var left = division.getLeftExpression().accept(this);
            var right = division.getRightExpression().accept(this);
            return performArithmetic(left, right, (a, b) -> a / b);
        }

        @Override
        public Object visit(FunctionCall functionCall) {
            RuntimeFunction<?> function = applicationContext.getBean(functionCall.name(), RuntimeFunction.class);

            var evaluatedArgs = Arrays.stream(functionCall.args())
                .map(arg -> arg.accept(this))
                .toArray();

            if (!function.validate(evaluatedArgs)) {
                throw new RuntimeException("Invalid arguments for function: " + functionCall.name());
            }

            return function.execute(evaluatedArgs);
        }

        @Override
        public Object visit(Identifier identifier) {
            var name = identifier.name();

            // First, try to resolve from the current context object
            var currentObject = metadata.object();
            if (identifierResolutionService.isPartOfObject(currentObject, name)) {
                return identifierResolutionService.resolveIdentifier(context, currentObject, name);
            }

            var targetObject = identifierResolutionService.findObject(name);

            // If not found, check if it's a relation to another object type
            for (var entry : relationResolvers.entrySet()) {
                var resolver = entry.getValue();
                if (resolver.sourceObject() == currentObject && resolver.targetObject() == targetObject) {
                    List<?> relatedObjects = resolver.resolve(context);
                    return relatedObjects.stream()
                        .map(obj -> identifierResolutionService.resolveIdentifier(obj, name))
                        .toList();
                }
            }

            throw new IllegalStateException("Unable to resolve identifier: " + name);
        }

        @Override
        public Object visit(StringLiteral stringLiteral) {
            return stringLiteral.value();
        }

        @Override
        public Object visit(IntegerLiteral integerLiteral) {
            return integerLiteral.value();
        }

        @Override
        public Object visit(DoubleLiteral doubleLiteral) {
            return doubleLiteral.value();
        }

        private boolean toBoolean(Object value) {
            return switch (value) {
                case null -> false;
                case Boolean b -> b;
                case Number number -> number.intValue() != 0;
                case String s -> !s.isEmpty();
                default -> true;
            };
        }

        @SuppressWarnings("unchecked")
        private int compareValues(Object left, Object right) {
            if (left == null && right == null) {
                return 0;
            }
            if (left == null) {
                return -1;
            }
            if (right == null) {
                return 1;
            }

            if (left instanceof Comparable && right instanceof Comparable) {
                if (left.getClass().isAssignableFrom(right.getClass()) ||
                    right.getClass().isAssignableFrom(left.getClass())) {
                    return ((Comparable) left).compareTo(right);
                }
            }

            // Try numeric comparison
            if (left instanceof Number && right instanceof Number) {
                var leftVal = ((Number) left).doubleValue();
                var rightVal = ((Number) right).doubleValue();
                return Double.compare(leftVal, rightVal);
            }

            // String comparison
            return left.toString().compareTo(right.toString());
        }

        private Number performArithmetic(Object left, Object right, ArithmeticOperation op) {
            if (!(left instanceof Number leftNum) || !(right instanceof Number rightNum)) {
                throw new RuntimeException("Arithmetic operations require numeric operands");
            }

            // Use double arithmetic for floating point operations
            if (left instanceof Double || right instanceof Double ||
                left instanceof Float || right instanceof Float) {
                return op.apply(leftNum.doubleValue(), rightNum.doubleValue());
            }

            // Use long arithmetic for integers
            return op.apply(leftNum.longValue(), rightNum.longValue()).longValue();
        }

        @FunctionalInterface
        private interface ArithmeticOperation {

            Number apply(double a, double b);
        }
    }
}
