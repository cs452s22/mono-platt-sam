package edu.sou.cs452.jlox;

import edu.sou.cs452.jlox.generated.types.*;
import java.util.ArrayList;
import java.util.List;

public class Interpreter implements ExprVisitor<LiteralValue>, StmtVisitor<Void> { // changed this line for lab 4

    final Environment globals = new Environment();
    private Environment environment = globals;
    public String outputString; // to be used by the elm frontend later in lab 4

    Interpreter() {
        globals.define("clock", new ClockFunction());
    }

    public void generateOutputString(LiteralValue value) {
        if (value instanceof LiteralBoolean) {
            outputString = (new Boolean(((LiteralBoolean) value).getValue())).toString();
        } else if (value instanceof LiteralFloat) {
            Float f = (new Float(((LiteralFloat) value).getValue()));
            outputString = f.toString();
            if (f % 1 == 0) { // if it is a whole number it is an integer
                outputString = (new Integer(f.intValue())).toString();
            }
        } else if (value instanceof LiteralString) {
            outputString = ((LiteralString) value).getValue();
        }
    }
    public String getOutputString() {
        return outputString;
    }

    private LiteralValue evaluate(Expr expr) {
        return accept(expr); // changed this line for lab 4
    }

    private Void execute(Stmt stmt) {
        accept(stmt); // changed this line for lab 4
        return null;
    }

    Void executeBlock(List<Stmt> statements, Environment environment) {
        Environment previous = this.environment;
        try {
            this.environment = environment;

            for (Stmt statement : statements) {
                execute(statement);
            }
        } finally {
            this.environment = previous;
        }
        return null;
    }

    @Override
    public Void visitBlockStmt(Block stmt) {
        executeBlock(stmt.getStatements(), new Environment(environment));
        return null;
    }

    @Override
    public Void visitExpressionStmt(Expression stmt) {
        evaluate(stmt.getStatement());
        return null;
    }

    @Override
    public Void visitFunctionStmt(Function stmt) {
        LoxFunction function = new LoxFunction(stmt, environment);
        environment.define(stmt.getName().getLexeme(), function);
        return null;
    }

    @Override
    public Void visitPrintStmt(Print stmt) {
        LiteralValue value = evaluate(stmt.getExpression());
        generateOutputString(value);
        return null;
    }

    @Override
    public Void visitReturnStmt(Return stmt) {
        LiteralValue value = null;
        if (stmt.getValue() != null) {
            value = evaluate(stmt.getValue());
        }

        throw new ReturnException(value);
    }

    @Override
    public Void visitVarStmt(Var stmt) {
        LiteralValue value = null;
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

        switch (expr.getOperator().getType()) {
            case GREATER:
                checkNumberOperands(expr.getOperator(), left, right);
                if (left instanceof LiteralFloat && right instanceof LiteralFloat) { // we previously assumed left and right were doubles, now we have to confirm this
                    LiteralFloat l = (LiteralFloat)left; // cast to LiteralFloat
                    LiteralFloat r = (LiteralFloat)right; // cast to LiteralFloat

                    return new LiteralBoolean(l.getValue() > r.getValue());
                }
                throw new RuntimeError(expr.getOperator(),
                "Operands must be two numbers.");
            case GREATER_EQUAL:
                checkNumberOperands(expr.getOperator(), left, right);
                if (left instanceof LiteralFloat && right instanceof LiteralFloat) {
                    LiteralFloat l = (LiteralFloat)left; // cast to LiteralFloat
                    LiteralFloat r = (LiteralFloat)right; // cast to LiteralFloat

                    return new LiteralBoolean(l.getValue() >= r.getValue());
                }
                throw new RuntimeError(expr.getOperator(),
                "Operands must be two numbers.");
            case LESS:
                checkNumberOperands(expr.getOperator(), left, right);
                if (left instanceof LiteralFloat && right instanceof LiteralFloat) {
                    LiteralFloat l = (LiteralFloat)left; // cast to LiteralFloat
                    LiteralFloat r = (LiteralFloat)right; // cast to LiteralFloat

                    return new LiteralBoolean(l.getValue() < r.getValue());
                }
                throw new RuntimeError(expr.getOperator(),
                "Operands must be two numbers.");
            case LESS_EQUAL:
                checkNumberOperands(expr.getOperator(), left, right);
                if (left instanceof LiteralFloat && right instanceof LiteralFloat) {
                    LiteralFloat l = (LiteralFloat)left; // cast to LiteralFloat
                    LiteralFloat r = (LiteralFloat)right; // cast to LiteralFloat
                    return new LiteralBoolean(l.getValue() <= r.getValue());
                }
                throw new RuntimeError(expr.getOperator(),
                "Operands must be two numbers.");
            case MINUS:
                checkNumberOperands(expr.getOperator(), left, right);
                if (left instanceof LiteralFloat && right instanceof LiteralFloat) {
                    LiteralFloat l = (LiteralFloat)left; // cast to LiteralFloat
                    LiteralFloat r = (LiteralFloat)right; // cast to LiteralFloat
                    return new LiteralFloat(l.getValue() - r.getValue());
                }
                throw new RuntimeError(expr.getOperator(),
                "Operands must be two numbers.");
            case PLUS:
                checkNumberOperands(expr.getOperator(), left, right);
                // addition
                if (left instanceof LiteralFloat && right instanceof LiteralFloat) {
                    LiteralFloat l = (LiteralFloat)left; // cast to LiteralFloat
                    LiteralFloat r = (LiteralFloat)right; // cast to LiteralFloat
                    return new LiteralFloat(l.getValue() + r.getValue());
                }
                // concatenation
                if (left instanceof LiteralString && right instanceof LiteralString) {
                    LiteralString l = (LiteralString)left; // cast to LiteralString
                    LiteralString r = (LiteralString)right; // cast to LiteralString

                    return new LiteralString(l.getValue() + r.getValue());
                }
                throw new RuntimeError(expr.getOperator(),
                "Operands must be two numbers or two strings.");
            case SLASH:
                checkNumberOperands(expr.getOperator(), left, right);
                if (left instanceof LiteralFloat && right instanceof LiteralFloat) {
                    LiteralFloat l = (LiteralFloat)left; // cast to LiteralFloat
                    LiteralFloat r = (LiteralFloat)right; // cast to LiteralFloat

                    return new LiteralFloat(l.getValue() / r.getValue());
                }
                throw new RuntimeError(expr.getOperator(),
                "Operands must be two numbers or two strings.");
            case STAR: // multiplication
                checkNumberOperands(expr.getOperator(), left, right);
                if (left instanceof LiteralFloat && right instanceof LiteralFloat) {
                    LiteralFloat l = (LiteralFloat)left; // cast to LiteralFloat
                    LiteralFloat r = (LiteralFloat)right; // cast to LiteralFloat

                    return new LiteralFloat(l.getValue() * r.getValue());
                }
                throw new RuntimeError(expr.getOperator(),
                "Operands must be two numbers.");
            case BANG_EQUAL:
                checkNumberOperands(expr.getOperator(), left, right);
                if (left instanceof LiteralBoolean && right instanceof LiteralBoolean) {
                    LiteralBoolean l = (LiteralBoolean)left;
                    LiteralBoolean r = (LiteralBoolean)right;

                    return new LiteralBoolean(!isEqual(l, r));
                }
                if (left instanceof LiteralFloat && right instanceof LiteralFloat) {
                    LiteralFloat l = (LiteralFloat)left;
                    LiteralFloat r = (LiteralFloat)right;

                    return new LiteralBoolean(!isEqual(l, r));
                }
                if (left instanceof LiteralString && right instanceof LiteralString) {
                    LiteralString l = (LiteralString)left;
                    LiteralString r = (LiteralString)right;

                    return new LiteralBoolean(!isEqual(l, r));
                }
                throw new RuntimeError(expr.getOperator(),
                "Operands must be two booleans, two numbers, or two strings.");
            
            case EQUAL_EQUAL:
                checkNumberOperands(expr.getOperator(), left, right);
                if (left instanceof LiteralBoolean && right instanceof LiteralBoolean) {
                    LiteralBoolean l = (LiteralBoolean)left;
                    LiteralBoolean r = (LiteralBoolean)right;

                    return new LiteralBoolean(isEqual(l, r));
                }
                if (left instanceof LiteralFloat && right instanceof LiteralFloat) {
                    LiteralFloat l = (LiteralFloat)left;
                    LiteralFloat r = (LiteralFloat)right;

                    return new LiteralBoolean(isEqual(l, r));
                }
                if (left instanceof LiteralString && right instanceof LiteralString) {
                    LiteralString l = (LiteralString)left;
                    LiteralString r = (LiteralString)right;

                    return new LiteralBoolean(isEqual(l, r));
                }
                throw new RuntimeError(expr.getOperator(),
                "Operands must be two booleans, two numbers, or two strings.");
            default:
                throw new RuntimeError(expr.getOperator(), 
                "The method visitBinary() not implemented for this operator type.");   
        }
    }

    @Override
    public LiteralValue visitLiteralExpr(Literal expr) {
        return expr.getValue();
    }

    @Override
    public LiteralValue visitCallExpr(Call expr) {
        LiteralValue callee = evaluate(expr.getCallee());

        List<LiteralValue> arguments = new ArrayList<>();
        for (Expr argument : expr.getArguments()) {

            arguments.add(evaluate(argument));
        }

        if (!(callee instanceof LoxCallable)) {
            throw new RuntimeError(expr.getParen(), "Can only call functions and classes.");
        }

        LoxCallable function = (LoxCallable)callee;
        return function.call(this, arguments);
    }

    @Override
    public LiteralValue visitGroupingExpr(Grouping expr) {
        return evaluate(expr.getExpression());
    }

    @Override
    public LiteralValue visitUnaryExpr(Unary expr) {
        LiteralValue right = evaluate(expr.getRight());

        switch (expr.getOperator().getType()) {
            case BANG:
                if (right instanceof LiteralBoolean) {
                    LiteralBoolean r = (LiteralBoolean)right;
                    return new LiteralBoolean(!isTruthy(r));
                }
                throw new RuntimeError(expr.getOperator(),
                "Operands must be a boolean.");
            case MINUS:
                checkNumberOperand(expr.getOperator(), right);
                if (right instanceof LiteralFloat) {
                    LiteralFloat r = (LiteralFloat)right;
                    return new LiteralFloat(-r.getValue());
                }
                throw new RuntimeError(expr.getOperator(),
                "Operand must be a number.");
            default:
                throw new RuntimeError(expr.getOperator(), 
                "The method visitUnaryExpr() not implemented for this operator type.");   
        }
    }

    @Override
    public LiteralValue visitVariableExpr(Variable expr) {
        return environment.get(expr.getName());
    }

    private Void checkNumberOperand(Token operator, LiteralValue operand) {
        if (operand instanceof LiteralFloat) { return null; }
        throw new RuntimeError(operator, "Operand must be a number.");
    }

    private Void checkNumberOperands(Token operator, LiteralValue left, LiteralValue right) {
        if (left instanceof LiteralFloat && right instanceof LiteralFloat) { return null; }
        throw new RuntimeError(operator, "Operands must be numbers.");
    }

    private boolean isTruthy(LiteralBoolean lit) {
        if (lit == null) { return false; }
        if (lit instanceof LiteralBoolean) { return lit.getValue(); }
        return true;
    }

    private boolean isEqual(LiteralValue a, LiteralValue b) {
        if (a == null && b == null) { return true; }
        if (a == null) { return false; }
    
        return a.equals(b);
    }

    private String stringify(LiteralValue lit) {
        if (lit == null) return "nil";
    
        if (lit instanceof LiteralFloat) {
            String text = lit.toString();
            if (text.endsWith(".0")) {
                text = text.substring(0, text.length() - 2);
            }
            return text;
        }
    
        return lit.toString();
    }

    Void interpret(List<Stmt> statements) {
        try {
            for (Stmt statement : statements) {
                execute(statement);
            }
        } catch (RuntimeError error) {
            Lox.runtimeError(error);
        }
        return null;
    }
}
