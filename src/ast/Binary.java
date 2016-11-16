
package ast;

import gnu.bytecode.CodeAttr;
import parser.TypeError;

public class Binary extends Node {
    private String operation = null;
    private Node expro = null;
    private Node expri = null;
    
    public Binary( String op, Node eo, Node ei ) throws TypeError
    {
        operation = op;
        expro = eo;
        expri = ei;

        // TODO move type check to parser
        if( expro.type == 'T' && expri.type == 'T' && operation.equals("&") )
            type = 'T';
        else if( expro.type == 'R' && expri.type == 'R' )
            type = 'R';
        else
            throw new TypeError("Տիպի սխալ։");
    }
    
    @Override
    public void compile( CodeAttr code )
    {}
}
