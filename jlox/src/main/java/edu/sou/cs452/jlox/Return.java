package edu.sou.cs452.jlox;

import edu.sou.cs452.jlox.generated.types.*;

class Return extends RuntimeException {
    final Token keyword;
    final Expr value;
  
    Return(Expr value) {
        super(null, null, false, false);
        this.keyword = null;
        this.value = value;
    }

    public Expr getValue() {
        return this.value;
    }
}
