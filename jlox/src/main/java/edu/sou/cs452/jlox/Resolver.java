package edu.sou.cs452.jlox;

import edu.sou.cs452.jlox.generated.types.*;
import edu.sou.cs452.jlox.generated.types.Class;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import static edu.sou.cs452.jlox.generated.types.TokenType.*;

public class Resolver implements ExprVisitor<Void>, StmtVisitor<Void> {
    private final Interpreter interpreter;
    private final Stack<Map<String, Boolean>> scopes = new Stack<>();
    private FunctionType currentFunction = FunctionType.NONE;

    Resolver(Interpreter interpreter) {
        this.interpreter = interpreter;
    }

    private enum FunctionType {
        NONE,
        FUNCTION,
        INITIALIZER,
        METHOD
    }

    private enum ClassType {
        NONE,
        CLASS
    }
    
    private ClassType currentClass = ClassType.NONE;

    // walks a list of statements and calls each one
    void resolve(List<Stmt> statements) {
        for (Stmt statement : statements) {
            resolve(statement);
        }
    }

    private void resolve(Stmt stmt) {
        accept(stmt);
    }

    private void resolve(Expr expr) {
        accept(expr);
    }

    private void resolveFunction(Function function, FunctionType type) {
        FunctionType enclosingFunction = currentFunction;
        currentFunction = type;

        beginScope();
        for (Token param : function.getParams()) {
            declare(param);
            define(param);
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

    // resolve the variable itself using a helper
    private void resolveLocal(Expr expr, Token name) {
        for (int i = scopes.size() - 1; i >= 0; i--) {
            if (scopes.get(i).containsKey(name.getLexeme())) {
                interpreter.resolve(expr, scopes.size() - 1 - i);
                return;
            }
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
        ClassType enclosingClass = currentClass;
        currentClass = ClassType.CLASS;
        declare(stmt.getName());
        define(stmt.getName());
        if (stmt.getSuperclass() != null && stmt.getName().getLexeme().equals(stmt.getSuperclass().getName().getLexeme())) {
            Lox.error(stmt.getSuperclass().getName(), "A class can't inherit from itself.");
        }
        if (stmt.getSuperclass() != null) {
            resolve(stmt.getSuperclass());
        }
        beginScope();
        scopes.peek().put("this", true);
        for (Function method : stmt.getMethods()) {
            FunctionType declaration = FunctionType.METHOD;
            if (method.getName().getLexeme().equals("init")) {
                declaration = FunctionType.INITIALIZER;
            }
            resolveFunction(method, declaration); 
        }
        endScope();
        if (stmt.getSuperclass() != null) endScope();
        currentClass = enclosingClass;
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
        if (stmt.getValue() != null) {
            if (currentFunction == FunctionType.INITIALIZER) {
                if (stmt.getValue() instanceof Literal) {
                    Literal returnExpr = (Literal) stmt.getValue();
                    if (returnExpr.getValue() instanceof LiteralNull) {
                        return null;
                    }
                }
                Lox.error(stmt.getKeyword(), "Can't return a value from an initializer.");
            }
            resolve(stmt.getValue());
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
    public Void visitAssignExpr(Assign expr) {
        resolve(expr.getValue());
        resolveLocal(expr, expr.getName());
        return null;
    }

    @Override
    public Void visitBinaryExpr(Binary expr) {
        resolve(expr.getLeft());
        resolve(expr.getRight());
        return null;
    }

    @Override
    public Void visitCallExpr(Call expr) {
        resolve(expr.getCallee());
        for (Expr argument : expr.getArguments()) {
            resolve(argument);
        }
        return null;
    }

    @Override
    public Void visitGroupingExpr(Grouping expr) {
        resolve(expr.getExpression());
        return null;
    }

    @Override
    public Void visitLiteralExpr(Literal expr) {
        return null;
    }

    @Override
    public Void visitLogicalExpr(Logical expr) {
        resolve(expr.getLeft());
        resolve(expr.getRight());
        return null;
    }

    @Override
    public Void visitSetExpr(Set expr) {
        resolve(expr.getValue());
        resolve(expr.getObject());
        return null;
    }

    @Override
    public Void visitSuperExpr(Super expr) {
        resolveLocal(expr, expr.getKeyword());
        return null;
    }

    @Override
    public Void visitThisExpr(This expr) {
        if (currentClass == ClassType.NONE) {
            Lox.error(expr.getKeyword(), "Can't use 'this' outside of a class.");
            return null;
        }
        resolveLocal(expr, expr.getKeyword());
        return null;
    }

    @Override
    public Void visitUnaryExpr(Unary expr) {
        resolve(expr.getRight()); // resolve its one operand
        return null;
    }

    @Override
    public Void visitVariableExpr(Variable expr) {
        if (!scopes.isEmpty() && 
            scopes.peek().get(expr.getName().getLexeme()) == Boolean.FALSE) {
                Lox.error(expr.getName(), "Can't read local variable in its own initializer");
            }
        
        resolveLocal(expr, expr.getName());
        return null;
    }

    @Override
    public Void visitGetExpr(Get expr) {
        resolve(expr.getObject());
        return null;
    }
}
