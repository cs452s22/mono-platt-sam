package edu.sou.cs452.jlox;

import edu.sou.cs452.jlox.generated.types.*;

import static edu.sou.cs452.jlox.generated.types.TokenType.*;

public class AbstractInterpreter implements ExprVisitor<LiteralValue>, StmtVisitor<Void> {

    @Override
    public Void visitBlockStmt(Block stmt) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Void visitExpressionStmt(Expression stmt) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Void visitPrintStmt(Print stmt) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Void visitVarStmt(Var stmt) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public LiteralValue visitAssignExpr(Assign expr) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public LiteralValue visitBinaryExpr(Binary expr) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public LiteralValue visitGroupingExpr(Grouping expr) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public LiteralValue visitLiteralExpr(Literal expr) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public LiteralValue visitUnaryExpr(Unary expr) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public LiteralValue visitVariableExpr(Variable expr) {
        // TODO Auto-generated method stub
        return null;
    }
    
}
