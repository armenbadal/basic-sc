package parser;

import java.util.Map;
import static java.util.Map.entry;

/**/
public class Scanner {
    private Map<String,Token> keywords = Map.ofEntries(
            entry("SUB", Token.Subroutine),
            entry("END", Token.End),
            entry("LET", Token.Let),
            entry("INPUT", Token.Input),
            entry("PRINT", Token.Print),
            entry("IF", Token.If),
            entry("THEN", Token.Then),
            entry("ELSEIF", Token.ElseIf),
            entry("ELSE", Token.Else),
            entry("FOR", Token.For),
            entry("TO", Token.To),
            entry("STEP", Token.Step),
            entry("WHILE", Token.While),
            entry("CALL", Token.Call),
            entry("MOD", Token.Mod),
            entry("AND", Token.And),
            entry("OR", Token.Or),
            entry("NOT", Token.Not)
    );

    private char[] source = null;
    private int position = 0;

    public int line = 1;

    //
    public Scanner( String text )
    {
        source = (text + "@").toCharArray();
    }

    //
    public Lexeme next()
    {
        char ch = source[position++];

        // անտեսել բացատները
        while( ch == ' ' || ch == '\t' || ch == '\r' )
            ch = source[position++];

        // հոսքի ավարտ
        if( position == source.length )
            return new Lexeme(Token.Eof, line);

        // մեկնաբանություն
        if( ch == '\'' ) {
            do {
                ch = source[position++];
            } while( ch != '\n' );
            --position;

            return next();
        }

        // ծառայողական բառեր և իդենտիֆիկատոր
        if( Character.isLetter(ch) )
            return keywordOrIdentifier();

        // թվային լիտերալ
        if( Character.isDigit(ch) )
            return numericLiteral();

        // տողային լիտերալ
        if( ch == '"' )
            return textLiteral();

        // մետասիմվոլներ կամ գործողություններ
        if( ch == '\n' )
            return new Lexeme(Token.NewLine, line++);

        // >, >=
        if( ch == '>' ) {
            ch = source[position++];
            if( ch == '=' )
                return new Lexeme(Token.Ge, ">=", line);
            else
                --position;
            return new Lexeme(Token.Gt, ">", line);
        }

        // <, <=, <>
        if( ch == '<' ) {
            ch = source[position++];
            if( ch == '=' )
                return new Lexeme(Token.Le, "<=", line);
            else if( ch == '>' )
                return new Lexeme(Token.Ne, "<>", line);
            else
                --position;
            return new Lexeme(Token.Lt, "<", line);
        }

        Token kind = Token.None;
        switch( ch ) {
            case '=':
                kind = Token.Eq;
                break;
            case '+':
                kind = Token.Add;
                break;
            case '-':
                kind = Token.Sub;
                break;
            case '*':
                kind = Token.Mul;
                break;
            case '/':
                kind = Token.Div;
                break;
            case '^':
                kind = Token.Pow;
                break;
            case '(':
                kind = Token.LeftPar;
                break;
            case ')':
                kind = Token.RightPar;
                break;
            case ',':
                kind = Token.Comma;
                break;
            case '&':
                kind = Token.Amp;
                break;
        }

        return new Lexeme(kind, String.valueOf(ch), line);
    }

    //
    private Lexeme keywordOrIdentifier()
    {
        int begin = position - 1;
        char ch = source[begin];
        while( Character.isLetterOrDigit(ch) )
            ch = source[position++];
        if( ch != '$' )
            --position;
        String vl = String.copyValueOf(source, begin, position - begin);
        Token kd = keywords.getOrDefault(vl, Token.Identifier);
        return new Lexeme(kd, vl, line);
    }

    //
    private Lexeme numericLiteral()
    {
        int begin = position - 1;
        char ch = source[begin];
        while( Character.isDigit(ch) )
            ch = source[position++];
        if( ch == '.' ) {
            ch = source[position++];
            while( Character.isDigit(ch) )
                ch = source[position++];
        }
        --position;
        String vl = String.copyValueOf(source, begin, position - begin);
        return new Lexeme(Token.Number, vl, line);
    }

    private Lexeme textLiteral()
    {
        int begin = position;
        char ch = source[begin];
        while( ch != '"' )
            ch = source[position++];
        String vl = String.copyValueOf(source, begin, position - begin);
        return new Lexeme(Token.Text, vl, line);
    }
}
