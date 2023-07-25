/*
 * Sequence of tokens are passed from scanner to parser
 * A token consists of type,value and line number
 */

public class Token {
    private TokenType tokenType;
    private String token;
    private int tokenLine;

    public TokenType getTokenType() {
        return tokenType;
    }
    public void setTokenType(TokenType tokenType) {
        this.tokenType = tokenType;
    }
    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }
    public int getTokenLine() {
        return tokenLine;
    }
    public void setTokenLine(int tokenLine) {
        this.tokenLine = tokenLine;
    }
}
