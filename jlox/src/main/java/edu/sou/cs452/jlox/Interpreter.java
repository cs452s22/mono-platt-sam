package edu.sou.cs452.jlox;

import edu.sou.cs452.jlox.generated.types.*;
import java.util.List;

// TODO: remove the following import if commenting it out doesn't cause errors
// import static edu.sou.cs452.jlox.generated.types.TokenType.*;


public class Interpreter implements ExprVisitor<LiteralValue>, StmtVisitor<Void> { // changed this line for lab 4

    private Environment environment = new Environment();

    private LiteralValue evaluate(Expr expr) {
        return accept(expr); // changed this line for lab 4
    }

    private Void execute(Stmt stmt) {
        accept(stmt); // changed this line for lab 4
        return null;
    }

    void executeBlock(List<Stmt> statements, Environment environment) {
        Environment previous = this.environment;
        try {
            this.environment = environment;

            for (Stmt statement : statements) {
                execute(statement);
            }
        } finally {
            this.environment = previous;
        }
    }

    @Override
    public Void visitBlockStmt(Block stmt) {
        executeBlock(stmt.getStatements(), new Environment(environment));
        return null;
    }

    @Override
    public Void visitExpressionStmt(Expression stmt) {
        evaluate(stmt.getExpression());
        return null;
    }

    @Override
    public Void visitPrintStmt(Print stmt) {
        Object value = evaluate(stmt.getExpression());
        System.out.println(stringify(value));
        return null;
    }

    @Override
    public Void visitVarStmt(Var stmt) {
        Object value = null;
        if (stmt.getInitializer() != null) {
            value = evaluate(stmt.getInitializer());
        }

        environment.define(stmt.getName().getLexeme(), value);
        return null;
    }

    @Override
    public LiteralValue visitAssignExpr(Assign expr) {
        LiteralValue value = evaluate(expr.getValue());
        environment.assign(expr.getName(), value);
        return value;
    }

    @Override
    public LiteralValue visitBinaryExpr(Binary expr) {
        LiteralValue left = evaluate(expr.getLeft());
        LiteralValue right = evaluate(expr.getRight()); 

        // TODO: figure out if I need to add code for other cases
        switch (expr.getOperator().getType()) {
            case GREATER:
                checkNumberOperands(expr.getOperator(), left, right);
                if (left instanceof LiteralFloat && right instanceof LiteralFloat) { // we previously assumed left and right were doubles, now we have to confirm this
                    LiteralFloat l = (LiteralFloat)left; // cast to LiteralFloat
                    LiteralFloat r = (LiteralFloat)right; // cast to LiteralFloat
                    return new LiteralBoolean(l.getValue() > r.getValue());
                } else {
                    throw new RuntimeError(expr.getOperator(),
                    "Operands must be two numbers.");
                }
            case GREATER_EQUAL:
                checkNumberOperands(expr.getOperator(), left, right);
                if (left instanceof LiteralFloat && right instanceof LiteralFloat) {
                    LiteralFloat l = (LiteralFloat)left; // cast to LiteralFloat
                    LiteralFloat r = (LiteralFloat)right; // cast to LiteralFloat
                    return new LiteralBoolean(l.getValue() >= r.getValue());
                } else {
                    throw new RuntimeError(expr.getOperator(),
                    "Operands must be two numbers.");
                }
            case LESS:
                checkNumberOperands(expr.getOperator(), left, right);
                if (left instanceof LiteralFloat && right instanceof LiteralFloat) {
                    LiteralFloat l = (LiteralFloat)left; // cast to LiteralFloat
                    LiteralFloat r = (LiteralFloat)right; // cast to LiteralFloat
                    return new LiteralBoolean(l.getValue() < r.getValue());
                } else {
                    throw new RuntimeError(expr.getOperator(),
                    "Operands must be two numbers.");
                }
            case LESS_EQUAL:
                checkNumberOperands(expr.getOperator(), left, right);
                if (left instanceof LiteralFloat && right instanceof LiteralFloat) {
                    LiteralFloat l = (LiteralFloat)left; // cast to LiteralFloat
                    LiteralFloat r = (LiteralFloat)right; // cast to LiteralFloat
                    return new LiteralBoolean(l.getValue() <= r.getValue());
                } else {
                    throw new RuntimeError(expr.getOperator(),
                    "Operands must be two numbers.");
                }
            case MINUS:
                checkNumberOperands(expr.getOperator(), left, right);
                if (left instanceof LiteralFloat && right instanceof LiteralFloat) {
                    LiteralFloat l = (LiteralFloat)left; // cast to LiteralFloat
                    LiteralFloat r = (LiteralFloat)right; // cast to LiteralFloat
                    return new LiteralFloat(l.getValue() - r.getValue());
                } else {
                    throw new RuntimeError(expr.getOperator(),
                    "Operands must be two numbers.");
                }
            case PLUS:
                if (left instanceof LiteralFloat && right instanceof LiteralFloat) {
                    LiteralFloat l = (LiteralFloat)left; // cast to LiteralFloat
                    LiteralFloat r = (LiteralFloat)right; // cast to LiteralFloat
                    return new LiteralFloat(l.getValue() + r.getValue());
                } else if (left instanceof LiteralString && right instanceof LiteralString) {
                    LiteralString l = (LiteralString)left; // cast to LiteralString
                    LiteralString r = (LiteralString)right; // cast to LiteralString
                    return new LiteralString(l.getValue() + r.getValue());
                } else {
                    throw new RuntimeError(expr.getOperator(),
                    "Operands must be two numbers or two strings.");
                }
            case SLASH:
                checkNumberOperands(expr.getOperator(), left, right);
                return (double)left / (double)right;
            case STAR:
                checkNumberOperands(expr.getOperator(), left, right);
                return (double)left * (double)right;
            case BANG_EQUAL:
                return !isEqual(left, right);
            case EQUAL_EQUAL:
                return isEqual(left, right);
        }

        // Unreachable.
        return null;
    }

    @Override
    public LiteralValue visitLiteralExpr(Literal expr) {
        return expr.getValue();
    }

    @Override
    public LiteralValue visitUnaryExpr(Unary expr) {
        LiteralValue right = evaluate(expr.getRight());

        // TODO: figure out if I need to add code for other cases
        switch (expr.getOperator().getType()) {
            case BANG:
                return !isTruthy(right);
            case MINUS:
                checkNumberOperand(expr.getOperator(), right);
                return -(double)right;
        }

        // Unreachable.
        return null;
    }

    @Override
    public LiteralValue visitVariableExpr(Variable expr) {
        return environment.get(expr.getName());
    }

    private void checkNumberOperand(Token operator, Object operand) {
        if (operand instanceof Double) { return; }
        throw new RuntimeError(operator, "Operand must be a number.");
    }

    private void checkNumberOperands(Token operator, Object left, Object right) {
        if (left instanceof Double && right instanceof Double) { return; }
        throw new RuntimeError(operator, "Operands must be numbers.");
    }

    @Override
    public LiteralValue visitGroupingExpr(Grouping expr) {
        return evaluate(expr.getExpression());
    }

    private boolean isTruthy(Object object) {
        if (object == null) { return false; }
        if (object instanceof Boolean) { return (boolean)object; }
        return true;
    }

    private boolean isEqual(Object a, Object b) {
        if (a == null && b == null) { return true; }
        if (a == null) { return false; }
    
        return a.equals(b);
    }

    private String stringify(Object object) {
        if (object == null) return "nil";
    
        if (object instanceof Double) {
            String text = object.toString();
            if (text.endsWith(".0")) {
                text = text.substring(0, text.length() - 2);
            }
            return text;
        }
    
        return object.toString();
    }

    void interpret(List<Stmt> statements) {
        try {
            for (Stmt statement : statements) {
                execute(statement);
            }
        } catch (RuntimeError error) {
            Lox.runtimeError(error);
        }
    }
}
