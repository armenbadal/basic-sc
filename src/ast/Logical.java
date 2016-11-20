package ast;

import gnu.bytecode.CodeAttr;

public class Logical extends Binary {
    public Logical( String op, Node eo, Node ei )
    {
        super(op, eo, ei);
    }

    @Override
    public void compile( CodeAttr code )
    {
        if( operation.equals("AND") ) {
            expro.compile(code);
            code.emitPushInt(0);
            code.emitIfEq();
            // continue
        }
        else if( operation.equals("OR") ) {
            //
        }
    }
}