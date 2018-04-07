
package ast;

/**
 * Պարամետրով ցիկլ
 */
public class For extends Statement {
    public Variable param = null;
    public Expression init = null;
    public Expression bound = null;
    public Real step = null;
    public Statement body = null;

    public For( Variable pr, Expression ini, Expression bou, Real sp, Statement bo )
    {
        param = pr;
        init = ini;
        bound = bou;
        step = sp;
        body = bo;
    }
}
