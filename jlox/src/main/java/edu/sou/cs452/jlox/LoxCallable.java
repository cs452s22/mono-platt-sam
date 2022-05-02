package edu.sou.cs452.jlox;

import edu.sou.cs452.jlox.generated.types.*;
import java.util.List;

import static edu.sou.cs452.jlox.generated.types.TokenType.*;

interface LoxCallable {
    int arity();

    Expr call(Interpreter interpreter, List<Expr> arguments);
}
