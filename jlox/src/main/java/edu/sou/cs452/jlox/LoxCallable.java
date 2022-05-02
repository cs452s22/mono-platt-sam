package edu.sou.cs452.jlox;

import edu.sou.cs452.jlox.generated.types.*;
import java.util.List;

import static edu.sou.cs452.jlox.generated.types.TokenType.*;

interface LoxCallable {
    int arity();

    LiteralValue call(Interpreter interpreter, List<LiteralValue> arguments);
}
