package edu.sou.cs452.jlox;

import edu.sou.cs452.jlox.generated.types.*;
import edu.sou.cs452.jlox.generated.types.Class;
import java.util.List;
import java.util.Map;

import static edu.sou.cs452.jlox.generated.types.TokenType.*;

class LoxClass extends Class implements LoxCallable {
  final String name;
  public final Map<String, LoxFunction> methods;

  LoxClass(String name, Map<String, LoxFunction> methods) {
    this.name = name;
    this.methods = methods;
  }

  LoxFunction findMethod(String name) {
    if (methods.containsKey(name)) {
      return methods.get(name);
    }

    return null;
  }

  @Override
  public LiteralValue call(Interpreter interpreter, List<LiteralValue> arguments) {
    LoxInstance instance = new LoxInstance(this);
    LoxFunction initializer = findMethod("init");
    if (initializer != null) {
      initializer.bind(instance).call(interpreter, arguments);
    }
    LoxClass klass = instance.getLoxClass();
    return klass;
  }

  @Override
  public int arity() {
    LoxFunction initializer = findMethod("init");
    if (initializer == null) { return 0; }
    return initializer.arity();
  }

  @Override
  public String toString() {
    return name;
  }
}