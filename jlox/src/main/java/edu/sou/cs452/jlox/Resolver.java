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
    private FunctionType currentFunction = FunctionType.NONE;

    private enum FunctionType {
        NONE,
        FUNCTION
    }

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
        stmt.accept(this);
    }

    // TODO: fix this
    private void resolve(Expr expr) {
        expr.accept(this);
    }

    private void resolveFunction(Function function, FunctionType type) {
        FunctionType enclosingFunction = currentFunction;
        currentFunction = type;

        beginScope();
        for (Token parem : function.getParams()) {
            declare(parem);
            define(parem);
        }
        resolve(function.getBody());
        endScope();
        currentFunction = enclosingFunction;
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
        if (scope.containsKey(name.getLexeme())) {
            Lox.error(name, "There is already a variable with this name in this scope.");
        }
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
        resolve(stmt.getExpression());
        return null;
    }

    @Override
    public Void visitFunctionStmt(Function stmt) {
        declare(stmt.getName());
        define(stmt.getName());

        resolveFunction(stmt, FunctionType.FUNCTION);
        return null;
    }

    @Override
    public Void visitPrintStmt(Print stmt) {
        resolve(stmt.getExpression());
        return null;
    }

    @Override
    public Void visitReturnStmt(Return stmt) {
        if (currentFunction == FunctionType.NONE) {
            throw new RuntimeError(stmt.getKeyword(), "Can't return from top-level code");
        }
        return null;
    }

    @Override
    public Void visitForStmt(For stmt) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Void visitIfStmt(If stmt) {
        resolve(stmt.getCondition());
        resolve(stmt.getThenBranch());

        if (stmt.getElseBranch() != null) {
            resolve(stmt.getElseBranch());
        }
        return null;
    }

    @Override
    public Void visitWhileStmt(While stmt) {
        resolve(stmt.getCondition());
        resolve(stmt.getBody());
        return null;
    }

    @Override
    public LiteralValue visitAssignExpr(Assign expr) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public LiteralValue visitBinaryExpr(Binary expr) {
        resolve(expr.getLeft());
        resolve(expr.getRight());
        return null;
    }

    @Override
    public LiteralValue visitCallExpr(Call expr) {
        resolve(expr.getCallee());
        for (Expr argument : expr.getArguments()) {
            resolve(argument);
        }
        return null;
    }

    @Override
    public LiteralValue visitGroupingExpr(Grouping expr) {
        resolve(expr.getExpression());
        return null;
    }

    @Override
    public LiteralValue visitLiteralExpr(Literal expr) {
        return null;
    }

    @Override
    public Void visitLogicalExpr(Logical expr) {
        resolve(expr.getLeft());
        resolve(expr.getRight());
        return null;
    }

    @Override
    public LiteralValue visitUnaryExpr(Unary expr) {
        resolve(expr.getRight());
        return null;
    }

    @Override
    public LiteralValue visitVariableExpr(Variable expr) {
        if (!scopes.isEmpty() && 
            scopes.peek().get(expr.getName().getLexeme()) == Boolean.FALSE) {
                Lox.error(expr.getName(), "Can't read local variable in its own initializer");
            }
        
        resolveLocal(expr, expr.getName());
        return null;
    }
}
