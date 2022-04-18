package edu.sou.cs452.jlox;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;

import java.util.List;
import java.util.stream.Collectors;
import edu.sou.cs452.jlox.generated.types.*;
import edu.sou.cs452.jlox.generated.types.Token;
import edu.sou.cs452.jlox.generated.types.TokenType;
import edu.sou.cs452.jlox.generated.types.LiteralValue;
import edu.sou.cs452.jlox.generated.types.LiteralString;
import edu.sou.cs452.jlox.generated.types.LiteralFloat;
import edu.sou.cs452.jlox.generated.types.LiteralBoolean;

import static edu.sou.cs452.jlox.generated.types.TokenType.*;

@DgsComponent
public class TokensDatafetcher {

    private final List<Token> tokens = List.of(
        /*
        new Show("Stranger Things", 2016),
        new Show("Ozark", 2017),
        new Show("The Crown", 2016),
        new Show("Dead to Me", 2019),
        new Show("Orange is the New Black", 2013)
        */
    );

    @DgsQuery
    public List<Token> tokens(@InputArgument String titleFilter) {
        if(titleFilter == null) {
            return tokens;
        }

        return tokens.stream().filter(s -> s.getTitle().contains(titleFilter)).collect(Collectors.toList());
    }
}
