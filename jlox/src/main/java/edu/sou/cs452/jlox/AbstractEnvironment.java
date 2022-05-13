package edu.sou.cs452.jlox;

import edu.sou.cs452.jlox.AbstractInterpreter.*; // Why did I have to do this???
import edu.sou.cs452.jlox.generated.types.*;
import java.util.HashMap;
import java.util.Map;

class AbstractEnvironment extends Environment {
    final AbstractEnvironment enclosing;
    final Map<String, AbstractValue> values = new HashMap<>();

    AbstractEnvironment() {
        enclosing = null;
    }
    
    AbstractEnvironment(AbstractEnvironment enclosing) {
        this.enclosing = enclosing;
    }

    public AbstractEnvironment cloneAbstractEnvironment() {
        AbstractEnvironment e = new AbstractEnvironment();
        e.values.putAll(this.values);
        return e;
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

    void define(String name, AbstractValue value) {
        values.put(name, value);
    }

    AbstractEnvironment ancestor(int distance) {
        AbstractEnvironment environment = this;
        for (int i = 0; i < distance; i++) {
            environment = environment.enclosing;
        }
        return environment;
    }
    
    AbstractValue getAbstractAt(int distance, String name) {
        return ancestor(distance).values.get(name);
    }

    AbstractValue getAbstract(Token name) {
        if (values.containsKey(name.getLexeme())) {
          return values.get(name.getLexeme());
        }

        // If the variable isn’t found in this environment, we simply try the enclosing one
        if (enclosing != null) {
            return enclosing.getAbstract(name);
        }
    
        throw new RuntimeError(name,
            "Undefined variable '" + name.getLexeme() + "'.");
    }


}
