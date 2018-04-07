
package ast;

public class If extends Statement {
    public Expression condition = null;
    public Statement decision = null;
    public Statement alternative = null;

    public If( Expression co, Statement de )
    {
        condition = co;
        decision = de;
    }
}
