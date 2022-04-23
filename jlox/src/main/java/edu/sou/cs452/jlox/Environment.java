package edu.sou.cs452.jlox;

import edu.sou.cs452.jlox.generated.types.*;
import edu.sou.cs452.jlox.generated.types.Token;
import edu.sou.cs452.jlox.generated.types.TokenType;
import edu.sou.cs452.jlox.generated.types.LiteralBoolean;
import java.util.HashMap;
import java.util.Map;

import static edu.sou.cs452.jlox.generated.types.TokenType.*;

class Environment {
    private final Map<String, Object> values = new HashMap<>();

    Object get(Token name) {
        if (values.containsKey(name.getLexeme())) {
          return values.get(name.getLexeme());
        }
    
        throw new RuntimeError(name,
            "Undefined variable '" + name.getLexeme() + "'.");
    }

    void define(String name, Object value) {
        values.put(name, value);
    }
}
