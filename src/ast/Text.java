
package ast;

import gnu.bytecode.CodeAttr;

public class Text extends Node {
    public String value = null;
    
    public Text( String vl )
    {
        type = Node.Text;
        value = vl;
    }

    @Override
    public void compile( CodeAttr code )
    {
        code.emitPushString(value);
    }
}
