package edu.sou.cs452.jlox;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import edu.sou.cs452.jlox.generated.types.*;
import java.util.List;

import static edu.sou.cs452.jlox.generated.types.TokenType.*;

@DgsComponent
public class TokensDatafetcher {
    @DgsQuery
    public List<Token> tokens(@InputArgument String code) { // changed from titleFilter to code
        Scanner sc = new Scanner(code); // changed from titleFilter to code
        return sc.scanTokens(); // call new scanner and return the scanned tokens to be output to DGS in graphql
    }
}
