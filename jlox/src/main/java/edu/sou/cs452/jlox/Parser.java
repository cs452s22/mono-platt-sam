package edu.sou.cs452.jlox;

import edu.sou.cs452.jlox.generated.types.*;
import edu.sou.cs452.jlox.generated.types.Class;
import java.util.ArrayList;
import java.util.List;

import static edu.sou.cs452.jlox.generated.types.TokenType.*;

public class Parser {
    private static class ParseError extends RuntimeException {
        public ParseError(String message) {
            super(message);
        }
    }

    private final List<Token> tokens;
    private int current = 0;

    Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public int getCurrent() {
        return current;
    }

    List<Stmt> parse() {
        List<Stmt> statements = new ArrayList<>();
        while (!isAtEnd()) {
            statements.add(declaration());
        }
    
        return statements; 
    }

    private Expr expression() {
        return assignment(); // changed for lab 4
    }

    private Stmt declaration() {
        try {
            if (match(CLASS)) {
                return loxClassDeclaration();
            }
            if (match(FUN)) {
                return function("function");
            }
            if (match(VAR)) {
                return varDeclaration();
            }
            return statement();
        } catch (ParseError error) {
            synchronize();
            return null;
        }
    }

    private Stmt loxClassDeclaration() {
        Token name = consume(IDENTIFIER, "Expect class name.");
        Variable superclass = null;
        if (match(LESS)) {
            consume(IDENTIFIER, "Expect superclass name.");
            superclass = new Variable(current, previous());
        }
        consume(LEFT_BRACE, "Expect '{' before class body.");
    
        List<Function> methods = new ArrayList<>(); // TODO: turn this into a map
        while (!check(RIGHT_BRACE) && !isAtEnd()) {
          methods.add(function("method"));
        }
    
        consume(RIGHT_BRACE, "Expect '}' after class body.");
    
        return new Class(current, name, superclass, methods);
    }

    private Stmt statement() {
        if (match(PRINT)) {
            return printStatement();
        }
        if (match(RETURN)) {
            return returnStatement();
        }
        if (match(LEFT_BRACE)) {
            int id = current; // set id to current id

            return new Block(id, block());
        }
    
        return expressionStatement();
    }

    private Stmt printStatement() {
        Expr value = expression();
        consume(SEMICOLON, "Expect ';' after value.");

        int id = current; // set id to current id

        return new Print(id, value);
    }

    private Return returnStatement() {
        Token keyword = previous();
        Expr value = null;
        if (!check(SEMICOLON)) {
          value = expression();
        }

        consume(SEMICOLON, "Expect ';' after return value.");
        int id = current;
        return new Return(id, keyword, value);
    }

    private Stmt varDeclaration() {
        Token name = consume(IDENTIFIER, "Expect variable name.");
    
        Expr initializer = null;
        if (match(EQUAL)) {
            initializer = expression();
        }
    
        consume(SEMICOLON, "Expect ';' after variable declaration.");

        int id = current; // set id to current id

        return new Var(id, name, initializer);
    }

    private Stmt expressionStatement() {
        Expr expr = expression();
        consume(SEMICOLON, "Expect ';' after expression.");

        int id = current; // set id to current id

        return new Expression(id, expr);
    }

    private Function function(String kind) {
        Token name = consume(IDENTIFIER, "Expect " + kind + " name.");
        consume(LEFT_PAREN, "Expect '(' after " + kind + " name.");
        List<Token> parameters = new ArrayList<>();
        if (!check(RIGHT_PAREN)) {
            do {
                if (parameters.size() >= 255) {
                error(peek(), "Can't have more than 255 parameters.");
                }

                parameters.add(
                    consume(IDENTIFIER, "Expect parameter name."));
            } while (match(COMMA));
        }
        consume(RIGHT_PAREN, "Expect ')' after parameters.");
        consume(LEFT_BRACE, "Expect '{' before " + kind + " body.");
        List<Stmt> body = block();
        int id = current;
        return new Function(id, name, parameters, body);
    }

    private List<Stmt> block() {
        List<Stmt> statements = new ArrayList<>();
    
        while (!check(RIGHT_BRACE) && !isAtEnd()) {
          statements.add(declaration());
        }
    
        consume(RIGHT_BRACE, "Expect '}' after block.");
        return statements;
    }

    private Expr assignment() {
        Expr expr = equality();
        if (match(EQUAL)) {

            Token equals = previous();
            Expr value = assignment();
        
            if (expr instanceof Variable) {
                Token name = ((Variable)expr).getName();

                int id = current; // set id to current id

                return new Assign(id, name, value);
            } else if (expr instanceof Get) {
                Get get = (Get)expr;
                return new Set(current, get.getObject(), get.getName(), value);
            }
            error(equals, "Invalid assignment target."); 
        }
        return expr;
    }

    private Expr equality() {
        Expr expr = comparison();

        while (match(BANG_EQUAL, EQUAL_EQUAL)) {
            Token operator = previous();
            Expr right = comparison();
            int id = current; // set id to current id
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

            expr = new Binary(id, expr, operator, right);
        }
    
        return expr;
    }

    private Expr unary() {
        if (match(BANG, MINUS)) {
            Token operator = previous();
            Expr right = unary();

            int id = current; // set id to current id

            return new Unary(id, operator, right);
        }
    
        return call(); // changed during lab 5
    }

    private Expr finishCall(Expr callee) {
        List<Expr> arguments = new ArrayList<>();
        if (!check(RIGHT_PAREN)) {
            do {
                if (arguments.size() >= 255) {
                    error(peek(), "Can't have more than 255 arguments.");
                }
                arguments.add(expression());
            } while (match(COMMA));
        }
        
        int id = current; // set id to current id
        Token paren = consume(RIGHT_PAREN, "Expect ')' after arguments.");

        return new Call(id, callee, paren, arguments); // modified during lab 5 part 2
    }

    private Expr call() {
        Expr expr = primary();
    
        while (true) { 
          if (match(LEFT_PAREN)) {
            expr = finishCall(expr);
            } else if (match(DOT)) {
            Token name = consume(IDENTIFIER,
                "Expect property name after '.'.");
            expr = new Get(current, expr, name);
          } else { break; }
        }
    
        return expr;
    }

    private Expr primary() {
        if (match(FALSE)) {
            int id = current; // set id to current id

            return new Literal(id, new LiteralBoolean(false));
        }
        if (match(TRUE)) {
            int id = current; // set id to current id

            return new Literal(id, new LiteralBoolean(true));
        }
        if (match(NIL)) {
            int id = current; // set id to current id

            return new Literal(id, null);
        }
        if (match(NUMBER, STRING)) {
            int id = current; // set id to current id

            return new Literal(id, previous().getLiteral());
        }
        if (match(THIS)) {
            return new This(current, previous());
        }
        if (match(IDENTIFIER)) {
            int id = current; // set id to current id

            return new Variable(id, previous());
        }
        if (match(LEFT_PAREN)) {
            Expr expr = expression();
            consume(RIGHT_PAREN, "Expect ')' after expression.");

            int id = current; // set id to current id
            
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
        if (!isAtEnd()) { current++; }
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
        throw new ParseError(message);
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
                case PROTO:
                    Token name = consume(IDENTIFIER, "Expect property name after '.'.");
                    // Token name;
                    if (check(IDENTIFIER)) {
                        name = consume(IDENTIFIER, "Expect property name after '.'.");
                    } else if (check(PROTO)) {
                        name = consume(PROTO, "Expect proto after '.'.");
                    } else {
                        throw error(peek(), "Expect property or proto after dot.");
                    }
            }
            advance();
        }
    }
}
