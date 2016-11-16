
package ast;

import gnu.bytecode.Access;
import gnu.bytecode.ClassType;
import gnu.bytecode.CodeAttr;
import gnu.bytecode.Method;
import gnu.bytecode.Type;
import java.util.ArrayList;

import java.util.List;

public class Function {
    public String name = null;
    public List<Variable> parameters = null;
    public char rtype = 'V';
    public Node body = null;
    
    //
    public Function( String nm, List<Variable> prs )
    {
        name = nm;
        parameters = new ArrayList<>();
        parameters.addAll(prs);
    }

    //    
    public void compile( ClassType clo )
    {
        int parc = parameters.size();
        
        Type[] partyp = new Type[parc];
        for( int i = 0; i < parc; ++i )
            partyp[i] = Type.doubleType;
        
        Type rty = rtype == 'R' ? Type.doubleType : Type.voidType;
        final int pust = Access.PUBLIC | Access.STATIC;
        Method subr = clo.addMethod(name, partyp, rty, pust);
        
        CodeAttr code = subr.startCode();
        for( int i = 0; i < parc; ++i ) {
            String pna = parameters.get(i).name;
            code.getArg(i).setName(pna);
        }
        
        code.pushScope();
        body.compile(code);
        code.popScope();
    }
}
