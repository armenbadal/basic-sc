
package ast;

import gnu.bytecode.CodeAttr;
import java.util.List;

public class BuiltIn extends Node {
    public BuiltIn( String fu, List<Node> ag )
    {}
    
    public static boolean is( String nm )
    {
        if( nm.equals("SQR") )
            return true;
        return false;
    }

    @Override
    public void compile( CodeAttr code )
    {}
}
