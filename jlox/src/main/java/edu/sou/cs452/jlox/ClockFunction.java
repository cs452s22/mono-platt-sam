package edu.sou.cs452.jlox;

import edu.sou.cs452.jlox.AbstractInterpreter.AbstractValue;
import edu.sou.cs452.jlox.generated.types.*;
import java.util.List;

public class ClockFunction extends Function implements LoxCallable {

    @Override
    public int arity() { return 0; }

    @Override
    public LiteralValue call(Interpreter interpreter, List<LiteralValue> arguments) {
        return new LiteralFloat((double)System.currentTimeMillis() / 1000.0);
    }

    @Override
    public LiteralValue call(AbstractInterpreter interpreter, List<AbstractValue> arguments) {
        return new LiteralFloat((double)System.currentTimeMillis() / 1000.0);
    }

    @Override
    public String toString() { return "<native fn>"; }
    
}
