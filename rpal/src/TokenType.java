/*
 * Type of a token is set by the Scanner
 */
public enum TokenType {
    IDENTIFIER, 
    INTEGER , 
    OPERATOR , 
    STRING , 
    DELETE , 
    L_PAREN , 
    R_PAREN , 
    SEMICOLON , 
    COMMA , 
    KEYWORD,//used to represent reverved keywords of RPAL language
    PUNCTION ;
}
