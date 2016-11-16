
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
    public List<Variable> params = null;
    public char rtype = 'V';
    public Node body = null;

    public List<Variable> locals = null;

    //
    public Function( String nm, List<Variable> prs )
    {
        name = nm;
        params = new ArrayList<>();
        params.addAll(prs);

        locals = new ArrayList<>();
    }

    //    
    public void compile( ClassType clo )
    {
        int parc = params.size();

        // պարամետրերի տիպերը
        Type[] partyp = new Type[parc];
        for( int i = 0; i < parc; ++i ) {
            char tyn = params.get(i).type;
            if( tyn == 'R' )
                partyp[i] = Type.doubleType;
            else if( tyn == 'T' )
                partyp[i] = Type.javalangStringType;
        }

        // վերադարձվող արժեքի տիպը
        Type rty = Type.voidType;
        if( rtype == 'R' )
            rty = Type.doubleType;
        else if( rtype == 'T' )
            rty = Type.voidType;

        final int pust = Access.PUBLIC | Access.STATIC;
        Method subr = clo.addMethod(name, partyp, rty, pust);
        
        CodeAttr code = subr.startCode();
        for( int i = 0; i < parc; ++i ) {
            String pna = params.get(i).name;
            code.getArg(i).setName(pna);
        }
        
        code.pushScope();
        body.compile(code);
        code.popScope();
    }
}
