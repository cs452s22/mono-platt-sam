package edu.sou.cs452.jlox;

import edu.sou.cs452.jlox.generated.types.*;
import java.util.HashMap;
import java.util.Map;

// TODO: remove the following import if commenting it out doesn't cause errors
// import static edu.sou.cs452.jlox.generated.types.TokenType.*;

class Environment {
    final Environment enclosing;
    private final Map<String, LiteralValue> values = new HashMap<>();

    Environment() {
        enclosing = null;
    }
    
    Environment(Environment enclosing) {
        this.enclosing = enclosing;
    }

    LiteralValue get(Token name) {
        if (values.containsKey(name.getLexeme())) {
          return values.get(name.getLexeme());
        }

        if (enclosing != null) { return enclosing.get(name); }
    
        throw new RuntimeError(name,
            "Undefined variable '" + name.getLexeme() + "'.");
    }

    void assign(Token name, LiteralValue value) {
        if (values.containsKey(name.getLexeme())) {
            values.put(name.getLexeme(), value);
            return;
        }

        if (enclosing != null) {
            enclosing.assign(name, value);
            return;
        }
    
        throw new RuntimeError(name,
            "Undefined variable '" + name.getLexeme() + "'.");
    }

    void define(String name, LiteralValue value) {
        values.put(name, value);
    }
}
