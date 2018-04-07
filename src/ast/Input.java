
package ast;

public class Input extends Statement {
    public String prompt = null;
    public Variable vari = null;

    public Input( String pr, Variable vr )
    {
        prompt = pr;
        vari = vr;
    }
}
