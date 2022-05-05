package edu.sou.cs452.jlox;

import edu.sou.cs452.jlox.AbstractInterpreter.*; // Why did I have to do this???
import edu.sou.cs452.jlox.generated.types.*;
import java.util.List;

interface LoxCallable {
    int arity();

    LiteralValue call(Interpreter interpreter, List<LiteralValue> arguments);
    LiteralValue call(AbstractInterpreter interpreter, List<AbstractValue> arguments);
}
