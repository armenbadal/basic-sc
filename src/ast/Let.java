
package ast;

import gnu.bytecode.CodeAttr;


public class Let extends Node {
    private Variable vari = null;
    private Node valu = null;
    
    public Let( Variable vr, Node ex ) throws TypeError
    {
        vari = vr;
        valu = ex;
        
        if( vari.type != valu.type )
            throw new TypeError("Տիպի սխալ։");
    }
    
    @Override
    public void compile( CodeAttr code )
    {}
}
