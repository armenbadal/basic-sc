
package parser;

/**/
public enum Token {
    None, // ոչինչ

    Number,     // թվային հաստատուն
    Text,       // տեքստային հաստատուն
    Identifier, // իդենտիֆիկատոր

    Subroutine, // SUB
    Input,      // INPUT
    Print,      // PRINT
    Let,        // LET
    If,         // IF
    Then,       // THEN
    ElseIf,     // ELSEIF
    Else,       // ELSE
    While,      // WHILE
    For,        // FOR
    To,         // TO
    Step,       // STEP
    Call,       // CALL
    End,        // END

    NewLine, // նոր տողի նիշ

    Eq, // =
    Ne, // <>
    Lt, // <
    Le, // <=
    Gt, // >
    Ge, // >=

    LeftPar,  // (
    RightPar, // )
    Comma,    // ,

    Add, // +
    Sub, // -
    Amp, // &
    Or,  // OR
    Mul, // *
    Div, // /
    Mod, // MOD
    And, // AND
    Pow, // ^
    Not, // NOT

    Eof // ֆայլի վերջը
}
