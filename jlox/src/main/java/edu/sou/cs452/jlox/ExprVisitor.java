package edu.sou.cs452.jlox;

import edu.sou.cs452.jlox.generated.types.*;

public interface ExprVisitor<T> {
    T visitAssignExpr(Assign expr);
    T visitBinaryExpr(Binary expr);
    T visitBlockStmt(Block expr);
    T visitGrouping(Grouping expr);
    T visitExpressionStmt(Expression expr);
    T visitPrintStmt(Print expr);
    T visitLiteralExpr(Literal expr);
    T visitUnaryExpr(Unary expr);
    T visitVariableExpr(Variable expr);
    T visitVarStmt(Var expr);
    
    default T accept(Expr e) {
        if (e instanceof Assign) {
            return visitAssignExpr((Assign) e);
        } else if (e instanceof Binary) {
            return visitBinaryExpr((Binary) e);
        } else if (e instanceof Grouping) {
            return visitGrouping((Grouping) e);
        } else if (e instanceof Literal) {
            return visitLiteralExpr((Literal) e);
        } else if (e instanceof Unary) {
            return visitUnaryExpr((Unary) e);
        } else if (e instanceof Variable) {
            return visitVariableExpr((Variable) e);
        } else if (e instanceof Var) {
            return visitVarStmt((Var) e);
        } else {
            throw new RuntimeException("Unsupported expression type: " + e.getClass().getName());
        }
    }
}