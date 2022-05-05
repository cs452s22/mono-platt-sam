package edu.sou.cs452.jlox;

import edu.sou.cs452.jlox.AbstractInterpreter.*; // Why did I have to do this???
import edu.sou.cs452.jlox.generated.types.*;
import java.util.HashMap;
import java.util.List;

import static edu.sou.cs452.jlox.generated.types.TokenType.*;

public class AbstractInterpreter implements ExprVisitor<AbstractValue>, StmtVisitor<Void> {

    public enum AbstractValue {
        BOTTOM,
        NEGATIVE,
        POSITIVE,
        TOP,
        ZERO
    }

    final Environment globals = new Environment();
    private Environment environment = globals;
    public String outputString; // to be used by the elm frontend later in lab 4

    AbstractInterpreter() {
        globals.define("clock", new ClockFunction());
    }

    // TODO: update this function
    public void generateOutputString(AbstractValue value) {
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

    private AbstractValue evaluate(Expr expr) {
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
        AbstractValue value = evaluate(stmt.getExpression());
        generateOutputString(value);
        return null;
    }

    @Override
    public Void visitReturnStmt(Return stmt) {
        AbstractValue value = null;
        if (stmt.getValue() != null) {
            value = evaluate(stmt.getValue());
        }

        throw new ReturnException(value);
    }

    @Override
    public Void visitVarStmt(Var stmt) {
        AbstractValue value = null;
        if (stmt.getInitializer() != null) {
            value = evaluate(stmt.getInitializer());
        }

        environment.define(stmt.getName().getLexeme(), value);
        return null;
    }

    @Override
    public AbstractValue visitAssignExpr(Assign expr) {
        AbstractValue value = evaluate(expr.getValue());
        environment.assign(expr.getName(), value);
        return value;
    }

    @Override
    public AbstractValue visitBinaryExpr(Binary expr) {
        AbstractValue left = evaluate(expr.getLeft());
        AbstractValue right = evaluate(expr.getRight()); 

        switch (expr.getOperator().getType()) {
            case MINUS:
                return minus(left, right);
                throw new RuntimeError(expr.getOperator(),
                "Operands must be two numbers.");
            case PLUS:
                return plus(left, right);
                throw new RuntimeError(expr.getOperator(),
                "Operands must be two numbers or two strings.");
            case SLASH:
                return minus(left, right);
                throw new RuntimeError(expr.getOperator(),
                "Operands must be two numbers or two strings.");
            case STAR: // multiplication
                return star(left, right);
                throw new RuntimeError(expr.getOperator(),
                "Operands must be two numbers.");
            default:
                throw new RuntimeError(expr.getOperator(), 
                "The method visitBinary() not implemented for this operator type.");   
        }
    }

    @Override
    public AbstractValue visitLiteralExpr(Literal expr) {
        return expr.getValue();
    }

    @Override
    public AbstractValue visitCallExpr(Call expr) {
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
    public AbstractValue visitGroupingExpr(Grouping expr) {
        return evaluate(expr.getExpression());
    }

    @Override
    public AbstractValue visitUnaryExpr(Unary expr) {
        AbstractValue right = evaluate(expr.getRight());

        switch (expr.getOperator().getType()) {
            case BANG:
                return bang(right);
                throw new RuntimeError(expr.getOperator(), "Operands must be a boolean.");
            case MINUS:
                return minus(right);
                throw new RuntimeError(expr.getOperator(), "Operand must be a number.");
            default:
                throw new RuntimeError(expr.getOperator(), 
                "The method visitUnaryExpr() not implemented for this operator type.");   
        }
    }

    @Override
    public AbstractValue visitVariableExpr(Variable expr) {
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

    public final static AbstractValue minus(AbstractValue leftValue, AbstractValue rightValue) {
        HashMap<AbstractValue, HashMap<AbstractValue, AbstractValue>> lookup = new HashMap<>();
    
        HashMap<AbstractValue, AbstractValue> left;

        // left +
        left = new HashMap<>();
        left.put(AbstractValue.POSITIVE, AbstractValue.TOP); // positive - positive
        left.put(AbstractValue.NEGATIVE, AbstractValue.POSITIVE); // positive - negative
        left.put(AbstractValue.ZERO, AbstractValue.POSITIVE); // positivie - zero
        left.put(AbstractValue.BOTTOM, AbstractValue.BOTTOM); // positive - bottom
        left.put(AbstractValue.TOP, AbstractValue.TOP); // positive - top
        lookup.put(AbstractValue.POSITIVE, left);

        // left -
        left = new HashMap<>();
        left.put(AbstractValue.POSITIVE, AbstractValue.NEGATIVE); // negative - positive
        left.put(AbstractValue.NEGATIVE, AbstractValue.TOP); // negative - negative
        left.put(AbstractValue.ZERO, AbstractValue.NEGATIVE); // negative - 0
        left.put(AbstractValue.BOTTOM, AbstractValue.BOTTOM); // negative - bottom
        left.put(AbstractValue.TOP, AbstractValue.TOP); // negative - top
        lookup.put(AbstractValue.NEGATIVE, left);

        // left 0
        left = new HashMap<>();
        left.put(AbstractValue.POSITIVE, AbstractValue.NEGATIVE); // 0 - positive
        left.put(AbstractValue.NEGATIVE, AbstractValue.POSITIVE); // 0 - negative
        left.put(AbstractValue.ZERO, AbstractValue.ZERO); // 0 - 0
        left.put(AbstractValue.BOTTOM, AbstractValue.BOTTOM);
        left.put(AbstractValue.TOP, AbstractValue.TOP);
        lookup.put(AbstractValue.ZERO, left);

        // left top
        left = new HashMap<>();

        lookup.put(AbstractValue.TOP, left);

        // left bottom
        left = new HashMap<>();

        lookup.put(AbstractValue.BOTTOM, left);
    }

    public final static AbstractValue plus(AbstractValue leftValue, AbstractValue rightValue) {
        HashMap<AbstractValue, HashMap<AbstractValue, AbstractValue>> lookup = new HashMap<>();
    
        HashMap<AbstractValue, AbstractValue> left;
        // left +
        left = new HashMap<>();
        left.put(AbstractValue.POSITIVE, AbstractValue.POSITIVE);
        left.put(AbstractValue.NEGATIVE, AbstractValue.TOP);
        left.put(AbstractValue.ZERO, AbstractValue.POSITIVE);
        left.put(AbstractValue.BOTTOM, AbstractValue.BOTTOM);
        left.put(AbstractValue.TOP, AbstractValue.TOP);
        lookup.put(AbstractValue.POSITIVE, left);
    
        // left -
        left = new HashMap<>();
        left.put(AbstractValue.POSITIVE, AbstractValue.TOP);
        left.put(AbstractValue.NEGATIVE, AbstractValue.NEGATIVE);
        left.put(AbstractValue.ZERO, AbstractValue.NEGATIVE);
        left.put(AbstractValue.BOTTOM, AbstractValue.BOTTOM);
        left.put(AbstractValue.TOP, AbstractValue.TOP);
        lookup.put(AbstractValue.NEGATIVE, left);
    
        // left 0
        left = new HashMap<>();
        left.put(AbstractValue.POSITIVE, AbstractValue.POSITIVE);
        left.put(AbstractValue.NEGATIVE, AbstractValue.NEGATIVE);
        left.put(AbstractValue.ZERO, AbstractValue.ZERO);
        left.put(AbstractValue.BOTTOM, AbstractValue.BOTTOM);
        left.put(AbstractValue.TOP, AbstractValue.TOP);
        lookup.put(AbstractValue.ZERO, left);
    
        // left Bottom
        left = new HashMap<>();
        left.put(AbstractValue.POSITIVE, AbstractValue.BOTTOM);
        left.put(AbstractValue.NEGATIVE, AbstractValue.BOTTOM);
        left.put(AbstractValue.ZERO, AbstractValue.BOTTOM);
        left.put(AbstractValue.BOTTOM, AbstractValue.BOTTOM);
        left.put(AbstractValue.TOP, AbstractValue.BOTTOM);
        lookup.put(AbstractValue.BOTTOM, left);
    
        // left Top
        left = new HashMap<>();
        left.put(AbstractValue.POSITIVE, AbstractValue.TOP);
        left.put(AbstractValue.NEGATIVE, AbstractValue.TOP);
        left.put(AbstractValue.ZERO, AbstractValue.TOP);
        left.put(AbstractValue.BOTTOM, AbstractValue.BOTTOM);
        left.put(AbstractValue.TOP, AbstractValue.TOP);
        lookup.put(AbstractValue.TOP, left);
    
        return lookup.get(leftValue).get(rightValue);
    }
}
