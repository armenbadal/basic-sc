
package parser;

import ast.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import static java.util.Map.entry;

/**/
public class Parser {
    // բառային վերլուծիչը
    private Scanner scan = null;
    // ընտրության սիմվոլ
    private Lexeme lookahead = null;

    // վերլուծվող ֆայլի անունը
    private Path pathToFile = null;
    // վերլուծվող ծրագիր
    private Program program = null;
    // վերլուծվող ենթածրագրի հղումը
    private Subroutine current = null;

    // operations
    private Map<Token,Operation> opcodes = Map.ofEntries(
            entry(Token.Add, Operation.Add),
            entry(Token.Sub, Operation.Sub),
            entry(Token.Amp, Operation.Conc),
            entry(Token.Mul, Operation.Mul),
            entry(Token.Div, Operation.Div),
            entry(Token.Mod, Operation.Mod),
            entry(Token.Pow, Operation.Pow),
            entry(Token.Eq, Operation.Eq),
            entry(Token.Ne, Operation.Ne),
            entry(Token.Gt, Operation.Gt),
            entry(Token.Ge, Operation.Ge),
            entry(Token.Lt, Operation.Lt),
            entry(Token.Le, Operation.Le),
            entry(Token.And, Operation.And),
            entry(Token.Or, Operation.Or));

    //
    private Map<String,List<Apply>> unresolved = null;

    //
    public Parser( Path path ) throws IOException
    {
        pathToFile = path;

        StringBuilder texter = new StringBuilder();
        try( BufferedReader read = new BufferedReader(new FileReader(pathToFile.toFile())) ) {
            read.lines().forEach(e -> texter.append(e).append("\n"));
        }

        scan = new Scanner(texter.toString());
    }

    //
    public Program parse() throws ParseError
    {
        return parseProgram();
    }

    //
    private Program parseProgram() throws ParseError
    {
        program = new Program(pathToFile.toString());

        lookahead = scan.next();

        // բաց թողնել ֆայլի սկզբի դատարկ տողերը
        while( lookahead.is(Token.NewLine) )
            lookahead = scan.next();

        while( !lookahead.is(Token.Eof) ) {
            parseSubroutine();
            parseNewLines();
        }

        return program;
    }

    //
    private void parseSubroutine() throws ParseError
    {
        // վերլուծել վերնագիրը
        match(Token.Subroutine);
        String name = lookahead.value;
        match(Token.Identifier);

//        // ստուգել ֆունկցիայի՝ դեռևս սահմանված չլինելը
//        for( Subroutine si : subroutines )
//            if( si.name.equals(name) && si.body != null )
//                throw new ParseError(name + " անունով ֆունկցիան արդեն սահմանված է։");

        List<Variable> params = new ArrayList<>();
        if( lookahead.is(Token.LeftPar) ) {
            match(Token.LeftPar);
            if( lookahead.is(Token.Identifier) ) {
                String nm = match(Token.Identifier);
//                if( params.contains(nm) )
//                    throw new ParseError(varl + " անունն արդեն կա պարամետրերի ցուցակում։");
                params.add(new Variable(nm));
                while( lookahead.is(Token.Comma) ) {
                    match(Token.Comma);
                    nm = match(Token.Identifier);
                    params.add(new Variable(nm));
                }
            }
            match(Token.RightPar);
        }

        current = new Subroutine(name, params);
        program.subroutines.add(current);

//        // ենթածրագիր ցուցիչը
//        Optional<Subroutine> optsub = program.subroutines.stream()
//                .filter(e -> e.name.equals(name))
//                .findFirst();
//        if( optsub.isPresent() ) {
//            subr = optsub.get();
//            current = subr;
//        }
//        else
//            subroutines.add(subr);

        current.body = parseStatements();

        match(Token.End);
        match(Token.Subroutine);
    }

    //
    private Sequence parseStatements() throws ParseError
    {
        parseNewLines();

        Sequence sequ = new Sequence();
        while( true ) {
            Statement stat = null;
            int line = lookahead.line;
            if( lookahead.is(Token.Let) )
                stat = parseLet();
            else if( lookahead.is(Token.Input) )
                stat = parseInput();
            else if( lookahead.is(Token.Print) )
                stat = parsePrint();
            else if( lookahead.is(Token.If) )
                stat = parseIf();
            else if( lookahead.is(Token.While) )
                stat = parseWhile();
            else if( lookahead.is(Token.For) )
                stat = parseFor();
            else if( lookahead.is(Token.Call) )
                stat = parseCall();
            else
                break;
            stat.line = line;
            sequ.statements.add(stat);
            parseNewLines();
        }

        return sequ;
    }

    //
    private Let parseLet() throws ParseError
    {
        match(Token.Let);
        String varn = match(Token.Identifier);
        match(Token.Eq);
        Expression exl = parseExpression();

        Variable vr = getVariable(varn, false);

        if( varn.equals(current.name) )
            current.hasValue = true;

        return new Let(vr, exl);
    }

    //
    private Input parseInput() throws ParseError
    {
        match(Token.Input);
        String pr = "?";
        if( lookahead.is(Token.Text) ) {
            pr = match(Token.Text);
            match(Token.Comma);
        }
        String varn = match(Token.Identifier);

        Variable vr = getVariable(varn, false);

        return new Input(pr, vr);
    }

    //
    private Print parsePrint() throws ParseError
    {
        match(Token.Print);
        Expression exo = parseExpression();

        return new Print(exo);
    }

    //
    private If parseIf() throws ParseError
    {
        match(Token.If);
        Expression cond = parseExpression();
        match(Token.Then);
        Sequence thenp = parseStatements();
        If statbr = new If(cond, thenp);
        If bi = statbr;
        while( lookahead.is(Token.ElseIf) ) {
            match(Token.ElseIf);
            Expression coe = parseExpression();
            match(Token.Then);
            Sequence ste = parseStatements();
            If bre = new If(coe, ste);
            bi.alternative = bre;
            bi = bre;
        }
        if( lookahead.is(Token.Else) ) {
            match(Token.Else);
            bi.alternative = parseStatements();
        }
        match(Token.End);
        match(Token.If);

        return statbr;
    }

    //
    private For parseFor() throws ParseError
    {
        match(Token.For);

        Variable prn = new Variable(lookahead.value);
        String par = match(Token.Identifier);
        match(Token.Eq);
        Expression be = parseExpression();
        match(Token.To);
        Expression en = parseExpression();
        double spvl = 1.0;
        if( lookahead.is(Token.Step) ) {
            match(Token.Step);
            boolean neg = false;
            if( lookahead.is(Token.Sub) ) {
                match(Token.Sub);
                neg = true;
            }
            String num = match(Token.Number);
            spvl = Double.parseDouble(num);
            if( neg )
                spvl = -spvl;
        }
        Real sp = new Real(spvl);
        Variable vp = getVariable(par, false);
        Statement dy = parseStatements();
        match(Token.End);
        match(Token.For);

        return new For(vp, be, en, sp, dy);
    }

    //
    private While parseWhile() throws ParseError
    {
        match(Token.While);
        Expression cond = parseExpression();
        Statement body = parseStatements();
        match(Token.End);
        match(Token.While);

        return new While(cond, body);
    }

    //
    private Call parseCall() throws ParseError
    {
        match(Token.Call);
        String name = match(Token.Identifier);

        // արգումենտները
        ArrayList<Expression> args = new ArrayList<>();
        if( lookahead.is(Token.Number, Token.Text, Token.Identifier, Token.Sub, Token.Not, Token.LeftPar) ) {
            Expression exi = parseExpression();
            args.add(exi);
            while( lookahead.is(Token.Comma) ) {
                match(Token.Comma);
                exi = parseExpression();
                args.add(exi);
            }
        }

        Call caller = new Call(null, args);
        Subroutine callee = getSubroutine(name);

        if( null == callee )
            unresolved.get(name).add(caller.subrcall);

        caller.subrcall.callee = callee;

        return caller;
    }

    //
    private void parseNewLines() throws ParseError
    {
        match(Token.NewLine);
        while( lookahead.is(Token.NewLine) )
            lookahead = scan.next();
    }


    /*
    * Expression = Addition [('=' | '<>' | '>' | '>=' | '<' | '<=') Addition].
    */
    private Expression parseExpression() throws ParseError
    {
        Expression exo = parseAddition();
        if( lookahead.is(Token.Eq, Token.Ne, Token.Gt, Token.Ge, Token.Lt, Token.Le) ) {
            Token optok = lookahead.kind;
            match(lookahead.kind);
            Expression exi = parseAddition();
            exo = new Binary(opcodes.get(optok), exo, exi);
        }
        return exo;
    }

    /*
    * Addition = Multiplication {('+' | '-' | '&' | 'OR') Multiplication}.
    */
    private Expression parseAddition() throws ParseError
    {
        Expression exo = parseMultiplication();
        while( lookahead.is(Token.Add, Token.Sub, Token.Amp, Token.Or) ) {
            Token optok = lookahead.kind;
            match(lookahead.kind);
            Expression exi = parseMultiplication();
            exo = new Binary(opcodes.get(optok), exo, exi);
        }
        return exo;
    }

    /*
    * Multiplication = Power {('*' | '/' | '\' | 'AND') Power}.
    */
    private Expression parseMultiplication() throws ParseError
    {
        Expression exo = parsePower();
        while( lookahead.is(Token.Mul, Token.Div, Token.Mod, Token.And) ) {
            Token optok = lookahead.kind;
            match(lookahead.kind);
            Expression exi = parsePower();
            exo = new Binary(opcodes.get(optok), exo, exi);
        }
        return exo;
    }

    /*
    * Power = Factor ['^' Power].
    */
    private Expression parsePower() throws ParseError
    {
        Expression exo = parseFactor();
        if( lookahead.is(Token.Pow) ) {
            match(Token.Pow);
            Expression exi = parsePower();
            exo = new Binary(Operation.Pow, exo, exi);
        }
        return exo;
    }

    /**/
    private Expression parseFactor() throws ParseError
    {
        // NUMBER
        if( lookahead.is(Token.Number) ) {
            String lex = match(Token.Number);
            return new Real(Double.parseDouble(lex));
        }

        // TEXT
        if( lookahead.is(Token.Text) ) {
            String lex = match(Token.Text);
            return new Text(lex);
        }

        // ('-' | 'NOT') Factor
        if( lookahead.is(Token.Sub, Token.Not) ) {
            Operation opc = Operation.None;
            if( lookahead.is(Token.Sub) ) {
                opc = Operation.Sub;
                match(Token.Sub);
            }
            else if( lookahead.is(Token.Not) ) {
                opc = Operation.Not;
                match(Token.Not);
            }
            Expression exo = parseFactor();
            return new Unary(opc, exo);
        }

        // IDENT ['(' [ExpressionList] ')']
        if( lookahead.is(Token.Identifier) ) {
            String name = match(Token.Identifier);
            if( lookahead.is(Token.LeftPar) ) {
                ArrayList<Expression> args = new ArrayList<>();
                match(Token.LeftPar);
                Expression exo = parseExpression();
                args.add(exo);
                while( lookahead.is(Token.Comma) ) {
                    match(Token.Comma);
                    exo = parseExpression();
                    args.add(exo);
                }
                match(Token.RightPar);

                Apply applyer = new Apply(null, args);
                applyer.type = Type.typeOf(name);

                Subroutine callee = getSubroutine(name);
                if( null == callee )
                    unresolved.get(name).add(applyer);

                applyer.callee = callee;

                return applyer;
            }
            // ստուգել, որ name անունով փոփոխական սահմանված լինի
            return getVariable(name, true);
        }

        // '(' Expression ')'
        if( lookahead.is(Token.LeftPar) ) {
            match(Token.LeftPar);
            Expression exo = parseExpression();
            match(Token.RightPar);
            return exo;
        }

        throw new ParseError("Սպասվում է NUMBER, TEXT, '-', NOT, IDENT կամ '(', բայց հանդիպել է " + lookahead.value + "։");
    }

//    private String match( Token... exps ) throws ParseError
//    {
//        if( !lookahead.is(exps) )
//            throw new ParseError("Շարահյուսական սխալ։ %d տողում սպասվում էր %s, բայց հանդիպել է %s",
//                    lookahead.line, exps[0], lookahead.kind); // TODO: ուղղել
//
//        String valu = lookahead.value;
//        lookahead = scan.next();
//        return valu;
//    }

    /**/
    private String match( Token exp ) throws ParseError
    {
        if( !lookahead.is(exp) )
            throw new ParseError("Շարահյուսական սխալ։ %d տողում սպասվում էր %s, բայց հանդիպել է %s (%s)",
                    lookahead.line, exp, lookahead.kind, lookahead.value);

        String valu = lookahead.value;
        lookahead = scan.next();
        return valu;
    }

    /**/
    private Variable getVariable( String nm, boolean rval ) throws ParseError
    {
        if( rval && nm.equals(current.name) )
            throw new ParseError("Subroutine name is used as a variable.");

        for( Variable vi : current.locals )
            if( nm.equals(vi.name) )
                return vi;

        if( rval )
            throw new ParseError("Variable `" + nm + "` is not defined.");

        Variable varp = new Variable(nm);
        current.locals.add(varp);

        return varp;
    }

    /**/
    private Subroutine getSubroutine( String nm )
    {
        // որոնել տրված անունով ենթածրագիրը արդեն սահմանվածների մեջ
        for( Subroutine si : program.subroutines )
            if( nm.equals(si.name) )
                return si;

//        // որոնել
//        for( auto& bi : builtins )
//        if( std.get<0>(bi) == nm ) {
//            // հայտարարել ներդրված ենթածրագիր
//            auto sre = std.make_shared<Subroutine>(std.get<0>(bi), std.get<1>(bi));
//            sre->isBuiltIn = true;
//            sre->hasValue = std.get<2>(bi);
//            module->members.push_back(sre);
//            return sre;
//        }

        return null;
    }
}
