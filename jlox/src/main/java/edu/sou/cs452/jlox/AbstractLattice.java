package edu.sou.cs452.jlox;

import edu.sou.cs452.jlox.generated.types.*;

import static edu.sou.cs452.jlox.generated.types.TokenType.*;

public class AbstractLattice {
    enum AbstractValue {
        BOTTOM,
        NEGATIVE,
        POSITIVE,
        TOP,
        ZERO
    }

    private AbstractValue abstr;

    AbstractLattice(AbstractValue abstr) {
        this.abstr = abstr;
    }

    public AbstractValue getAbstractValue() {
        return this.abstr;
    }
}