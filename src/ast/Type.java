package ast;

public enum Type {
    Void,
    Real,
    Text;

    public static Type typeOf( String idn )
    {
        if( idn.endsWith("$") )
            return Text;

        return Real;
    }
}
