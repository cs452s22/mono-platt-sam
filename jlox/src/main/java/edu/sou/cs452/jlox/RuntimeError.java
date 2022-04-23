package edu.sou.cs452.jlox;

import edu.sou.cs452.jlox.generated.types.*;
import edu.sou.cs452.jlox.generated.types.Token;
import edu.sou.cs452.jlox.generated.types.TokenType;

import static edu.sou.cs452.jlox.generated.types.TokenType.*;

class RuntimeError extends RuntimeException {
  final Token token;

  RuntimeError(Token token, String message) {
    super(message);
    this.token = token;
  }
}