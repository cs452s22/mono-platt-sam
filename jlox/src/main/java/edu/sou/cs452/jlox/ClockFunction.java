package edu.sou.cs452.jlox;

import edu.sou.cs452.jlox.generated.types.*;
import java.util.List;

import static edu.sou.cs452.jlox.generated.types.TokenType.*;

public class ClockFunction extends Function implements LoxCallable {

    @Override
    public int arity() { return 0; }

    @Override
    public LiteralValue call(Interpreter interpreter, List<LiteralValue> arguments) {
        return new LiteralFloat((double)System.currentTimeMillis() / 1000.0);
    }

    @Override
    public String toString() { return "<native fn>"; }
    
}
