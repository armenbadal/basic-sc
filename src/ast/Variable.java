
package ast;

public class Variable extends Expression {
    public String name = null;
    
    public Variable( String vn )
    {
        type = Type.Real;
        name = vn;
        if( name.endsWith("$") )
            type = Type.Text;
    }
}
