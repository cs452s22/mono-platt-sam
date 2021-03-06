package edu.sou.cs452.jlox;

import edu.sou.cs452.jlox.generated.types.*;
import edu.sou.cs452.jlox.generated.types.Class;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Interpreter implements ExprVisitor<LiteralValue>, StmtVisitor<Void> { // changed this line for lab 4

    final Environment<LiteralValue> globals = new Environment<LiteralValue>();
    private Environment<LiteralValue> environment = globals; 
    private final Map<Expr, Integer> locals = new HashMap<>();
    public String outputString = ""; // to be used by the elm frontend later in lab 4

    Interpreter() {
        globals.define("clock", new ClockFunction());
        globals.define("getC", new GetCFunction());
    }

    public String generateOutputString(LiteralValue value) {
        if (value instanceof LiteralBoolean) {
            return (new Boolean(((LiteralBoolean) value).getValue())).toString();
        } else if (value instanceof LiteralFloat) {
            Float f = (new Float(((LiteralFloat) value).getValue()));
            if (f % 1 == 0) { // if it is a whole number it is an integer
                return (new Integer(f.intValue())).toString();
            } else {
                return f.toString();
            }
        } else if (value instanceof LiteralString) {
            return ((LiteralString) value).getValue();
        } else {
            return value.toString();
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

    void resolve(Expr expr, int depth) {
        locals.put(expr, depth);
    }

    Void executeBlock(List<Stmt> statements, Environment<LiteralValue> environment) {
        Environment<LiteralValue> previous = this.environment;
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
        executeBlock(stmt.getStatements(), new Environment<LiteralValue>(environment));
        return null;
    }

    @Override
    public Void visitExpressionStmt(Expression stmt) {
        evaluate(stmt.getExpression());
        return null;
    }

    @Override
    public Void visitFunctionStmt(Function stmt) {
        LoxFunction function = new LoxFunction(stmt, false, environment);
        environment.define(stmt.getName().getLexeme(), function);
        return null;
    }

    @Override
    public Void visitIfStmt(If stmt) {
        LiteralValue v = evaluate(stmt.getCondition());
        if (v instanceof LiteralBoolean) {
            if (isTruthy((LiteralBoolean)v)) {
                execute(stmt.getThenBranch());
            } else if (stmt.getElseBranch() != null) {
                execute(stmt.getElseBranch());
            }
            return null;
        }
        // Error handling
        throw new RuntimeError(stmt.getCondition(),
        "Operand's condition must evaluate to a literal boolean.");
    }

    @Override
    public Void visitPrintStmt(Print stmt) {
        LiteralValue value = evaluate(stmt.getExpression());
        outputString += generateOutputString(value);
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
    public Void visitWhileStmt(While stmt) {
        LiteralValue v = evaluate(stmt.getCondition());
        if (v instanceof LiteralBoolean) {
            while (isTruthy((LiteralBoolean)v)) {
                execute(stmt.getBody());
            }
            return null;
        }
        // Error handling
        throw new RuntimeError(stmt.getCondition(),
        "Operand's condition must evaluate to a literal boolean.");
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
        return lookUpVariable(expr.getName(), expr);
    }

    private LiteralValue lookUpVariable(Token name, Expr expr) {
        Integer distance = locals.get(expr);
        if (distance != null) {
           return environment.getAt(distance, name.getLexeme());
        } else {
            return globals.get(name);
        }
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

    @Override
    public Void visitClassStmt(Class stmt) {
        LiteralValue superclass = null;
        if (stmt.getSuperclass() != null) {
            superclass = stmt.getSuperclass();
            
            if (!(superclass instanceof LoxClass)) {
                throw new RuntimeError(stmt.getSuperclass().getName(),
                    "Superclass must be a class.");
            }
        }
        environment.define(stmt.getName().getLexeme(), null);
        if (stmt.getSuperclass() != null) {
            environment = new Environment<LiteralValue>(environment);
            environment.define("super", superclass);
        }
        Map<String, LoxFunction> methods = new HashMap<>();
        for (Function method : stmt.getMethods()) {
            String s = method.getName().getLexeme();
            boolean isInit = (s.equals("init"));

            LoxFunction function = new LoxFunction(method, isInit, environment);
            methods.put(method.getName().getLexeme(), function);
        }
        LoxClass klass = new LoxClass(stmt.getName().getLexeme(), (LoxClass) superclass, methods);
        environment.assign(stmt.getName(), klass);
        return null;
    }

    @Override
    public Void visitForStmt(For stmt) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public LiteralValue visitLogicalExpr(Logical expr) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public LiteralValue visitSetExpr(Set expr) {
        LiteralValue object = accept(expr.getObject());
        if (!(object instanceof LoxClass)) {
            throw new RuntimeError(expr.getName(), "Only classes have fields.");
        }
        LiteralValue value = evaluate(expr.getValue());
        // Prototype-based inheritance by setting super directly.
        if (expr.getName().getType() == TokenType.PROTO) {
            ((LoxClass) object).setSuperklass((LoxClass) value);
        } else {
            ((LoxClass) object).set(expr.getName(), value);
        }
        return value;
    }

    @Override
    public LiteralValue visitSuperExpr(Super expr) {
        int distance = locals.get(expr);
        LoxClass superclass = (LoxClass)environment.getAt(
            distance, "super");
        LoxClass loxClass = (LoxClass)environment.getAt(
            distance - 1, "this");
        LoxFunction method = superclass.findMethod(expr.getMethod().getLexeme());

        if (method == null) {
            throw new RuntimeError(expr.getMethod(),
                "Undefined property '" + expr.getMethod().getLexeme() + "'.");
        }

        return method.bind(loxClass);
    }

    @Override
    public LiteralValue visitThisExpr(This expr) {
        return lookUpVariable(expr.getKeyword(), expr);
    }

    @Override
    public LiteralValue visitGetExpr(Get expr) {
        LiteralValue value = accept(expr.getObject());
        if (value instanceof LoxClass) {
            return ((LoxClass) value).get(expr.getName());
        }

        throw new RuntimeError(expr.getName(),
            "Only instances have properties" + value.toString());
    }
}
