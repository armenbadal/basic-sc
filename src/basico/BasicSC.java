
package basico;

import ast.Program;
import parser.ParseError;
import parser.Parser;
import parser.TypeError;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class BasicSC {

    private boolean compile( Path path )
    {
        try {
            Parser parser = new Parser(path);
            Program prog = parser.parse();
            prog.compile();
        }
        catch( Exception ex ) {
            System.err.println(ex.getMessage());
        }
        return true;
    }

    private static void runTests()
    {
        try {
            Path dir = Paths.get("./cases");
            for( Path nm : Files.newDirectoryStream(dir, "*.bas") ) {
                if( nm.endsWith("test01.bas") ) {
                    System.out.printf("~ ~ ~ ~ ~ ~ ~ %s ~ ~ ~ ~ ~ ~ ~\n", nm);
                    BasicSC basic = new BasicSC();
                    basic.compile(nm);
                }
            }
        }
        catch( IOException ex ) {
            System.err.println(ex.getMessage());
        }
    }

    public static void main( String[] args ) throws IOException, TypeError, ParseError
    {
        BasicSC basic = new BasicSC();
        basic.runTests();
    }
}
