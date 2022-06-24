package edu.sou.cs452.jlox;

import edu.sou.cs452.jlox.generated.types.Expr;
import edu.sou.cs452.jlox.generated.types.Token;
import edu.sou.cs452.jlox.generated.types.TokenType;

class RuntimeError extends RuntimeException {
  final Token token;
  final TokenType type;
  final Expr condition;

  RuntimeError(Token token, String message) {
    super(message);
    this.token = token;
    this.type = null;
    this.condition = null;
  }

  RuntimeError(TokenType type, String message) {
    super(message);
    this.token = null;
    this.type = type;
    this.condition = null;
  }

  public RuntimeError(Expr condition, String message) {
    super(message);
    this.token = null;
    this.type = null;
    this.condition = condition;
  }
}