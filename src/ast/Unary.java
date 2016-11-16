
package ast;

import gnu.bytecode.CodeAttr;
import parser.TypeError;

public class Unary extends Node {
    private String operation = null;
    private Node subexpr = null;

    public Unary( String op, Node se ) throws TypeError
    {
        operation = op;
        subexpr = se;
        type = 'R';
    }
 
    @Override
    public void compile( CodeAttr code )
    {
    }
}
