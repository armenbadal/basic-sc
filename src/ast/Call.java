
package ast;

import java.util.List;

public class Call extends Statement {
    public Apply subrcall = null;

    public Call( Subroutine subr, List<Expression> args )
    {
        subrcall = new Apply(subr, args);
    }
}
