
package ast;

import java.util.ArrayList;
import java.util.List;

public class Apply extends Expression {
    public Subroutine callee = null;
    public List<Expression> arguments = null;

    public Apply( Subroutine subr, List<Expression> args )
    {
        callee = subr;
        arguments = new ArrayList<>(args);
    }
}
