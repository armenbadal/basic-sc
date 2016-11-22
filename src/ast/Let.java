
package ast;

import gnu.bytecode.CodeAttr;
import parser.TypeError;


public class Let extends Node {
    private Variable vari = null;
    private Node valu = null;
    
    public Let( Variable vr, Node ex ) throws TypeError
    {
        vari = vr;
        valu = ex;

        // TODO move type check to parser
        if( vari.type != valu.type )
            throw new TypeError("Տիպի սխալ։");
    }
    
    @Override
    public void compile( CodeAttr code )
    {
        valu.compile(code);
        gnu.bytecode.Variable v0 = code.lookup(vari.name);
        code.emitStore(v0);
    }
}
