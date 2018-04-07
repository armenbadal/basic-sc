package ast;

public abstract class Visitor {
    protected abstract void visit( Real node );
    protected abstract void visit( Text node );
    protected abstract void visit( Variable node );
    protected abstract void visit( Unary node );
    protected abstract void visit( Binary node );
    protected abstract void visit( Apply node );
    protected abstract void visit( Let node );
    protected abstract void visit( Input node );
    protected abstract void visit( Print node );
    protected abstract void visit( If node );
    protected abstract void visit( While node );
    protected abstract void visit( For node );
    protected abstract void visit( Call node );
    protected abstract void visit( Sequence node );
    protected abstract void visit( Subroutine node );
    protected abstract void visit( Program node );

    protected void visit( Node node )
    {
        if( node instanceof Real )
            visit((Real)node);
        else if( node instanceof Text )
            visit((Text)node);
        else if( node instanceof Variable )
            visit((Variable)node);
        else if( node instanceof Unary )
            visit((Unary)node);
        else if( node instanceof Binary )
            visit((Binary)node);
        else if( node instanceof Apply )
            visit((Apply)node);
        else if( node instanceof Let )
            visit((Let)node);
        else if( node instanceof Input )
            visit((Input)node);
        else if( node instanceof Print )
            visit((Print)node);
        else if( node instanceof If )
            visit((If)node);
        else if( node instanceof While )
            visit((While)node);
        else if( node instanceof For )
            visit((For)node);
        else if( node instanceof Call )
            visit((Call)node);
        else if( node instanceof Sequence )
            visit((Sequence) node);
        else if( node instanceof Subroutine )
            visit((Subroutine)node);
        else if( node instanceof Program )
            visit((Program)node);
    }
}
