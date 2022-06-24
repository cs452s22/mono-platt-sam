package edu.sou.cs452.jlox;

import edu.sou.cs452.jlox.generated.types.*;
import java.util.HashMap;
import java.util.Map;

class Environment<LiteralValue> {
    final Environment<LiteralValue> enclosing;
    protected Map<String, LiteralValue> values;

    Environment() {
        this.enclosing = null;
        this.values = new HashMap<String, LiteralValue>();
    }
    
    Environment(Environment<LiteralValue> enclosing) {
        this.enclosing = enclosing;
        this.values = new HashMap<String, LiteralValue>();
    }

    LiteralValue get(Token name) {
        if (values.containsKey(name.getLexeme())) {
          return values.get(name.getLexeme());
        }

        // If the variable isn’t found in this environment, we simply try the enclosing one
        if (enclosing != null) {
            return enclosing.get(name);
        }
    
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

    Environment<LiteralValue> ancestor(int distance) {
        Environment<LiteralValue> environment = this;
        for (int i = 0; i < distance; i++) {
            environment = environment.enclosing;
        }
        return environment;
    }

    
    LiteralValue getAt(int distance, String name) {
        Environment<LiteralValue> e = ancestor(distance);
        return e.values.get(name);
    }

    void assignAt(int distance, Token name, LiteralValue value) {
        ancestor(distance).values.put(name.getLexeme(), value);
    }
}
