
package ast;

public class Binary extends Expression {
    public Operation operation = Operation.None;
    public Expression expro = null;
    public Expression expri = null;
    
    public Binary( Operation op, Expression eo, Expression ei )
    {
        operation = op;
        expro = eo;
        expri = ei;
    }
}
