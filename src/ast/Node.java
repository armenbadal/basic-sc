
package ast;

import gnu.bytecode.CodeAttr;

public abstract class Node {
    // տողի համարը, որտեղ սկսվում է հանգույցը
    public int line = 0;
    
    // հանգույցի տիպը, ժառանգվող ատրիբուտ
    public char type = 'V';
    
    public abstract void compile( CodeAttr code );
}
