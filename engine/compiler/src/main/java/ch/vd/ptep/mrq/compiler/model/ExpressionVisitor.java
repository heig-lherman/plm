package ch.vd.ptep.mrq.compiler.model;

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
import ch.vd.ptep.mrq.compiler.model.schema.literal.NullLiteral;
import ch.vd.ptep.mrq.compiler.model.schema.literal.StringLiteral;

public interface ExpressionVisitor<T> {

    // --- Logic operators

    T visit(AndExpression andExpression);

    T visit(OrExpression orExpression);

    T visit(NotExpression notExpression);

    // --- Relational operators

    T visit(EqualsTo equalsTo);

    T visit(NotEqualsTo equalsTo);

    T visit(LessThan lessThan);

    T visit(LessThanOrEquals lessThanOrEquals);

    T visit(GreaterThan greaterThan);

    T visit(GreaterThanOrEquals greaterThanOrEquals);

    // --- Arithmetic operators

    T visit(AddOperation addition);

    T visit(SubtractOperation subtraction);

    T visit(MultiplyOperation multiplication);

    T visit(DivideOperation division);

    // --- Expressions

    T visit(FunctionCall functionCall);

    // --- Schema

    T visit(Identifier identifier);

    T visit(StringLiteral stringLiteral);

    T visit(IntegerLiteral integerLiteral);

    T visit(DoubleLiteral doubleLiteral);

    T visit(NullLiteral nullLiteral);
}
