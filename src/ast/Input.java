
package ast;

import gnu.bytecode.*;

public class Input extends Node {
    private Variable vari = null;

    public Input( Variable vr )
    {
        vari = vr;
    }
    
    @Override
    public void compile( CodeAttr code )
    {
        ClassType clo = code.getMethod().getDeclaringClass();
        Field sc = clo.getDeclaredField("scan");
        code.emitGetStatic(sc);

        ClassType scer = ClassType.make("java.util.Scanner");
        if( vari.type == 'R' ) {
            Method nedo = scer.getDeclaredMethod("nextDouble", 0);
            code.emitInvokeVirtual(nedo);
        }
        else if( vari.type == 'T' ) {
            Method neli = scer.getDeclaredMethod("nextLine", 0);
            code.emitInvokeVirtual(neli);
        }

        gnu.bytecode.Variable vro = code.lookup(vari.name);
        code.emitStore(vro);
    }
}
