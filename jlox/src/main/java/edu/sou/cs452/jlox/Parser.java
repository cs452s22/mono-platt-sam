package edu.sou.cs452.jlox;

import edu.sou.cs452.jlox.generated.types.*;
import edu.sou.cs452.jlox.generated.types.Token;
import edu.sou.cs452.jlox.generated.types.TokenType;
import edu.sou.cs452.jlox.generated.types.LiteralBoolean;
import java.util.ArrayList;
import java.util.List;

import static edu.sou.cs452.jlox.generated.types.TokenType.*;

public class Parser {
    private static class ParseError extends RuntimeException {}

    private final List<Token> tokens;
    private int current = 0;

    Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    List<Stmt> parse() {
        List<Stmt> statements = new ArrayList<>();
        while (!isAtEnd()) {
            statements.add(declaration());
        }
    
        return statements; 
    }

    private Expr expression() {
        return equality();
    }

    private Stmt declaration() {
        try {
            if (match(VAR)) return varDeclaration();
    
            return statement();
        } catch (ParseError error) {
            synchronize();
            return null;
        }
    }

    private Stmt statement() {
        if (match(PRINT)) return printStatement();
    
        return expressionStatement();
    }

    private Stmt printStatement() {
        Expr value = expression();
        consume(SEMICOLON, "Expect ';' after value.");
        return new Stmt.Print(value);
    }

    private Stmt varDeclaration() {
        Token name = consume(IDENTIFIER, "Expect variable name.");
    
        Expr initializer = null;
        if (match(EQUAL)) {
            initializer = expression();
        }
    
        consume(SEMICOLON, "Expect ';' after variable declaration.");
        return new Stmt.Var(name, initializer);
    }

    private Stmt expressionStatement() {
        Expr expr = expression();
        consume(SEMICOLON, "Expect ';' after expression.");
        return new Stmt.Expression(expr);
    }

    private Expr equality() {
        Expr expr = comparison();

        while (match(BANG_EQUAL, EQUAL_EQUAL)) {
            Token operator = previous();
            Expr right = comparison();
            int id = current; // set id to current id
            current++; // increment current by 1 for next
            expr = new Binary(id, expr, operator, right);
        }

        return expr;
    }

    private Expr comparison() {
        Expr expr = term();

        while (match(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)) {
            Token operator = previous();
            Expr right = term();
            int id = current; // set id to current id
            current++; // increment current by 1 for next

            expr = new Binary(id, expr, operator, right);
        }

        return expr;
    }

    private Expr term() {
        Expr expr = factor();

        while (match(MINUS, PLUS)) {
            Token operator = previous();
            Expr right = factor();

            int id = current; // set id to current id
            current++; // increment current by 1 for next
            
            expr = new Binary(id, expr, operator, right);
        }

        return expr;
    }

    private Expr factor() {
        Expr expr = unary();
    
        while (match(SLASH, STAR)) {
            Token operator = previous();
            Expr right = unary();

            int id = current; // set id to current id
            current++; // increment current by 1 for next

            expr = new Binary(id, expr, operator, right);
        }
    
        return expr;
    }

    private Expr unary() {
        if (match(BANG, MINUS)) {
            Token operator = previous();
            Expr right = unary();

            int id = current; // set id to current id
            current++; // increment current by 1 for next

            return new Unary(id, operator, right);
        }
    
        return primary();
    }

    private Expr primary() {
        if (match(FALSE)) {
            int id = current; // set id to current id
            current++; // increment current by 1 for next

            return new Literal(id, new LiteralBoolean(false));
        }
        if (match(TRUE)) {
            int id = current; // set id to current id
            current++; // increment current by 1 for next

            return new Literal(id, new LiteralBoolean(true));
        }
        if (match(NIL)) {
            int id = current; // set id to current id
            current++; // increment current by 1 for next

            return new Literal(id, null);
        }
        if (match(NUMBER, STRING)) {
            int id = current; // set id to current id
            current++; // increment current by 1 for next

            return new Literal(id, previous().getLiteral());
        }
        
        if (match(IDENTIFIER)) {
            return new Expr.Variable(previous());
        }

        if (match(LEFT_PAREN)) {
            Expr expr = expression();
            consume(RIGHT_PAREN, "Expect ')' after expression.");

            int id = current; // set id to current id
            current++; // increment current by 1 for next
            
            return new Grouping(id, expr);
        }
        
        throw error(peek(), "Expect expression.");
    }

    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }

        return false;
    }

    private Token consume(TokenType type, String message) {
        if (check(type)) return advance();
    
        throw error(peek(), message);
    }

    private boolean check(TokenType type) {
        if (isAtEnd()) return false;
        return peek().getType() == type;
    }

    private Token advance() {
        if (!isAtEnd()) current++;
        return previous();
    }

    private boolean isAtEnd() {
        return peek().getType() == EOF;
    }

    private Token peek() {
        return tokens.get(current);
    }

    private Token previous() {
        return tokens.get(current - 1);
    }

    private ParseError error(Token token, String message) {
        Lox.error(token, message);
        return new ParseError();
    }

    private void synchronize() {
        advance();
    
        while (!isAtEnd()) {
            if (previous().getType() == SEMICOLON) return;
    
            switch (peek().getType()) {
                case CLASS:
                case FUN:
                case VAR:
                case FOR:
                case IF:
                case WHILE:
                case PRINT:
                case RETURN:
                    return;
            }
    
            advance();
        }
    }
}
