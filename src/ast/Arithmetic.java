package ast;

import gnu.bytecode.CodeAttr;
import gnu.bytecode.Type;

public class Arithmetic extends Binary {
    public Arithmetic( String op, Node eo, Node ei )
    {
        super(op, eo, ei);
    }

    @Override
    public void compile( CodeAttr code )
    {
        expro.compile(code);
        expri.compile(code);
        switch( operation ) {
            case "+":
                code.emitAdd(Type.doubleType);
                break;
            case "-":
                code.emitSub(Type.doubleType);
                break;
            case "*":
                code.emitMul();
                break;
            case "/":
                code.emitDiv();
                break;
            case "^":
                break;
        }
    }
}
