package edu.sou.cs452.jlox;

import edu.sou.cs452.jlox.AbstractInterpreter.*;
import edu.sou.cs452.jlox.generated.types.*;
import java.util.List;

// functions the user writes
class LoxFunction extends Function implements LoxCallable {
    public final boolean isInitializer;
    private final Function declaration;
    private final Environment closure;
    private final AbstractEnvironment abstractClosure;

    public LoxFunction(Function declaration, Environment closure, boolean isInitializer) {
        this.closure = closure;
        this.declaration = declaration;
        this.isInitializer = isInitializer;
        this.abstractClosure = null;
    }

    public LoxFunction(Function declaration, boolean isInitializer, AbstractEnvironment closure) {
        this.closure = null;
        this.declaration = declaration;
        this.isInitializer = isInitializer;
        this.abstractClosure = closure;
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

    @Override
    public LiteralValue call(AbstractInterpreter interpreter, List<AbstractValue> arguments) {
        throw new RuntimeException("Abstract Interpretor can't handle calls for Lox Functions.");
        /*
        AbstractEnvironment environment = new AbstractEnvironment(abstractClosure);
        for (int i = 0; i < declaration.getParams().size(); i++) {
            environment.define(declaration.getParams().get(i).getLexeme(), arguments.get(i));
        }

        try {
            interpreter.executeBlock(declaration.getBody(), environment);
        } catch (ReturnException returnValue) {
            return returnValue.getLiteralValue();
        }
        return null;
        */
    }
}
