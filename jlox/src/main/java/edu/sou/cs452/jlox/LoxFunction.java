package edu.sou.cs452.jlox;

import edu.sou.cs452.jlox.generated.types.*;
import java.util.List;

// functions the user writes
class LoxFunction extends Function implements LoxCallable {
    public final boolean isInitializer;
    private final Function declaration;
    private final Environment closure;

    public LoxFunction(Function declaration, boolean isInitializer, Environment closure) {
        this.closure = closure;
        this.declaration = declaration;
        this.isInitializer = isInitializer;
    }

    LoxFunction bind(LoxInstance instance) {
        Environment environment = new Environment(closure);
        environment.define("this", instance);

        String s = declaration.getName().getLexeme();
        boolean isInit = (s.equals("init"));

        return new LoxFunction(declaration, isInit, environment);
    }

    @Override
    public String toString() { // gives nicer output if a user decides to print a function value
        return "<fn " + declaration.getName().getLexeme() + ">";
    }

    @Override
    public int arity() {
        return getParams().size();
    }

    @Override
    public LiteralValue call(Interpreter interpreter, List<LiteralValue> arguments) {
        Environment callEnvironment = new Environment(closure);

        for (int i = 0; i < getParams().size(); i++) {
            callEnvironment.define(getParams().get(i).getLexeme(), arguments.get(i));
        }

        try {
            interpreter.executeBlock(getBody(), callEnvironment);
        } catch (ReturnException returnValue) {
            if (isInitializer) { return closure.getAt(0, "this"); }
            return returnValue.getLiteralValue();
        }

        if (isInitializer) return closure.getAt(0, "this");

        return new LiteralNull();
    }
}
