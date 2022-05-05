package edu.sou.cs452.jlox;

import edu.sou.cs452.jlox.generated.types.Token;
import edu.sou.cs452.jlox.generated.types.TokenType;

class RuntimeError extends RuntimeException {
  final Token token;
  final TokenType type;

  RuntimeError(Token token, String message) {
    super(message);
    this.token = token;
    this.type = null;
  }

public RuntimeError(TokenType type, String message) {
  super(message);
  this.token = null;
  this.type = type;
}
}