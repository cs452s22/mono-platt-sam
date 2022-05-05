package edu.sou.cs452.jlox;

import edu.sou.cs452.jlox.AbstractInterpreter.AbstractValue;
import edu.sou.cs452.jlox.generated.types.*;
import java.util.List;

class LoxFunction extends Function implements LoxCallable {
    private final Function declaration;
    private final Environment closure;
    private final AbstractEnvironment abstractClosure;

    LoxFunction(Function declaration, Environment closure) {
        this.closure = closure;
        this.declaration = declaration;
        this.abstractClosure = null;
    }

    LoxFunction(Function declaration, AbstractEnvironment closure) {
        this.closure = null;
        this.declaration = declaration;
        this.abstractClosure = closure;
    }

    @Override
    public String toString() { // gives nicer output if a user decides to print a function value
        return "<fn " + declaration.getName().getLexeme() + ">";
    }

    @Override
    public int arity() {
        return declaration.getParams().size();
    }

    @Override
    public LiteralValue call(Interpreter interpreter, List<LiteralValue> arguments) {
        Environment environment = new Environment(closure);
        for (int i = 0; i < declaration.getParams().size(); i++) {
            environment.define(declaration.getParams().get(i).getLexeme(), arguments.get(i));
        }

        try {
            interpreter.executeBlock(declaration.getBody(), environment);
        } catch (ReturnException returnValue) {
            return returnValue.getLiteralValue();
        }
        return null;
    }

    @Override
    public LiteralValue call(AbstractInterpreter interpreter, List<AbstractValue> arguments) {
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
    }
}
