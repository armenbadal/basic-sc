
package ast;

import gnu.bytecode.Access;
import gnu.bytecode.ClassType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Program {
    public String name = null;
    public List<Function> subroutines = null;
    
    public Program( String nm )
    {
        name = nm;
        subroutines = new ArrayList<>();
    }
    
    public void add( Function sub )
    {
        subroutines.add(sub);
    }
    
    public void compile() throws IOException
    {
        ClassType classobj = new ClassType(name);
        classobj.setSuper("java.lang.Object");
        classobj.setModifiers(Access.PUBLIC);
        
        for( Function su : subroutines )
            su.compile(classobj);
        
        classobj.writeToFile(name + ".class");
    }
}
