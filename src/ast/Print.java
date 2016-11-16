
package ast;

import gnu.bytecode.CodeAttr;

public class Print extends Node {
    private Node expr = null;

    public Print( Node ex )
    {
        expr = ex;
    }
    
    @Override
    public void compile( CodeAttr code )
    {
        expr.compile(code);
        // TODO generate code for System.out.println
    }
}
