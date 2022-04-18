package edu.sou.cs452.jlox;

import java.util.List;

import edu.sou.cs452.jlox.generated.types.*; // Token

public class Parser {
    private final List<Token> tokens;
    private int current = 0;

    Parser(List<Token> tokens) {
        this.tokens = tokens;
    }
}
