package edu.sou.cs452.jlox;

import edu.sou.cs452.jlox.generated.types.*;
import java.util.List;

import static edu.sou.cs452.jlox.generated.types.TokenType.*;

class LoxFunction extends Function implements LoxCallable {
    private final Function declaration;
    private final Environment closure;

    LoxFunction(Function declaration, Environment closure) {
        this.closure = closure;
        this.declaration = declaration;
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
            return returnValue.getValue();
        }
        return null;
    }
}
