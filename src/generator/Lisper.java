
package generator;

import ast.*;

import java.io.PrintStream;
import java.util.Map;
import static java.util.Map.entry;

public class Lisper extends Visitor {
    ///
    private Map<Operation,String> mnemonic = Map.ofEntries(
            entry(Operation.None,  "?"),
            entry(Operation.Add,  "ADD"),
            entry(Operation.Sub,  "SUB"),
            entry(Operation.Mul,  "MUL"),
            entry(Operation.Div,  "DIV"),
            entry(Operation.Mod,  "MOD"),
            entry(Operation.Pow,  "POW"),
            entry(Operation.Eq,   "EQ"),
            entry(Operation.Ne,   "NE"),
            entry(Operation.Gt,   "GT"),
            entry(Operation.Ge,   "GE"),
            entry(Operation.Lt,   "LT"),
            entry(Operation.Le,   "LE"),
            entry(Operation.And,  "AND"),
            entry(Operation.Or,   "OR"),
            entry(Operation.Not,  "NOT"),
            entry(Operation.Conc, "CONC")
    );

    private PrintStream ooo = null;
    private int indent = 0;

    public Lisper( PrintStream os )
    {
        ooo = os;
    }

    ///
    public boolean asLisp( Node node )
    {
        visit(node);
        return true;
    }

    ///
    @Override
    protected void visit( Real node )
    {
        ooo.printf("(basic-number %s )", node.value);
    }

    ///
    @Override
    protected void visit( Text node )
    {
        ooo.printf("(basic-text \"%s\")", node.value);
    }

    ///
    @Override
    protected void visit( Variable node )
    {
        ooo.printf("(basic-variable \"%s\")", node.name);
    }

    ///
    @Override
    protected void visit( Unary node )
    {
        ooo.printf("(basic-unary \"%s\" ", mnemonic.get(node.operation));
        visit(node.subexpr);
        ooo.print(")");
    }

    ///
    @Override
    protected void visit( Binary node )
    {
        ooo.printf("(basic-binary \"%s\"", mnemonic.get(node.operation));
        ++indent;
        visit(node.expro);
        visit(node.expri);
        ooo.print(")");
        --indent;
    }

    ///
    @Override
    protected void visit( Apply node )
    {
        ooo.printf("(basic-apply \"%s\"", node.callee.name);
        ++indent;
        for( Expression e : node.arguments )
            visit(e);
        ooo.print(")");
        --indent;
    }

    ///
    @Override
    protected void visit( Let node )
    {
        ooo.printf("(basic-let (basic-variable \"%s\"", node.vari.name);
        ++indent;
        visit(node.expr);
        ooo.print(")");
        --indent;
    }

    ///
    @Override
    protected void visit( Input node )
    {
        ooo.printf("(basic-input (basic-variable \"%s\") \"%s\")",
                node.prompt, node.vari.name);
    }

    ///
    @Override
    protected void visit( Print node )
    {
        ooo.print("(basic-print");
        ++indent;
        visit(node.expr);
        ooo.print(")");
        --indent;
    }

    ///
    @Override
    protected void visit( If node )
    {
        ooo.print("(basic-if");
        ++indent;
        visit(node.condition);
        visit(node.decision);
        if( null != node.alternative )
            visit(node.alternative);
        ooo.print(")");
        --indent;
    }

    ///
    @Override
    protected void visit( While node )
    {
        ooo.print("(basic-while");
        ++indent;
        visit(node.condition);
        visit(node.body);
        ooo.print(")");
        --indent;
    }

    ///
    @Override
    protected void visit( For node )
    {
        ooo.print("(basic-for");
        ++indent;
        visit(node.param);
        visit(node.init);
        visit(node.bound);
        visit(node.step);
        visit(node.body);
        ooo.print(")");
        --indent;
    }

    ///
    @Override
    protected void visit( Call node )
    {
        ooo.printf("(basic-call \"%s\"", node.subrcall.callee.name);
        ++indent;
        for( Expression e : node.subrcall.arguments )
            visit(e);
        ooo.print(")");
        --indent;
    }

    ///
    @Override
    protected void visit( Sequence node )
    {
        ooo.print("(basic-sequence");
        ++indent;
        for( Statement ei : node.statements )
            visit(ei);
        ooo.print(")");
        --indent;
    }

    ///
    @Override
    protected void visit( Subroutine node )
    {
        ooo.printf("(basic-subroutine \"%s\"", node.name);
        ++indent;
        String parlis = "";
        for( Variable ip : node.params ) {
            parlis.concat("\"");
            parlis.concat(ip.name);
            parlis.concat("\" ");
        }
//        if( 0 != parlis.length() )
//            parlis.pop_back();
        ooo.printf("\n%s'(%s)", spaces(), parlis);
        visit(node.body);
        ooo.print(")");
        --indent;
    }

    ///
    @Override
    protected void visit( Program node )
    {
        ooo.printf("(basic-program \"%s\"", node.name);
        ++indent;
        for( Subroutine si : node.subroutines )
            if( !si.isBuiltIn )
                visit((Node)si);
        --indent;
        ooo.println(")");
    }

    @Override
    protected void visit( Node node )
    {
        if( null != node ) {
            ooo.printf("\n" + spaces());
            super.visit(node);
        }
    }

    private String spaces()
    {
        return (new String(new char[2 * indent])).replace('\0', ' ');
    }
}
