package ast;

import gnu.bytecode.CodeAttr;

public class Comparison extends Binary {
    public Comparison( String op, Node eo, Node ei )
    {
        super(op, eo, ei);
    }

    @Override
    public void compile( CodeAttr code )
    {
        expro.compile(code);
        expri.compile(code);
        switch( operation ) {
            case "=":
                code.emitIfEq();
                break;
            case "<>":
                code.emitIfNEq();
                break;
            case ">":
                code.emitIfGt();
                break;
            case ">=":
                code.emitIfGe();
                break;
            case "<":
                code.emitIfLt();
                break;
            case "<=":
                code.emitIfLe();
                break;
            default:
                return;
        }
        code.emitPushInt(1);
        code.emitElse();
        code.emitPushInt(0);
        code.emitFi();
    }
}