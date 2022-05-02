package edu.sou.cs452.jlox;

import edu.sou.cs452.jlox.generated.types.*;

class ReturnException extends RuntimeException {
    LiteralValue value;

    ReturnException(LiteralValue value) {
        super(null, null, false, false);
        this.value = value;
    }

    public LiteralValue getValue() {
        return this.value;
    }
}
