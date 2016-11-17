
package ast;

import gnu.bytecode.*;
import gnu.bytecode.Variable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Program {
    public String name = null;
    public List<Function> subroutines = null;

    public boolean hasInput = false;

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
        // նախապատրաստել class֊ը
        ClassType classobj = new ClassType(name);
        classobj.setSuper("java.lang.Object");
        classobj.setModifiers(Access.PUBLIC);

        // եթե ներմուծման հրաման կա ֆունկցիաներից որևէ մեկում,
        // ապա նախատեսել ստատիկ scan օբյեկտը
        if( hasInput ) {
            int prista = Access.PRIVATE | Access.STATIC;
            Type scty = Type.getType("java.util.Scanner");
            classobj.addField("scan", scty, prista);
        }

        // գեներացնել ֆունկցիաները (մեթոդներ)
        for( Function su : subroutines )
            su.compile(classobj);

        // գեներացնել main մեթոդը
        Type[] epty = { Type.getType("java.lang.String[]") };
        int pubsta = Access.PUBLIC | Access.STATIC;
        Method enpo = classobj.addMethod("main", epty, Type.voidType, pubsta);
        CodeAttr code = enpo.startCode();

        // եթե ներմուծման հրաման կա, ապա ստեղծել ստատիկ scan օբյեկտը
        if( hasInput ) {
            ClassType sys = ClassType.make("java.lang.System");
            Field in = sys.getField("in");
            ClassType scer = ClassType.make("java.util.Scanner");
            Type[] scty = { in.getType() };
            Method scco = scer.getDeclaredMethod("<init>", scty);

            code.emitNew(scer);
            code.emitDup();
            code.emitGetStatic(in);
            code.emitInvokeSpecial(scco);
            Field sc = classobj.getDeclaredField("scan");
            code.emitPutStatic(sc);
        }

        // գրել ֆայլի մեջ
        classobj.writeToFile(name + ".class");
    }
}
