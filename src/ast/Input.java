
package ast;

import gnu.bytecode.CodeAttr;

public class Input extends Node {
    private Variable vari = null;

    public Input( Variable vr )
    {
        vari = vr;
    }
    
    @Override
    public void compile( CodeAttr code )
    {}
}
