package edu.sou.cs452.jlox;

import edu.sou.cs452.jlox.generated.types.*;

// TODO: remove the following import if commenting it out doesn't cause errors
// import static edu.sou.cs452.jlox.generated.types.TokenType.*;

class RuntimeError extends RuntimeException {
  final Token token;

  RuntimeError(Token token, String message) {
    super(message);
    this.token = token;
  }
}