
package ast;

public class Real extends Expression {
    public double value = 0D;
    
    public Real( double vl )
    {
        type = Type.Real;
        value = vl;
    }
}
