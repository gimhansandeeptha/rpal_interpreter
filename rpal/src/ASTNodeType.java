public enum ASTNodeType {

    IDENTIFIER("<ID:%s>"),
    STRING("<STR:%s>"),
    INTEGER("<STR:%s>"),

    LET("let"),
    LAMBDA("lambda"),
    WHERE("where"),
    TAU("tau"),
    AUG("aug"),
    CONDITIONAL("->"),

    OR("or"),
    BOOLAND("&"),
    NOT("not"),
    GR("gr"),
    GE("ge"),
    LS("ls"),
    LE("le"),
    EQ("eq"),
    NE("ne"),

    //Arithmatic expressions 
    PLUS("+"),
    MINUS("-"),
    NEG("neg"),
    MUL("*"),
    DEV("/"),
    EXP("**"),
    AT("@"),

    //Operators and operands 
    GAMMA("gamma"),
    TRUE("<true>"),
    FALSE("<false>"),
    NIL("<nil>"),
    DUMMY("<dummy>"),

    //Definitions 
    WITHIN("whitin"),
    AND("and"),
    REC("rec"),
    EQUAL("="),
    FCNFORM("function_form"),

    //Variables 
    PARAN("()"),
    COMMA(","),

    Y("Y"),

    BETA(""),
    DELTA(""),
    ETA(""),
    TUPLE("");

    private String name;

    private ASTNodeType(String name){
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

}
