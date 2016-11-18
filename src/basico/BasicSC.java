
package basico;

import ast.Program;
import parser.ParseError;
import parser.Parser;
import parser.TypeError;

import java.io.IOException;

public class BasicSC {

    public static void main( String[] args ) throws IOException, TypeError, ParseError
    {
//        Program prog = new Program("P0");
//        prog.hasInput = true;
//        prog.compile();

        Parser parser = new Parser("");
        Program prog = parser.parse();
    }
}
