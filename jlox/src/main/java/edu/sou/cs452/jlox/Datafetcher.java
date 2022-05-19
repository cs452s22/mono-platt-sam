package edu.sou.cs452.jlox;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import edu.sou.cs452.jlox.generated.types.*;
import java.util.List;

import static edu.sou.cs452.jlox.generated.types.TokenType.*;

@DgsComponent
public class Datafetcher {
    @DgsQuery
    public String run(@InputArgument String code, String input) {
        if (code.contains("getC()")) {
            int getCStartIndex = code.indexOf("getC");
            // System.out.println(code.substring(0, getCStartIndex));
            // System.out.println(code.substring(getCStartIndex));
            String newString = code.substring(0, getCStartIndex+5) + input + code.substring(getCStartIndex+5);
            code = newString;
        }

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
        
        // System.out.println(intr.getOutputString());

        // return what was interpreted
        return intr.getOutputString();
    }

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

        return null;
    }
}