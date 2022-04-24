package edu.sou.cs452.jlox;

import edu.sou.cs452.jlox.generated.types.*;

public interface ExprVisitor<T> {
    T visitAssign(Assign expr);
    T visitBinary(Binary expr);
    T visitGrouping(Grouping expr);
    T visitLiteral(Literal expr);
    T visitUnary(Unary expr);
    // T visitVisitor(Visitor expr);
    
    default T accept(Expr e) {
        if (e instanceof Assign) {
            return visitAssign((Assign) e);

        } else if (e instanceof Binary) {
            return visitBinary((Binary) e);

        } else if (e instanceof Grouping) {
            return visitGrouping((Grouping) e);

        } else if (e instanceof Literal) {
            return visitLiteral((Literal) e);

        } else if (e instanceof Unary) {
            return visitUnary((Unary) e);
        /*
        } else if (e instanceof Visitor) {
            return visitVisitor((Visitor) e);
        }
        */
        } else {
            throw new RuntimeException("Unsupported expression type: " + e.getClass().getName());
        }
    }
}