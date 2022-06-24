package edu.sou.cs452.jlox;

import edu.sou.cs452.jlox.generated.types.*;

class ReturnException extends RuntimeException {
    LiteralValue literalValue;

    ReturnException(LiteralValue value) {
        super(null, null, false, false);
        this.literalValue = value;
    }

    public LiteralValue getLiteralValue() {
        return this.literalValue;
    }
}
