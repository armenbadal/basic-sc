
package ast;

public class Print extends Statement {
    public Expression expr = null;

    public Print( Expression ex )
    {
        expr = ex;
    }
}
