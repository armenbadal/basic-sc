
package ast;

public class Text extends Expression {
    public String value = null;
    
    public Text( String vl )
    {
        type = Type.Text;
        value = vl;
    }
}
