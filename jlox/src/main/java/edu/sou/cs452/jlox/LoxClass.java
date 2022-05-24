package edu.sou.cs452.jlox;

import edu.sou.cs452.jlox.generated.types.*;
import edu.sou.cs452.jlox.generated.types.Class;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static edu.sou.cs452.jlox.generated.types.TokenType.*;

public class LoxClass extends Class implements LoxCallable {
  final String name;
  private LoxClass superklass;
  public final Map<String, LoxFunction> methods;
  private final Map<String, LiteralValue> fields;

  LoxClass(LoxClass klass) {
    this.name = klass.name;
    this.methods = klass.methods;
    this.fields = klass.fields;
    this.superklass = klass.superklass;
  }

  LoxClass(String name, LoxClass superklass, Map<String, LoxFunction> methods) {
    this.name = name;
    this.superklass = superklass;
    this.methods = methods;
    this.fields = new HashMap<String, LiteralValue>();
  }

  protected LoxFunction findMethod(String name) {
    if (methods.containsKey(name)) {
      return methods.get(name);
    }

    if (superklass != null) {
      return superklass.findMethod(name);
    }

    return null;
  }

  public void setSuperklass(LoxClass superklass) {
    this.superklass = superklass;
  }

  LiteralValue get(Token name) {
    
    if (fields.containsKey(name.getLexeme())) {
      return fields.get(name.getLexeme());
    }
    
    // When looking up a method, return that method enclosed
    // over the current instance (this)
    LoxFunction method = findMethod(name.getLexeme());
    if (method != null) return method.bind(this);

    // We don't have field or method on this object with that name.
    // Check the parent.
    if (superklass != null && superklass.get(name) != null) {
      return superklass.get(name);
    
    }

    throw new RuntimeError(name, "Undefined property '" + name.getLexeme() + "'.");
  }

  void set(Token name, LiteralValue value) {
    fields.put(name.getLexeme(), value);
  }

  @Override
  public LiteralValue call(Interpreter interpreter, List<LiteralValue> arguments) {
    LoxClass instance = new LoxClass("", this, new HashMap<String, LoxFunction>());
    LoxFunction initializer = findMethod("init");
    if (initializer != null) {
      initializer.bind(instance).call(interpreter, arguments);
    }
    return instance;
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