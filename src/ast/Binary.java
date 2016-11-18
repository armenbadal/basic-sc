
package ast;

import gnu.bytecode.CodeAttr;
import parser.TypeError;

public class Binary extends Node {
    public static final String[] arithmetic
            = { "+", "-", "*", "/", "^" };
    public static final String[] comparison
            = { "=", "<>", ">", "<=", ">", ">=" };
    public static final String[] logical
            = { "AND", "OR" };
    public static final String[] textual
            = { "&" };

    private String operation = null;
    private Node expro = null;
    private Node expri = null;
    
    public Binary( String op, Node eo, Node ei ) throws TypeError
    {
        operation = op;
        expro = eo;
        expri = ei;

//        if( expro.type == Node.Text && expri.type == Node.Text && operation.equals("&") )
//            type = Node.Text;
//        else if( expro.type == Node.Real && expri.type == Node.Real )
//            type = Node.Real;

        // տիպի դուրսբերում
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
    public void compile( CodeAttr code )
    {}
}
