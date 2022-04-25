package edu.sou.cs452.jlox;

import edu.sou.cs452.jlox.generated.types.*;

public interface StmtVisitor<T> {
    T visitBlockStmt(Block stmt);
    T visitExpressionStmt(Expression stmt);
    T visitPrintStmt(Print stmt);
    T visitVarStmt(Var stmt);
    
    default Void accept(Stmt s) {
        if (s instanceof Block) {
            // TODO: Find out what goes before the return statement
            return null;
        } else if (s instanceof Expression) {
            // TODO: Find out what goes before the return statement
            return null;
        } else if (s instanceof Print) {
            // TODO: Find out what goes before the return statement
            return null;
        } else if (s instanceof Var) {
            // TODO: Find out what goes before the return statement
            return null;
        } else {
            throw new RuntimeException("Unsupported statement type: " + s.getClass().getName());
        }
    }
}