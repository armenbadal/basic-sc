package parser;

import java.util.ArrayList;

public class SymbolTable {
    private class Symbol {
        public String name = null;
        public char type = 'R';
        
        public Symbol( String nm )
        {
            name = nm;
        }
    }
    
    private ArrayList<Symbol> scope = null;
    
    public SymbolTable()
    {
        scope = new ArrayList<>();
    }
    
    public void add( String vr )
    {
        //
    }
}
