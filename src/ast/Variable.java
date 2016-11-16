
package ast;

import gnu.bytecode.CodeAttr;


public class Variable extends Node {
    public String name = null;
    
    public Variable( String vn )
    {
        name = vn;
        if( name.endsWith("$") ) {
            type = 'T';
            name = name.substring(0, name.length() - 1);
        }
    }
    
    public Variable( String vn, Node ix )
    {
    }
        
    @Override
    public void compile( CodeAttr code )
    {        
    }
}
