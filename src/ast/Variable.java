
package ast;

import gnu.bytecode.CodeAttr;


public class Variable extends Node {
    public String name = null;
    
    public Variable( String vn )
    {
        type = Node.Real;
        name = vn;
        if( name.endsWith("$") ) {
            type = Node.Text;
            name = name.substring(0, name.length() - 1);
        }
    }

    @Override
    public boolean equals( Object oj )
    {
        if( oj instanceof Variable ) {
            Variable other = (Variable)oj;
            return name.equals(other.name) && type == other.type;
        }
        return false;
    }

    @Override
    public void compile( CodeAttr code )
    {
        code.emitLoad(code.lookup(name));
    }
}
