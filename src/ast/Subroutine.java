
package ast;

import java.util.List;
import java.util.ArrayList;

public class Subroutine extends Node {
    public String name = null;
    public List<Variable> params = null;
    public Statement body = null;

    public boolean isBuiltIn = false;
    public boolean hasValue = false;
    public List<Variable> locals = null;

    //
    public Subroutine( String nm, List<Variable> prs )
    {
        name = nm;
        params = prs;

        locals = new ArrayList<>();
    }
}
