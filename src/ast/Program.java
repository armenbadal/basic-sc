
package ast;

import java.util.List;
import java.util.ArrayList;

public class Program extends Node {
    public String name = null;
    public List<Subroutine> subroutines = null;

    public Program( String nm )
    {
        name = nm;
        subroutines = new ArrayList<>();
    }
}
