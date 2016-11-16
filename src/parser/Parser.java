
package parser;

import ast.*;

import java.util.ArrayList;
import java.util.List;

/**/
public class Parser {
    private Scanner scan = null;
    private Lexeme lookahead = null;

    private List<Function> subroutines = null;
    private List<Variable> scopelocals = null;

    public Parser( String text )
    {
        scan = new Scanner(text + "@");
        lookahead = scan.next();
        // բաց թողնել ֆայլի սկզբի դատարկ տողերը
        while( lookahead.is(Token.NewLine) )
            lookahead = scan.next();
    }

    public List<Function> parse() throws ParseError, TypeError
    {
        subroutines = new ArrayList<>();

        while( lookahead.is(Token.Declare, Token.Function) ) {
            Function subri = null;
            if( lookahead.is(Token.Declare) )
                subri = parseDeclare();
            else if( lookahead.is(Token.Function) )
                subri = parseFunction();
            if( subri != null )
                addSubroutine(subri);
        }

        return subroutines;
    }

    private void addSubroutine( Function su )
    {
        if( !subroutines.contains(su) )
            subroutines.add(su);
    }

    private Function parseDeclare() throws ParseError, TypeError
    {
        match(Token.Declare);
        return parseFuncHeader();
    }

    private Function parseFuncHeader() throws ParseError, TypeError
    {
        match(Token.Function);
        String name = lookahead.value;
        match(Token.Identifier);

        // ստուգել ֆունկցիայի՝ դեռևս սահմանված չլինելը
        for( Function si : subroutines )
            if( si.name.equals(name) && si.body != null )
                throw new ParseError(name + " անունով ֆունկցիան արդեն սահմանված է։");

        match(Token.LeftParen);
        List<Variable> params = new ArrayList<>();
        if( lookahead.is(Token.Identifier) ) {
            Variable varl = new Variable(lookahead.value);
            match(Token.Identifier);
            if( params.contains(varl) )
                throw new ParseError(varl + " անունն արդեն կա պարամետրերի ցուցակում։");
            params.add(varl);
            while( lookahead.is(Token.Comma) ) {
                match(Token.Comma);
                varl = new Variable(lookahead.value);
                match(Token.Identifier);
                params.add(varl);
            }
        }
        match(Token.RightParen);
        parseNewLines();

        return new Function(name, params);
    }

    private Function parseFunction() throws ParseError, TypeError
    {
        Function subr = parseFuncHeader();
        addSubroutine(subr); // ?
        final String name = subr.name;
        subr = subroutines.stream()
                .filter(e -> e.name.equals(name))
                .findFirst()
                .get();
        scopelocals = new ArrayList<>(); // reset symbol table
        // TODO add function parameters to scopelocals (?)
        subr.body = parseNodeList();
        match(Token.End);
        match(Token.Function);
        parseNewLines();

        // TODO add content of symbol table to Function locals
        return subr;
    }

    private Node parseNodeList() throws ParseError, TypeError
    {
        Sequence seq = new Sequence();
        // FIRST(Node)
        while( lookahead.is(Token.Identifier, Token.Input, Token.Print,
                Token.If, Token.For, Token.While, Token.Call, Token.Let) ) {
            Node sti = parseNode();
            seq.add(sti);
        }

        return seq;
    }

    private Node parseNode() throws ParseError, TypeError
    {
        Node stat = null;
        switch( lookahead.kind ) {
            case Let:
            case Identifier:
                stat = parseLet();
                break;
            case Input:
                stat = parseInput();
                break;
            case Print:
                stat = parsePrint();
                break;
            case If:
                stat = parseConditional();
                break;
            case For:
                stat = parseForLoop();
                break;
            case While:
                stat = parseWhileLoop();
                break;
            case Call:
                stat = parseCallSub();
                break;
        }
        parseNewLines();

        return stat;
    }

    private Node parseLet() throws ParseError, TypeError
    {
        if( lookahead.is(Token.Let) )
            match(Token.Let);
        String varl = lookahead.value;
        match(Token.Identifier);
        match(Token.Eq);
        Node exl = parseDisjunction();

        Variable vr = new Variable(varl);
        // TODO նոր անուն ավելացնել current-ի locals-ում
        for( Variable v : scopelocals )
            if( v.name.equals(vr.name) && v.type == vr.type )
                break;
        // TODO ստուգել փոփոխականի տիպը
        return new Let(vr, exl);
    }

    private Node parseInput() throws ParseError, TypeError
    {
        match(Token.Input);
        String varn = lookahead.value;
        match(Token.Identifier);

        return new Input(new Variable(varn));
    }

    private Node parsePrint() throws ParseError, TypeError
    {
        match(Token.Print);
        Node exo = parseDisjunction();

        return new Print(exo);
    }

    private Node parseConditional() throws ParseError, TypeError
    {
        match(Token.If);
        Node cond = parseDisjunction();
        match(Token.Then);
        parseNewLines();
        Node thenp = parseNodeList();
        If statbr = new If(cond, thenp);
        If bi = statbr;
        while( lookahead.is(Token.ElseIf) ) {
            match(Token.ElseIf);
            Node coe = parseDisjunction();
            match(Token.Then);
            parseNewLines();
            Node ste = parseNodeList();
            If bre = new If(coe, ste);
            bi.setElse(bre);
            bi = bre;
        }
        if( lookahead.is(Token.Else) ) {
            match(Token.Else);
            parseNewLines();
            Node bre = parseNodeList();
            bi.setElse(bre);
        }
        match(Token.End);
        match(Token.If);

        return statbr;
    }

    private Node parseForLoop() throws ParseError, TypeError
    {
        match(Token.For);
        Variable prn = new Variable(lookahead.value);
        match(Token.Identifier);
        if( 'T' == prn.type )
            throw new ParseError("FOR ցիկլի պարամետրը պետք է լինի թվային։");
        match(Token.Eq);
        Node init = parseDisjunction();
        match(Token.To);
        Node lim = parseDisjunction();
        Node ste = null;
        if( lookahead.is(Token.Step) ) {
            match(Token.Step);
            ste = parseDisjunction();
        }
        parseNewLines();
        Node bdy = parseNodeList();
        match(Token.End);
        match(Token.For);

        return new For(prn, init, lim, ste, bdy);
    }

    private Node parseWhileLoop() throws ParseError, TypeError
    {
        match(Token.While);
        Node cond = parseDisjunction();
        parseNewLines();
        Node bdy = parseNodeList();
        match(Token.End);
        match(Token.While);

        return new While(cond, bdy);
    }

    private Node parseCallSub() throws ParseError, TypeError
    {
        match(Token.Call);
        String subnam = lookahead.value;
        match(Token.Identifier);
        // TODO ստուգել ֆունկցիայի սահմանված կամ հայտարարված լինելը
        ArrayList<Node> argus = new ArrayList<>();
        if( lookahead.is(Token.Number, Token.Identifier, Token.Sub, Token.Not, Token.LeftParen) ) {
            Node exi = parseDisjunction();
            argus.add(exi);
            while( lookahead.is(Token.Comma) ) {
                lookahead = scan.next();
                exi = parseDisjunction();
                argus.add(exi);
            }
        }

        Function func = subroutines.stream()
                .filter(e -> e.name.equals(subnam))
                .findFirst().get();

        if( func.params.size() != argus.size() )
            throw new ParseError("%s ֆունկցիան սպասում է %d պարամետրեր։",
                    subnam, func.params.size());

        return new Call(func, argus);
    }

    private void parseNewLines() throws ParseError, TypeError
    {
        match(Token.NewLine);
        while( lookahead.is(Token.NewLine) )
            lookahead = scan.next();
    }

    private Node parseDisjunction() throws ParseError, TypeError
    {
        Node exo = parseConjunction();
        while( lookahead.is(Token.Or) ) {
            lookahead = scan.next();
            Node exi = parseConjunction();
            return new Binary("OR", exo, exi);
        }
        return exo;
    }

    private Node parseConjunction() throws ParseError, TypeError
    {
        Node exo = parseEquality();
        while( lookahead.is(Token.And) ) {
            lookahead = scan.next();
            Node exi = parseEquality();
            return new Binary("AND", exo, exi);
        }
        return exo;
    }

    private Node parseEquality() throws ParseError, TypeError
    {
        Node exo = parseComparison();
        if( lookahead.is(Token.Eq, Token.Ne) ) {
            String oper = lookahead.value;
            lookahead = scan.next();
            Node exi = parseComparison();
            if( exo.type != exi.type )
                throw new TypeError("Տիպի սխալ։");
            exo = new Binary(oper, exo, exi);
        }
        return exo;
    }

    private Node parseComparison() throws ParseError, TypeError
    {
        Node exo = parseAddition();
        if( lookahead.is(Token.Gt, Token.Ge, Token.Lt, Token.Le) ) {
            String oper = lookahead.value;
            lookahead = scan.next();
            Node exi = parseAddition();
            if( exo.type != exi.type ) // TODO maybe allow for Real only
                throw new TypeError("Տիպի սխալ։");
            exo = new Binary(oper, exo, exi);
        }
        return exo;
    }

    private Node parseAddition() throws ParseError, TypeError
    {
        Node exo = parseMultiplication();
        while( lookahead.is(Token.Add, Token.Sub) ) {
            String oper = lookahead.value;
            lookahead = scan.next();
            Node exi = parseMultiplication();
            if( exo.type != 'R' || exi.type != 'R' )
                throw new TypeError("Տիպի սխալ։");
            exo = new Binary(oper, exo, exi);
        }
        return exo;
    }

    private Node parseMultiplication() throws ParseError, TypeError
    {
        Node exo = parsePower();
        while( lookahead.is(Token.Mul, Token.Div) ) {
            String oper = lookahead.value;
            lookahead = scan.next();
            Node exi = parsePower();
            if( exo.type != 'R' || exi.type != 'R' )
                throw new TypeError("Տիպի սխալ։");
            exo = new Binary(oper, exo, exi);
        }
        return exo;
    }

    private Node parsePower() throws ParseError, TypeError
    {
        Node exo = parseFactor();
        if( lookahead.is(Token.Power) ) {
            match(Token.Power);
            Node exi = parsePower();
            if( exo.type != 'R' || exi.type != 'R' )
                throw new TypeError("Տիպի սխալ։");
            exo = new Binary("^", exo, exi);
        }
        return exo;
    }

    private Node parseFactor() throws ParseError, TypeError
    {
        Node result = null;
        if( lookahead.is(Token.Number) ) {
            double numval = Double.valueOf(lookahead.value);
            lookahead = scan.next();
            result = new Real(numval);
        }
        else if( lookahead.is(Token.Text) ) {
            String textval = lookahead.value;
            lookahead = scan.next();
            result = new Text(textval);
        }
        else if( lookahead.is(Token.Identifier) ) {
            String varnam = lookahead.value;
            lookahead = scan.next();
            if( lookahead.is(Token.LeftParen) ) {
                ArrayList<Node> argus = new ArrayList<>();
                match(Token.LeftParen);
                // FIRST(Disjunction)
                if( lookahead.is(Token.Number, Token.Identifier, Token.Sub, Token.Not, Token.LeftParen) ) {
                    Node exi = parseDisjunction();
                    argus.add(exi);
                    while( lookahead.is(Token.Comma) ) {
                        lookahead = scan.next();
                        exi = parseDisjunction();
                        argus.add(exi);
                    }
                }
                match(Token.RightParen);
                // ստուգել ներդրված ֆունկցիա լինելը
                if( BuiltIn.is(varnam) )
                    return new BuiltIn(varnam, argus);
                // գտնել հայտարարված կամ սահմանված ֆունկցիան
                Function func = subroutines.stream()
                        .filter(e -> e.name.equals(varnam))
                        .findFirst().get();
                // համեմատել ֆունկցիայի պարամետրերի և փոխանցված արգումենտների քանակը
                if( func.params.size() != argus.size() )
                    throw new ParseError("%s ֆունկցիան սպասում է %d պարամետրեր։",
                            varnam, func.params.size());
                result = new Apply(func, argus);
            }
            else
                result = new Variable(varnam);
        }
        else if( lookahead.is(Token.Sub, Token.Not) ) {
            String oper = lookahead.value;
            lookahead = scan.next();
            Node subex = parseFactor();
            if( subex.type != 'R' )
                throw new TypeError("Տիպի սխալ։");
            result = new Unary(oper, subex);
        }
        else if( lookahead.is(Token.LeftParen) ) {
            match(Token.LeftParen);
            result = parseDisjunction();
            match(Token.RightParen);
        }

        return result;
    }

    private void match( Token exp ) throws ParseError
    {
        if( lookahead.is(exp) )
            lookahead = scan.next();
        else
            throw new ParseError("Շարահյուսական սխալ։ %d տողում սպասվում էր %s, բայց հանդիպել է %s",
                    lookahead.line, exp, lookahead.kind);
    }
}
