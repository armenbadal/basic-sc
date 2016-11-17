
package ast;

import gnu.bytecode.ClassType;
import gnu.bytecode.CodeAttr;
import gnu.bytecode.Method;

import java.util.ArrayList;
import java.util.List;

public class BuiltIn extends Node {
    private String func = null;
    private List<Node> argus = null;

    public BuiltIn( String fu, List<Node> ag )
    {
        func = fu;
        argus = new ArrayList<>(ag);
    }
    
    public static boolean is( String nm )
    {
        if( nm.equals("SQR") )
            return true;

        return false;
    }

    @Override
    public void compile( CodeAttr code )
    {
        ClassType math = ClassType.make("java.lang.Math");

        for( Node a : argus )
            a.compile(code);

        if( func.equals("SQR") ) {
            Method sqr = math.getDeclaredStaticMethod("sqrt", 1);
            code.emitInvokeStatic(sqr);
        }
    }
}
