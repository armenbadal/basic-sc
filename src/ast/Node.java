
package ast;

import gnu.bytecode.CodeAttr;

public abstract class Node {
    // տողի համարը, որտեղ սկսվում է հանգույցը
    public int line = 0;

    public static final char Void = 'V';
    public static final char Real = 'R';
    public static final char Text = 'T';
    public static final char Boolean = 'B';

    // հանգույցի տիպը, ժառանգվող ատրիբուտ
    public char type = Void;
    
    public abstract void compile( CodeAttr code );
}
