
package ast;

import java.util.ArrayList;
import java.util.List;

public class Sequence extends Statement {
    public List<Statement> statements = null;

    public Sequence()
    {
        statements = new ArrayList<>();
    }
}
