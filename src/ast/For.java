
package ast;

import gnu.bytecode.CodeAttr;

public class For extends Node {
    public Variable param = null;
    public Node init = null;
    public Node bound = null;
    public Node step = null;
    public Node body = null;

    public For( Variable pr, Node ini, Node bou, Node sp, Node bo )
    {
        param = pr;
        init = ini;
        bound = bou;
        step = sp;
        body = bo;
    }
    
    @Override
    public void compile( CodeAttr code )
    {
        // param = eval(init)
        // spval = eval(step)
        // boval = eval(bound)
        // while( param <> boval )
        //    exec(body)
        //    param = param + spval;
    }
}
