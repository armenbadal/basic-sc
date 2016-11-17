
package ast;

import gnu.bytecode.CodeAttr;
import gnu.bytecode.Label;

public class While extends Node {
    public Node condition = null;
    public Node body = null;

    public While( Node co, Node bo )
    {
        condition = co;
        body = bo;
    }
    
    @Override
    public void compile( CodeAttr code )
    {
        Label wb = code.getLabel();
        Label we = code.getLabel();

        wb.define(code);
        condition.compile(code);
        code.emitPushInt(1);
        code.emitGotoIfNE(we);
        body.compile(code);
        code.emitGoto(wb);
        we.define(code);
    }
}
