package edu.sou.cs452.jlox;

import edu.sou.cs452.jlox.generated.types.*;

import static edu.sou.cs452.jlox.generated.types.TokenType.*;

public class AbstractValue {
    enum AbstractValue {
        BOTTOM,
        NEGATIVE,
        POSITIVE,
        TOP,
        ZERO
    }

    private AbstractValue abstractValue;
}