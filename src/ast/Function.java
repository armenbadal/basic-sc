
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
    public char rtype = Node.Void;
    public List<Variable> params = null;
    public Node body = null;

    private List<Variable> locals = null;

    //
    public Function( String nm, List<Variable> prs )
    {
        name = nm;
        rtype = Node.Real;
        if( name.endsWith("$") ) {
            rtype = Node.Text;
            name = name.substring(0, name.length() - 1);
        }
        if( name.equals("Main") )
            rtype = Node.Void;

        params = prs;

        locals = new ArrayList<>();
    }

    //
    public void addLocal( Variable vr )
    {
        locals.add(vr);
    }

    //
    public boolean isParameter( Variable vr )
    {
        return params.contains(vr);
    }
    //
    public boolean isLocal( Variable vr )
    {
        return locals.contains(vr);
    }

    @Override
    public boolean equals( Object oj )
    {
        if (!(oj instanceof Function))
            return false;

        Function of = (Function) oj;
        if (!of.name.equals(name))
            return false;
        if (of.params.size() != params.size())
            return false;

        // TODO ստուգել նաև պարամետրերի անունները և տիպերը

        return true;
    }

    //    
    public void compile( ClassType clo )
    {
        int parc = params.size();

        // պարամետրերի տիպերը
        Type[] partyp = new Type[parc];
        for( int i = 0; i < parc; ++i ) {
            char tyn = params.get(i).type;
            if( tyn == Node.Real )
                partyp[i] = Type.doubleType;
            else if( tyn == Node.Text )
                partyp[i] = Type.javalangStringType;
        }

        // վերադարձվող արժեքի տիպը
        Type rty = Type.voidType;
        if( rtype == Node.Real )
            rty = Type.doubleType;
        else if( rtype == Node.Text )
            rty = Type.javalangStringType;
        else if( rtype == Node.Void )
            rty = Type.voidType;

        final int pust = Access.PUBLIC | Access.STATIC;
        Method subr = clo.addMethod(name, partyp, rty, pust);
        
        CodeAttr code = subr.startCode();
        for( int i = 0; i < parc; ++i ) {
            String pna = params.get(i).name;
            code.getArg(i).setName(pna);
        }

        for( Variable v : locals )
            if( v.type == Node.Real )
                code.addLocal(Type.doubleType, v.name);
            else if( v.type == Node.Text )
                code.addLocal(Type.javalangStringType, v.name);
        
        code.pushScope();
        body.compile(code);
        if( rtype != Node.Void )
            code.emitLoad(code.lookup("result$" + name));
        code.emitReturn();
        code.popScope();
    }
}
