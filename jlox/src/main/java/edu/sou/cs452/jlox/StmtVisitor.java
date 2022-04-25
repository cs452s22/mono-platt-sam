package edu.sou.cs452.jlox;

import edu.sou.cs452.jlox.generated.types.*;

public interface StmtVisitor<T> {
    T visitBlockStmt(Block stmt);
    T visitExpressionStmt(Expression stmt);
    T visitPrintStmt(Print stmt);
    T visitVarStmt(Var stmt);
    
    default T accept(Stmt s) {
        if (s instanceof Block) {
            return visitBlockStmt((Block) s);
        } else if (s instanceof Expression) {
            return visitExpressionStmt((Expression) s);
        } else if (s instanceof Print) {
            return visitPrintStmt((Print) s);
        } else if (s instanceof Var) {
            return visitVarStmt((Var) s);
        } else {
            throw new RuntimeException("Unsupported statement type: " + s.getClass().getName());
        }
    }
}