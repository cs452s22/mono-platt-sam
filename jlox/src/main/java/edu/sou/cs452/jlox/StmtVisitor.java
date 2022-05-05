package edu.sou.cs452.jlox;

import edu.sou.cs452.jlox.generated.types.*;
import edu.sou.cs452.jlox.generated.types.Class;

import static edu.sou.cs452.jlox.generated.types.TokenType.*;

public interface StmtVisitor<T> {
    T visitBlockStmt(Block stmt);
    T visitClassStmt(Class stmt);
    T visitExpressionStmt(Expression stmt);
    T visitForStmt(For stmt);
    T visitFunctionStmt(Function stmt);
    T visitIfStmt(If stmt);
    T visitPrintStmt(Print stmt);
    T visitReturnStmt(Return stmt);
    T visitVarStmt(Var stmt);
    T visitWhileStmt(While stmt);
    
    default T accept(Stmt s) {
        if (s instanceof Block) {
            return visitBlockStmt((Block) s);
        } else if (s instanceof Class) {
            return visitClassStmt((Class) s);
        } else if (s instanceof Expression) {
            return visitExpressionStmt((Expression) s);
        } else if (s instanceof For) {
            return visitForStmt((For) s);
        } else if (s instanceof Function) {
            return visitFunctionStmt((Function) s);
        } else if (s instanceof If) {
            return visitIfStmt((If) s);
        } else if (s instanceof Print) {
            return visitPrintStmt((Print) s);
        } else if (s instanceof Return) {
            return visitReturnStmt((Return) s);
        } else if (s instanceof Var) {
            return visitVarStmt((Var) s);
        } else if (s instanceof While) {
            return visitWhileStmt((While) s);
        } else {
            throw new RuntimeException("Unsupported statement type: " + s.getClass().getName());
        }
    }
}