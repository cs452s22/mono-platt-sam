package edu.sou.cs452.jlox;

import edu.sou.cs452.jlox.AbstractInterpreter.*; // Why did I have to do this???
import edu.sou.cs452.jlox.generated.types.*;
import java.util.HashMap;
import java.util.Map;

class AbstractEnvironment extends Environment {
    final AbstractEnvironment enclosing;
    private final Map<String, AbstractValue> values = new HashMap<>();

    AbstractEnvironment() {
        enclosing = null;
    }
    
    AbstractEnvironment(AbstractEnvironment enclosing) {
        this.enclosing = enclosing;
    }

    void assign(Token name, AbstractValue value) {
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
}
