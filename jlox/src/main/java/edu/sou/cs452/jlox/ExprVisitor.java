package edu.sou.cs452.jlox;

import edu.sou.cs452.jlox.generated.types.*;

public interface ExprVisitor<T> {
    T visitAssignExpr(Assign expr);
    T visitBinaryExpr(Binary expr);
    T visitCallExpr(Call expr);
    T visitGroupingExpr(Grouping expr);
    T visitLiteralExpr(Literal expr);
    T visitLogicalExpr(Logical expr);
    T visitUnaryExpr(Unary expr);
    T visitVariableExpr(Variable expr);
    
    default T accept(Expr e) {
        if (e instanceof Assign) {
            return visitAssignExpr((Assign) e);
        } else if (e instanceof Binary) {
            return visitBinaryExpr((Binary) e);
        } else if (e instanceof Call) {
            return visitCallExpr((Call) e);
        } else if (e instanceof Grouping) {
            return visitGroupingExpr((Grouping) e);
        } else if (e instanceof Literal) {
            return visitLiteralExpr((Literal) e);
        } else if (e instanceof Logical) {
            return visitLogicalExpr((Logical) e);
        } else if (e instanceof Unary) {
            return visitUnaryExpr((Unary) e);
        } else if (e instanceof Variable) {
            return visitVariableExpr((Variable) e);
        } else {
            throw new RuntimeException("Unsupported expression type: " + e.getClass().getName());
        }
    }
}