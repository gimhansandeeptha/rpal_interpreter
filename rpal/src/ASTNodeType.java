
public enum ASTNodeType {

    IDENTIFIER,
    STRING,
    INTEGER,

    LET,
    LAMBDA,
    WHERE,
    TAU,
    AUG,
    CONDITIONAL,

    OR,
    BOOLAND,
    NOT,
    GR,
    GE,
    LS,
    LE,
    EQ,
    NE,

    //Arithmatic expressions 
    PLUS,
    MINUS,
    NEG,
    MUL,
    DEV,
    EXP,
    AT,

    //Operators and operands 
    GAMMA,
    TRUE,
    FALSE,
    NIL,
    DUMMY,

    //Definitions 
    WITHIN,
    AND,
    REC,
    EQUAL,
    FCNFORM,

    //Variables 
    PARAN,
    COMMA,

    Y,

    BETA,
    DELTA,
    ETA,
    TUPLE;

}
