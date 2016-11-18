
package ast;

import gnu.bytecode.CodeAttr;

public class Real extends Node {
    public double value = 0D;
    
    public Real( double vl )
    {
        type = Node.Real;
        value = vl;
    }

    @Override
    public void compile( CodeAttr code ) 
    {
        code.emitPushDouble(value);
    }
}
