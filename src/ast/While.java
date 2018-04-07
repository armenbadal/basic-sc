
package ast;

public class While extends Statement {
    public Expression condition = null;
    public Statement body = null;

    public While( Expression co, Statement bo )
    {
        condition = co;
        body = bo;
    }
}
