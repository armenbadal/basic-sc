
package basico;

import ast.Program;
import generator.Lisper;
import parser.ParseError;
import parser.Parser;

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
            (new Lisper(System.out)).asLisp(prog);
        }
        catch( Exception ex ) {
            System.err.println(ex.getMessage());
        }
        return true;
    }

    private static void runTests( boolean all )
    {
        try {
            Path dir = Paths.get("./cases");
            for( Path nm : Files.newDirectoryStream(dir, "*.bas") ) {
                if( all || nm.endsWith("test00.bas") ) {
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

    public static void main( String[] args ) throws IOException, ParseError
    {
        BasicSC basic = new BasicSC();
        basic.runTests(false);
    }
}
