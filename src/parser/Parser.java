
package parser;

import ast.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**/
public class Parser {
    // բառային վերլուծիչը
    private Scanner scan = null;
    // ընտրության սիմվոլ
    private Lexeme lookahead = null;

    // վերլուծվող ֆայլի անունը
    private String fileName = null;
    // հայտարարված ու սահմանված ենթածրագրերը
    private List<Function> subroutines = null;
    // վերլուծվող ենթածրագրի հղումը
    private Function current = null;

    //
    public Parser( String name ) throws IOException
    {
        fileName = name;

        StringBuilder texter = new StringBuilder();
        try( BufferedReader read = new BufferedReader(new FileReader(fileName)) ) {
            read.lines().forEach(e -> texter.append(e).append("\n"));
        }
        texter.append('@');

        scan = new Scanner(texter.toString());
        lookahead = scan.next();
        // բաց թողնել ֆայլի սկզբի դատարկ տողերը
        while( lookahead.is(Token.NewLine) )
            lookahead = scan.next();
    }

    //
    public Program parse() throws ParseError, TypeError
    {
        subroutines = new ArrayList<>();

        while( lookahead.is(Token.Declare, Token.Function) ) {
            Function subri = null;
            if( lookahead.is(Token.Declare) )
                subri = parseDeclare();
            else if( lookahead.is(Token.Function) )
                subri = parseFunction();

            if( subri != null ) {
                if( !subroutines.contains(subri) )
                    subroutines.add(subri);
            }
        }

        String outname = fileName.replace(".bas", ".class");
        Program result = new Program(outname);
        subroutines.forEach(result::add);
        return result;
    }

    //
    private Function parseDeclare() throws ParseError, TypeError
    {
        match(Token.Declare);
        return parseFuncHeader();
    }

    //
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

        current = new Function(name, params);
        return current;
    }

    //
    private Function parseFunction() throws ParseError, TypeError
    {
        // վերլուծել վերնագիրը
        Function subr = parseFuncHeader();
        if( !subroutines.contains(subr) ) // ?
            subroutines.add(subr);        // ?

        // ենթածրագիր ցուցիչը
        final String name = subr.name;
        Optional<Function> optsub = subroutines.stream()
                .filter(e -> e.name.equals(name))
                .findFirst();
        if( optsub.isPresent() )
            subr = optsub.get();

        subr.body = parseNodeList();
        match(Token.End);
        match(Token.Function);
        parseNewLines();

        return subr;
    }

    //
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

    //
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

    //
    private Node parseLet() throws ParseError, TypeError
    {
        if( lookahead.is(Token.Let) )
            match(Token.Let);
        String varl = lookahead.value;
        match(Token.Identifier);
        match(Token.Eq);
        Node exl = parseDisjunction();

        Variable vr = new Variable(varl);
        current.addLocal(vr);

        if( vr.type != exl.type )
            throw new TypeError("Տիպի սխալ։");

        return new Let(vr, exl);
    }

    //
    private Node parseInput() throws ParseError, TypeError
    {
        match(Token.Input);
        String varn = lookahead.value;
        match(Token.Identifier);

        Variable vr = new Variable(varn);
        current.addLocal(vr);

        return new Input(vr);
    }

    //
    private Node parsePrint() throws ParseError, TypeError
    {
        match(Token.Print);
        Node exo = parseDisjunction();

        return new Print(exo);
    }

    //
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
        current.addLocal(prn);
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

    //
    private Node parseCallSub() throws ParseError, TypeError
    {
        match(Token.Call);
        String subnam = lookahead.value;
        match(Token.Identifier);

        // ստուգել ֆունկցիայի սահմանված կամ հայտարարված լինելը
        Optional<Function> optfunc = subroutines.stream()
                .filter(e -> e.name.equals(subnam))
                .findFirst();
        if( !optfunc.isPresent() )
            throw new ParseError("Կանչվող պրոցեդուրան սահմանված կամ հայտարարված չէ։");

        // արգումենտները
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

        Function func = optfunc.get();
        if( func.params.size() != argus.size() )
            throw new ParseError("%s ֆունկցիան սպասում է %d պարամետրեր։",
                    subnam, func.params.size());

        return new Call(func, argus);
    }

    //
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
            if( exo.type != Node.Real || exi.type != Node.Real )
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
            if( exo.type != Node.Real || exi.type != Node.Real )
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
            if( exo.type != Node.Real || exi.type != Node.Real )
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
            if( exo.type != Node.Real || exi.type != Node.Real )
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
                Optional<Function> optfunc = subroutines.stream()
                        .filter(e -> e.name.equals(varnam))
                        .findFirst();
                if( !optfunc.isPresent() )
                    new ParseError("Կանչված ֆունցկիան սահմանված կամ հայտարարված չէ։");
                Function func = optfunc.get();
                // համեմատել ֆունկցիայի պարամետրերի և փոխանցված արգումենտների քանակը
                if( func.params.size() != argus.size() )
                    throw new ParseError("%s ֆունկցիան սպասում է %d պարամետրեր։",
                            varnam, func.params.size());
                result = new Apply(func.name, argus);
            }
            else {
                Variable var = new Variable(varnam);
                if( !current.isParameter(var) || !current.isLocal(var) )
                    throw new ParseError("Օգտագործվող փոփոխականն արժեքավորված չէ։");
                return var;
            }
        }
        else if( lookahead.is(Token.Sub, Token.Not) ) {
            String oper = lookahead.value;
            lookahead = scan.next();
            Node subex = parseFactor();
            if( oper.equals("NOT") && subex.type != Node.Boolean )
                throw new TypeError("Տիպի սխալ։");
            if( oper.equals("-") && subex.type != Node.Real )
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
