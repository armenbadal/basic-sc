
package ast;


public class Let extends Statement {
    public Variable vari = null;
    public Expression expr = null;
    
    public Let( Variable vr, Expression ex )
    {
        vari = vr;
        expr = ex;
    }
}
