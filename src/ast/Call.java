
package ast;

import gnu.bytecode.ClassType;
import gnu.bytecode.CodeAttr;
import gnu.bytecode.Method;

import java.util.ArrayList;
import java.util.List;

public class Call extends Node {
    private String callee = null;
    private List<Node> argus = null;

    public Call( String fu, List<Node> ag )
    {
        callee = fu;
        argus = new ArrayList<>(ag);
    }
    
    @Override
    public void compile( CodeAttr code )
    {
        for( Node a : argus )
            a.compile(code);

        ClassType clo = code.getMethod().getDeclaringClass();
        Method meo = clo.getDeclaredStaticMethod(callee, argus.size());
        code.emitInvokeStatic(meo);
    }
}
