package edu.sou.cs452.jlox;

import edu.sou.cs452.jlox.AbstractInterpreter.AbstractValue;
import edu.sou.cs452.jlox.generated.types.*;
import java.util.List;

// functions we write
public class GetCFunction extends Function implements LoxCallable {

    @Override
    public int arity() { return 0; }

    @Override
    public LiteralValue call(Interpreter interpreter, List<LiteralValue> arguments) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public LiteralValue call(AbstractInterpreter interpreter, List<AbstractValue> arguments) {
        // TODO Auto-generated method stub
        return null;
    }

    
    
}
