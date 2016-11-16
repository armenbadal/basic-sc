
package ast;

import gnu.bytecode.CodeAttr;

public class Unary extends Node {
    private String operation = null;
    private Node subexpr = null;

    public Unary( String op, Node se ) throws TypeError
    {
        operation = op;
        subexpr = se;
        
        if( subexpr.type != 'R' )
            throw new TypeError("Տիպի սխալ։");
        type = 'R';
    }
 
    @Override
    public void compile( CodeAttr code )
    {}
}
