package edu.sou.cs452.jlox;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import edu.sou.cs452.jlox.generated.types.*;
import edu.sou.cs452.jlox.generated.types.Class;
import java.util.List;

import static edu.sou.cs452.jlox.generated.types.TokenType.*;

@DgsComponent
public class Datafetcher {
    /*
    @DgsQuery
    public String run(@InputArgument String code) {

        // create a scanner to scan the input
        Scanner sc = new Scanner(code);

        // get the list of tokens using the scanner
        List<Token> tokens = sc.scanTokens();

        // prints out each token for debugging purposes
        // for (Token t : tokens) { System.out.println(t); }

        // create a new parser that will parse the tokens
        Parser parser = new Parser(tokens);

        // create a list of statements from the parser
        List<Stmt> stmts = parser.parse();

        // prints out each statement for debugging purposes
        // for (Stmt s : stmts) { System.out.println(s); }

        // create an interpreter
        Interpreter intr = new Interpreter();

        // interpret the statements
        intr.interpret(stmts);

        // return what was interpreted
        return intr.getOutputString();
    }
    */

    @DgsQuery
    public String sign(@InputArgument String code) {

        // create a scanner to scan the input
        Scanner sc = new Scanner(code);

        // get the list of tokens using the scanner
        List<Token> tokens = sc.scanTokens();

        // prints out each token for debugging purposes
        // for (Token t : tokens) { System.out.println(t); }

        // create a new parser that will parse the tokens
        Parser parser = new Parser(tokens);

        // create a list of statements from the parser
        List<Stmt> stmts = parser.parse();

        // prints out each statement for debugging purposes
        // for (Stmt s : stmts) { System.out.println(s); }

        // create an interpreter
        AbstractInterpreter abstrIntr = new AbstractInterpreter();

        // interpret the statements
        abstrIntr.interpret(stmts);

        System.out.println(abstrIntr.getOutputString());

        // return what was interpreted
        return abstrIntr.getOutputString();
    }
}