package edu.sou.cs452.jlox;

import edu.sou.cs452.jlox.generated.types.*;

public interface ExprVisitor<T> {
    T visitBinary(Binary expr);
    T visitGrouping(Grouping expr);
    T visitUnary(Unary expr);
    T visitLiteral(Literal expr);

    default T accept(Expr e) {
        if (e instanceof Binary) {
            return visitBinary((Binary) e);
        } else if (e instanceof Grouping) {
            return visitGrouping((Grouping) e);
        } else if (e instanceof Unary) {
            return visitUnary((Unary) e);
        } else if (e instanceof Literal) {
            return visitLiteral((Literal) e);
        } else {
            throw new RuntimeException("Unsupported expression type: " + e.getClass().getName());
        }
    }
}