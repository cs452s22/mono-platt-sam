package edu.sou.cs452.jlox;

import edu.sou.cs452.jlox.generated.types.*;
import java.util.List;

// functions the user writes
class LoxFunction extends Function implements LoxCallable {
    public final boolean isInitializer;
    private final Environment<LiteralValue> closure;

    public LoxFunction(Function declaration, boolean isInitializer, Environment<LiteralValue> closure) {
        super(declaration.getId(), declaration.getName(), declaration.getParams(), declaration.getBody());
        this.closure = closure;
        this.isInitializer = isInitializer;
    }

    LoxFunction bind(LoxClass loxClass) {
        Environment<LiteralValue> environment = new Environment<LiteralValue>(closure);
        environment.define("this", loxClass);

        String s = getName().getLexeme();
        boolean isInit = (s.equals("init"));

        return new LoxFunction(this, isInit, environment);
    }

    @Override
    public String toString() { // gives nicer output if a user decides to print a function value
        return "<fn " + getName().getLexeme() + ">";
    }

    @Override
    public int arity() {
        return getParams().size();
    }

    @Override
    public LiteralValue call(Interpreter interpreter, List<LiteralValue> arguments) {
        Environment<LiteralValue> callEnvironment = new Environment<LiteralValue>(closure);
        for (int i = 0; i < getParams().size(); i++) {
            callEnvironment.define(getParams().get(i).getLexeme(), arguments.get(i));
        }

        try {
            interpreter.executeBlock(getBody(), callEnvironment);
        } catch (ReturnException returnValue) {
            if (isInitializer) { return closure.getAt(0, "this"); }
            return returnValue.getLiteralValue();
        }

        if (isInitializer) { return closure.getAt(0, "this"); }

        return new LiteralNull();
    }
}
