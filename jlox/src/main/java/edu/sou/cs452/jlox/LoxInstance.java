package edu.sou.cs452.jlox;

import edu.sou.cs452.jlox.generated.types.*;
import edu.sou.cs452.jlox.generated.types.Class;
import java.util.HashMap;
import java.util.Map;

class LoxInstance extends LoxClass {
  private LoxClass klass;
  private final Map<String, LiteralValue> fields = new HashMap<>();


  LoxInstance(LoxClass klass) {
    super(klass.name, klass.methods);
    this.klass = klass;
  }

  public LoxClass getLoxClass() {
    return klass;
  }

  LiteralValue get(Token name) {
    if (fields.containsKey(name.getLexeme())) {
      return fields.get(name.getLexeme());
    }

    LoxFunction method = klass.findMethod(name.getLexeme());
    if (method != null) {
      return method.bind(this);
    }

    throw new RuntimeError(name, "Undefined property '" + name.getLexeme() + "'.");
  }

  void set(Token name, LiteralValue value) {
    fields.put(name.getLexeme(), value);
  }

  @Override
  public String toString() {
    return klass.name + " instance";
  }
}
