package edu.sou.cs452.jlox;

import edu.sou.cs452.jlox.AbstractInterpreter.*;
import edu.sou.cs452.jlox.AbstractInterpreter.AbstractValue;
import edu.sou.cs452.jlox.generated.types.*;
import java.util.List;

// functions we write
public class GetCFunction extends Function implements LoxCallable {

    int char_iterator = 0;

    @Override
    public int arity() {
        return 1; // arity is the number of arguments
    }

    @Override
    public LiteralValue call(Interpreter interpreter, List<LiteralValue> arguments) {
        LiteralString str = (LiteralString)arguments.get(0);
        
        System.out.println(str.getValue());
        char_iterator++;
        return str;

        // throw new IndexOutOfBoundsException("Index " + char_iterator + " out of bounds.");
    }

    @Override
    public LiteralValue call(AbstractInterpreter interpreter, List<AbstractValue> arguments) {
        throw new RuntimeException("call is not implemented for abstract interpreter");
    }
}
