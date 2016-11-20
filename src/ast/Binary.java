
package ast;

import gnu.bytecode.CodeAttr;
import gnu.bytecode.Type;
import parser.TypeError;

public abstract class Binary extends Node {
    public static final String[] arithmetic
            = { "+", "-", "*", "/", "^" };
    public static final String[] comparison
            = { "=", "<>", ">", ">=", "<", "<=" };
    public static final String[] logical
            = { "AND", "OR" };
    public static final String[] textual
            = { "&" };

    protected String operation = null;
    protected  Node expro = null;
    protected Node expri = null;
    
    public Binary( String op, Node eo, Node ei )
    {
        operation = op;
        expro = eo;
        expri = ei;

        // տիպի որոշում
        if( isIn(operation, arithmetic) )
            type = Node.Real;
        else if( isIn(operation, comparison) )
            type = Node.Boolean;
        else if( isIn(operation, logical) )
            type = Node.Boolean;
        else if( operation.equals("&") )
            type = Node.Text;
    }

    private static boolean isIn( String op, String[] set )
    {
        for( String e : set )
            if( op.equals(e) )
                return true;
        return false;
    }

    @Override
    public abstract void compile( CodeAttr code );
}
