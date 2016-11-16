
package parser;

public class TypeError extends Exception {
    public TypeError( String mes, Object... ags )
    {
        super(String.format(mes, ags));
    }
}
