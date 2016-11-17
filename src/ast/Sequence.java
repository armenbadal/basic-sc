
package ast;

import gnu.bytecode.CodeAttr;

import java.util.ArrayList;
import java.util.List;

public class Sequence extends Node {
    public List<Node> statements = null;

    public Sequence()
    {
        statements = new ArrayList<>();
    }

    public void add( Node no )
    {
        statements.add(no);
    }
    
    @Override
    public void compile( CodeAttr code ) 
    {
        for( Node s : statements )
            s.compile(code);
    }
}
