package edu.sou.cs452.jlox;

import edu.sou.cs452.jlox.AbstractInterpreter.*; // Why did I have to do this???
import edu.sou.cs452.jlox.generated.types.*;
import edu.sou.cs452.jlox.generated.types.Class;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static edu.sou.cs452.jlox.generated.types.TokenType.*;

public class AbstractInterpreter implements ExprVisitor<AbstractValue>, StmtVisitor<Void> {

    public enum AbstractValue {
        BOTTOM,
        NEGATIVE,
        POSITIVE,
        TOP,
        ZERO
    }

    static final AbstractValue BOTTOM = AbstractValue.BOTTOM;
    static final AbstractValue NEGATIVE = AbstractValue.NEGATIVE;
    static final AbstractValue POSITIVE = AbstractValue.POSITIVE;
    static final AbstractValue TOP = AbstractValue.TOP;
    static final AbstractValue ZERO = AbstractValue.ZERO;

    final AbstractEnvironment globals = new AbstractEnvironment();
    private AbstractEnvironment environment = globals;
    public String outputString = ""; // to be used by the elm frontend later in lab 4 (and 5)

    AbstractInterpreter() {
        globals.define("clock", new ClockFunction());
    }

    public String generateOutputString(AbstractValue value) {
        if (value == BOTTOM) return "BOTTOM";
        if (value == NEGATIVE) return "NEGATIVE";
        if (value == POSITIVE) return "POSITIVE";
        if (value == TOP) return "TOP";
        if (value == ZERO) return "ZERO";
        throw new RuntimeException("Output is not recognizable");
    }
    
    public String getOutputString() {
        return outputString;
    }

    private AbstractValue evaluate(Expr expr) {
        return accept(expr);
    }

    private Void execute(Stmt stmt) {
        accept(stmt);
        return null;
    }

    Void executeBlock(List<Stmt> statements, AbstractEnvironment environment) {
        AbstractEnvironment previous = this.environment;
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
        executeBlock(stmt.getStatements(), new AbstractEnvironment(environment));
        return null;
    }

    @Override
    public Void visitExpressionStmt(Expression stmt) {
        evaluate(stmt.getExpression());
        return null;
    }

    @Override
    public Void visitFunctionStmt(Function stmt) {
        throw new RuntimeException("Abstract Interpretor can't handle function statements.");
    }

    @Override
    public Void visitIfStmt(If stmt) {
        AbstractValue v = evaluate(stmt.getCondition());

        AbstractEnvironment thenEnvironment = environment.cloneAbstractEnvironment();
        AbstractEnvironment elseEnvironment = environment.cloneAbstractEnvironment();
        AbstractEnvironment currentEnvironment;

        AbstractValue thenBranchValue = evaluate(stmt.getCondition()); // evaluate the then branch
        currentEnvironment = thenEnvironment; // set the environment to the "then branch" environment
        execute(stmt.getThenBranch());

        AbstractValue elseBranchValue = evaluate(stmt.getCondition()); // evaluate the else branch
        currentEnvironment = elseEnvironment; // set the environment to the "else branch" environment
        execute(stmt.getElseBranch());

        // join the maps of values based on their keys, then do the top/bottom/negative/zero/join thing
        HashMap<String, AbstractValue> thenMap = new HashMap<String, AbstractValue>();
        HashMap<String, AbstractValue> elseMap = new HashMap<String, AbstractValue>();
        thenMap.putAll(thenEnvironment.values);
        elseMap.putAll(elseEnvironment.values);

        Set<String> thenKeys = thenMap.keySet();
        Set<String> elseKeys = elseMap.keySet();

        for (String thenKey : thenKeys) { // for each then key
            for (String elseKey : elseKeys) { // for each else key
                if (thenKey.equals(elseKey)) { // if the keys are the same
                    AbstractValue thenValue = thenMap.get(thenKey);
                    AbstractValue elseValue = elseMap.get(elseKey);
                    AbstractValue unionValue = null;

                    /* compare the values of the keys (top/positive/zero/negative/bottom) 
                     * to get the union as an AbstractValue */
                    if (thenValue == BOTTOM) { // bottom union ???
                        unionValue = BOTTOM;
                    
                    } else if (thenValue == POSITIVE) { // positive union ???
                        if (elseValue == POSITIVE) { // positive union positive
                            unionValue = POSITIVE;     
                        } else if (elseValue == NEGATIVE ) { // positive union negative
                            unionValue = TOP;
                        } else if (elseValue == ZERO) { // positive union zero
                            unionValue = TOP;
                        } else if (elseValue == TOP) { // positive union top
                            unionValue = TOP;
                        } else if (elseValue == BOTTOM) { // positive union bottom
                            unionValue = BOTTOM;
                        }
                    } else if (thenValue == ZERO) { // zero union ???
                        if (elseValue == BOTTOM) { // zero union bottom
                            unionValue = BOTTOM;
                        } else if (elseValue == ZERO) { // zero union zero
                            unionValue = ZERO;
                        } else {
                            unionValue = TOP;
                        }

                    } else if (thenValue == NEGATIVE) { // negative union ???
                        if (elseValue == NEGATIVE) { // negative union negative
                            unionValue = NEGATIVE;
                        } else if (elseValue == BOTTOM) { // negative union bottom
                            unionValue = BOTTOM;
                        } else {
                            unionValue = TOP;
                        }

                    } else if (thenValue == TOP) { // top union ???
                        if (elseValue == BOTTOM) { // top union bottom
                            unionValue = BOTTOM;
                        } else {
                            unionValue = TOP;
                        }
                    }
                    
                    // outputString += unionValue.toString();
                }
            }
        }

        // Error handling
        throw new RuntimeError(stmt.getCondition(),
        "Operand's condition must evaluate to a literal boolean.");
    }

    @Override
    public Void visitPrintStmt(Print stmt) {
        AbstractValue value = evaluate(stmt.getExpression());
        outputString += generateOutputString(value);
        return null;
    }

    @Override
    public Void visitReturnStmt(Return stmt) {
        /*
        AbstractValue abstrValue = null;
        if (stmt.getValue() != null) {
            abstrValue = evaluate(stmt.getValue());
            return null;
        }
        */
        throw new RuntimeException("Null value for return statement stmt");
    }

    @Override
    public Void visitVarStmt(Var stmt) {
        AbstractValue value = null;
        if (stmt.getInitializer() != null) {
            value = evaluate(stmt.getInitializer());
        }

        environment.define(stmt.getName().getLexeme(), value);
        return null;

       //  throw new RuntimeException("Abstract Interpretor can't handle var statements.");
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
            case PLUS:
                return plus(left, right);
            case SLASH:
                return minus(left, right);
            case STAR: // multiplication
                return star(left, right);
            default:
                throw new RuntimeError(expr.getOperator(), 
                "The method visitBinary() not implemented for this operator type.");   
        }
    }

    @Override
    public AbstractValue visitLiteralExpr(Literal expr) {
        if (expr != null) { // if expr is not null
            LiteralValue v = expr.getValue();
            if (v instanceof LiteralFloat) { // if it's a LiteralFloat
                LiteralFloat f = (LiteralFloat) v;
                if (f.getValue() > 0) return POSITIVE; // if the literalfloat is positive
                if (f.getValue() < 0) return NEGATIVE; // if the literalfloat is negative
                if (f.getValue() == 0) return ZERO; // if the literalfloat is zero
                return BOTTOM;
            }
            return BOTTOM;
        }
        throw new RuntimeException("Literal expression is null");
    }

    @Override
    public AbstractValue visitCallExpr(Call expr) {
        throw new RuntimeException("Abstract Interpretor can't handle call expressions.");
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
            case MINUS:
                return minus(right);
            default:
                throw new RuntimeError(expr.getOperator(), 
                "The method visitUnaryExpr() not implemented for this operator type.");   
        }
    }

    @Override
    public AbstractValue visitVariableExpr(Variable expr) {
        throw new RuntimeError(expr.getName(),
        "The method visitVariableExpr() is not implemented.");
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

    public Void interpret(List<Stmt> statements) {
        try {
            for (Stmt statement : statements) {
                accept(statement);
            }
        } catch (RuntimeError error) {
            Lox.runtimeError(error);
        }
        return null;
    }

    // TODO: get confirmation this is correct
    public final static AbstractValue bang(AbstractValue rightValue) {
        HashMap<AbstractValue, AbstractValue> lookup = new HashMap<>();
        
        lookup.put(POSITIVE, NEGATIVE); // ! positive
        lookup.put(NEGATIVE, POSITIVE); // ! negative
        lookup.put(ZERO, ZERO); // ! zero
        lookup.put(BOTTOM, BOTTOM); // ! bottom
        lookup.put(TOP, TOP); // ! top

        return lookup.get(rightValue);
    }

    public final static AbstractValue minus(AbstractValue rightValue) {
        HashMap<AbstractValue, AbstractValue> lookup = new HashMap<>();

        lookup.put(POSITIVE, NEGATIVE); // - positive
        lookup.put(NEGATIVE, POSITIVE); // - negative
        lookup.put(ZERO, ZERO); // - zero
        lookup.put(BOTTOM, BOTTOM); // - bottom
        lookup.put(TOP, TOP); // - top

        return lookup.get(rightValue);
    }

    public final static AbstractValue minus(AbstractValue leftValue, AbstractValue rightValue) {
        HashMap<AbstractValue, HashMap<AbstractValue, AbstractValue>> lookup = new HashMap<>();
    
        HashMap<AbstractValue, AbstractValue> left;

        // left +
        left = new HashMap<>();
        left.put(POSITIVE, TOP); // positive - positive
        left.put(NEGATIVE, POSITIVE); // positive - negative
        left.put(ZERO, POSITIVE); // positivie - zero
        left.put(BOTTOM, BOTTOM); // positive - bottom
        left.put(TOP, TOP); // positive - top
        lookup.put(POSITIVE, left);

        // left -
        left = new HashMap<>();
        left.put(POSITIVE, NEGATIVE); // negative - positive
        left.put(NEGATIVE, TOP); // negative - negative
        left.put(ZERO, NEGATIVE); // negative - 0
        left.put(BOTTOM, BOTTOM); // negative - bottom
        left.put(TOP, TOP); // negative - top
        lookup.put(NEGATIVE, left);

        // left 0
        left = new HashMap<>();
        left.put(POSITIVE, NEGATIVE); // 0 - positive
        left.put(NEGATIVE, POSITIVE); // 0 - negative
        left.put(ZERO, ZERO); // 0 - 0
        left.put(BOTTOM, BOTTOM);
        left.put(TOP, TOP);
        lookup.put(ZERO, left);

        // left top
        left = new HashMap<>();
        left.put(POSITIVE, TOP); // top - positive
        left.put(NEGATIVE, TOP); // top - negative
        left.put(ZERO, TOP); // top - zero
        left.put(BOTTOM, BOTTOM); // top - bottom
        left.put(TOP, TOP); // top - top
        lookup.put(TOP, left);

        // left bottom
        left = new HashMap<>();
        left.put(POSITIVE, BOTTOM); // bottom - positive
        left.put(NEGATIVE, BOTTOM); // bottom - negative
        left.put(ZERO, BOTTOM); // bottom - zero
        left.put(BOTTOM, BOTTOM); // bottom - bottom
        left.put(TOP, BOTTOM); // bottom - top
        lookup.put(BOTTOM, left);
        
        return lookup.get(leftValue).get(rightValue);
    }

    public final static AbstractValue plus(AbstractValue leftValue, AbstractValue rightValue) {
        HashMap<AbstractValue, HashMap<AbstractValue, AbstractValue>> lookup = new HashMap<>();
    
        HashMap<AbstractValue, AbstractValue> left;
        // left +
        left = new HashMap<>();
        left.put(POSITIVE, POSITIVE); // positive + positive
        left.put(NEGATIVE, TOP); // positive + negative
        left.put(ZERO, POSITIVE); // positive + zero
        left.put(BOTTOM, BOTTOM); // positive + bottom
        left.put(TOP, TOP); // positive + top
        lookup.put(POSITIVE, left);
    
        // left -
        left = new HashMap<>();
        left.put(POSITIVE, TOP); // negative + positive
        left.put(NEGATIVE, NEGATIVE); // negative + negative
        left.put(ZERO, NEGATIVE); // negative + zero
        left.put(BOTTOM, BOTTOM); // negative + bottom
        left.put(TOP, TOP); // negative + top
        lookup.put(NEGATIVE, left);
    
        // left 0
        left = new HashMap<>();
        left.put(POSITIVE, POSITIVE); // 0 + positive
        left.put(NEGATIVE, NEGATIVE); // 0 + negative
        left.put(ZERO, ZERO); // 0 + 0
        left.put(BOTTOM, BOTTOM); // 0 + bottom
        left.put(TOP, TOP); // 0 + top
        lookup.put(ZERO, left);
    
        // left Bottom
        left = new HashMap<>();
        left.put(POSITIVE, BOTTOM); // bottom + positive
        left.put(NEGATIVE, BOTTOM); // bottom + negative
        left.put(ZERO, BOTTOM); // bottom + zero
        left.put(BOTTOM, BOTTOM); // bottom + top
        left.put(TOP, BOTTOM); // bottom + bottom
        lookup.put(BOTTOM, left);
    
        // left Top
        left = new HashMap<>();
        left.put(POSITIVE, TOP); // top + positive
        left.put(NEGATIVE, TOP); // top + negative
        left.put(ZERO, TOP); // top + 0
        left.put(BOTTOM, BOTTOM); // top + bottom
        left.put(TOP, TOP); // top + top
        lookup.put(TOP, left);
    
        return lookup.get(leftValue).get(rightValue);
    }

    public final static AbstractValue star(AbstractValue leftValue, AbstractValue rightValue) {
        HashMap<AbstractValue, HashMap<AbstractValue, AbstractValue>> lookup = new HashMap<>();
    
        HashMap<AbstractValue, AbstractValue> left;

        // left +
        left = new HashMap<>();
        left.put(POSITIVE, POSITIVE); // positive * positive
        left.put(NEGATIVE, NEGATIVE); // positive * negative
        left.put(ZERO, ZERO); // positive * zero
        left.put(BOTTOM, BOTTOM); // positive * bottom
        left.put(TOP, TOP); // positive * top
        lookup.put(POSITIVE, left);

        // left -
        left = new HashMap<>();
        left.put(POSITIVE, NEGATIVE); // negative * positive
        left.put(NEGATIVE, NEGATIVE); // negative * negative
        left.put(ZERO, ZERO); // negative * zero
        left.put(BOTTOM, BOTTOM); // negative * bottom
        left.put(TOP, TOP); // negative * top
        lookup.put(NEGATIVE, left);

        // left 0
        left = new HashMap<>();
        left.put(POSITIVE, ZERO); // zero * positive
        left.put(NEGATIVE, ZERO); // zero * negative
        left.put(ZERO, ZERO); // zero * zero
        left.put(BOTTOM, BOTTOM); // zero * bottom
        left.put(TOP, ZERO); // zero * top
        lookup.put(ZERO, left);

        // left top
        left = new HashMap<>();
        left.put(POSITIVE, TOP); // top * positive
        left.put(NEGATIVE, TOP); // top * negative
        left.put(ZERO, ZERO); // top * zero
        left.put(BOTTOM, BOTTOM); // top * bottom
        left.put(TOP, TOP); // top * top
        lookup.put(TOP, left);

        // left bottom
        left = new HashMap<>();
        left.put(POSITIVE, BOTTOM); // bottom * positive
        left.put(NEGATIVE, BOTTOM); // bottom * negative
        left.put(ZERO, BOTTOM); // bottom * zero
        left.put(BOTTOM, BOTTOM); // bottom * bottom
        left.put(TOP, BOTTOM); // bottom * bottom
        lookup.put(BOTTOM, left);

        return lookup.get(leftValue).get(rightValue);
    }

    @Override
    public Void visitClassStmt(Class stmt) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Void visitForStmt(For stmt) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Void visitWhileStmt(While stmt) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public AbstractValue visitLogicalExpr(Logical expr) {
        // TODO Auto-generated method stub
        return null;
    }
}
