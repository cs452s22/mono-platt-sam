package edu.sou.cs452.jlox;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import edu.sou.cs452.jlox.generated.types.*;
import java.util.List;

@DgsComponent
public class RunDatafetcher { // TODO: complete this part

    @DgsQuery
    public String run(@InputArgument String code) {

        // create a scanner to scan the input
        Scanner sc = new Scanner(code);

        // get the list of tokens using the scanner
        List<Token> tokens = sc.scanTokens();

        // create a new parser that will parse the tokens
        Parser parser = new Parser(tokens);

        // create a list of statements from the parser
        List<Stmt> stmts = parser.parse();

        // create an interpreter
        Interpreter intr = new Interpreter();

        // interpret the statements
        intr.interpret(stmts);

        // return what was interpreted
        return intr.getOutputString();
    }
}