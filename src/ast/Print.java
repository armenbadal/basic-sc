
package ast;

import gnu.bytecode.*;

public class Print extends Node {
    private Node expr = null;

    public Print( Node ex )
    {
        expr = ex;
    }
    
    @Override
    public void compile( CodeAttr code )
    {
        ClassType sys = ClassType.make("java.lang.System");
        Field out = sys.getField("out");
        code.emitGetStatic(out);

        expr.compile(code);

        ClassType prs = ClassType.make("java.io.PrintStream");
        Type[] aty = new Type[1];
        Method prim = null;
        if( expr.type == 'R' ) {
            aty[0] = Type.doubleType;
            prim = prs.getDeclaredMethod("println", aty);
        }
        else if( expr.type == 'T' ) {
            aty[0] = Type.javalangStringType;
            prim = prs.getDeclaredMethod("println", aty);
        }
        code.emitInvokeVirtual(prim);
    }
}
