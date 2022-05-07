package edu.sou.cs452.jlox;

import edu.sou.cs452.jlox.generated.types.*;
import edu.sou.cs452.jlox.generated.types.Class;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import static edu.sou.cs452.jlox.generated.types.TokenType.*;

public class Resolver implements ExprVisitor<LiteralValue>, StmtVisitor<Void> {
    private final Interpreter interpreter;
    private final Stack<Map<String, Boolean>> scopes = new Stack<>();

    Resolver(Interpreter interpreter) {
        this.interpreter = interpreter;
    }

    // walks a list of statements and calls each one
    void resolve(List<Stmt> statements) {
        for (Stmt statement : statements) {
            resolve(statement);
        }
    }

    // TODO: fix this
    private void resolve(Stmt stmt) {
        // stmt.accept(this);
    }

    // TODO: fix this
    private void resolve(Expr expr) {
        // expr.accept(this);
    }

    // create a new block scope
    private void beginScope() {
        scopes.push(new HashMap<String, Boolean>());
    }
    
    private void endScope() {
        scopes.pop();
    }

    private void declare(Token name) {
        if (scopes.isEmpty()) { return; }
        Map<String, Boolean> scope = scopes.peek();
        scope.put(name.getLexeme(), false);
    }

    private void define(Token name) {
        if (scopes.isEmpty()) { return; }
        scopes.peek().put(name.getLexeme(), true);
    }

    // TODO: Fix this
    // resolve the variable itself using a helper
    private void resolveLocal(Expr expr, Token name) {
        for (int i = scopes.size() - 1; i >= 0; i--) {
            // interpreter.resolve(expr, scopes.size() - 1 - i);
            return;
        }
    }

    @Override
    public Void visitBlockStmt(Block stmt) {
        beginScope();
        resolve(stmt.getStatements());
        endScope();
        return null;
    }

    @Override
    public Void visitClassStmt(Class stmt) {
        declare(stmt.getName());
        define(stmt.getName());
        return null;
    }

    @Override
    public Void visitVarStmt(Var stmt) {
        declare(stmt.getName());
        if (stmt.getInitializer() != null) {
            resolve(stmt.getInitializer());
        }
        define(stmt.getName());
        return null;
    }

    @Override
    public Void visitExpressionStmt(Expression stmt) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Void visitFunctionStmt(Function stmt) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Void visitPrintStmt(Print stmt) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Void visitReturnStmt(Return stmt) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Void visitForStmt(For stmt) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Void visitIfStmt(If stmt) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Void visitWhileStmt(While stmt) {
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
    public LiteralValue visitCallExpr(Call expr) {
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
