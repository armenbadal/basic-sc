
package ast;

public class Unary extends Expression {
    public Operation operation = Operation.None;
    public Expression subexpr = null;

    public Unary( Operation op, Expression se )
    {
        operation = op;
        subexpr = se;
    }
}
