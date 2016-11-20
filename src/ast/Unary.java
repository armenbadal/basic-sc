
package ast;

import gnu.bytecode.CodeAttr;
import gnu.bytecode.Type;
import parser.TypeError;

public class Unary extends Node {
    private String operation = null;
    private Node subexpr = null;

    public Unary( String op, Node se ) throws TypeError
    {
        operation = op;
        subexpr = se;

        if( operation.equals("NOT") )
            type = Node.Boolean;
        else if( operation.equals("-") )
            type = Node.Real;
    }
 
    @Override
    public void compile( CodeAttr code )
    {
        subexpr.compile(code);
        if( operation.equals("-") )
            code.put1(119); // dneg
        else if( operation.equals("NOT") )
            code.emitNot(Type.booleanType);
    }
}
