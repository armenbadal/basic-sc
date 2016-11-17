
package ast;

import gnu.bytecode.CodeAttr;

public class If extends Node {
    public Node condition = null;
    public Node decision = null;
    public Node alternative = null;

    public If( Node co, Node de )
    {
        condition = co;
        decision = de;
    }
    
    public void setElse( Node el )
    {
        alternative = el;
    }
    
    @Override
    public void compile( CodeAttr code )
    {
        condition.compile(code);
        code.emitPushInt(1);
        code.emitIfEq();
        decision.compile(code);
        if( alternative != null ) {
            code.emitElse();
            alternative.compile(code);
        }
        code.emitFi();
    }
}
