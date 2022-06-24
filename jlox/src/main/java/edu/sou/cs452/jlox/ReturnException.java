package edu.sou.cs452.jlox;

import edu.sou.cs452.jlox.AbstractInterpreter.AbstractValue;
import edu.sou.cs452.jlox.generated.types.*;

class ReturnException extends RuntimeException {
    LiteralValue literalValue;
    AbstractValue abstractValue;

    ReturnException(LiteralValue value) {
        super(null, null, false, false);
        this.literalValue = value;
    }

    ReturnException(AbstractValue value) {
        super(null, null, false, false);
        this.abstractValue = value;
    }

    public LiteralValue getLiteralValue() {
        return this.literalValue;
    }

    public AbstractValue getAbstractValue() {
        return this.abstractValue;
    }
}
