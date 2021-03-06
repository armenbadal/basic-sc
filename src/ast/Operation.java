package ast;

public enum Operation {
    None, // անորոշ
    Add,  // գումարում
    Sub,  // հանում
    Mul,  // բազմապատկում
    Div,  // բաժանում
    Mod,  // ամբողջ բաժանում
    Pow,  // աստիճան
    Eq,   // հավասար է
    Ne,   // հավասար չէ
    Gt,   // մեծ է
    Ge,   // մեծ է կամ հավասար
    Lt,   // փոքր է
    Le,   // փոքր է կամ հավասար
    And,  // ԵՎ (կոնյունկցիա)
    Or,   // ԿԱՄ (դիզյունկցիա)
    Not,  // ՈՉ (ժխտում)
    Conc;  // տեքստերի կցում
}
