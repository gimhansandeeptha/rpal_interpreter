import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class Scanner {
    private BufferedReader buffer;
    private String extraChar;
    private int lineNumber;

    public Scanner(String inputFile) throws FileNotFoundException{
        buffer = new BufferedReader(new InputStreamReader(new FileInputStream(new File(inputFile))));
        extraChar =null;
        lineNumber =1;
    }

    public Token readNextToken() throws IOException{
        Token nextToken = null;
        String nextChar;
        
        if (extraChar==null){
            nextChar = readNextChar();
        }
        else{
            nextChar = extraChar;
        }
        if (nextChar != null){
            nextToken = buildToken(nextChar);
        }

        return nextToken;

    }

    private String readNextChar() throws IOException{
        int character;
        String c = null;
        if ((character = buffer.read()) != -1) {
            c = Character.toString((char) character);
            if (c.equals("\n")){lineNumber++ ;}
        }
        else{
            buffer.close();
        }
        return c;
    }

    private Token buildToken(String c) throws IOException{  // let func A = 
        Token nextToken = null;
        if (RegEx.LetterRegex.matcher(c).matches()) nextToken = identifierToken(c);
        else if (RegEx.DigitRegex.matcher(c).matches()) nextToken = integerToken(c);
        else if (RegEx.OperatorRegex.matcher(c).matches()) nextToken = operatorToken(c);
        else if (RegEx.SpacesRegex.matcher(c).matches()) nextToken = spaceToken(c);
        else if (RegEx.PunctionRegex.matcher(c).matches()) nextToken = punctionToken(c);
        else if (c.equals("\\")) nextToken = commentToken(c);
        else if (c.equals("\'")) nextToken = stringToken(c);

        return nextToken;
    }


    private Token identifierToken(String c) throws IOException{
        Token identifier = new Token();
        identifier.setTokenType(TokenType.IDENTIFIER);
        identifier.setTokenLine(lineNumber);
        StringBuilder stringBuilder = new StringBuilder(c);
        String nextChar = readNextChar();

        while(RegEx.IdentifierRegex.matcher(nextChar).matches()){
            stringBuilder.append(nextChar);
            nextChar = readNextChar();
        }
        extraChar=nextChar;
        identifier.setToken(stringBuilder.toString());

        return identifier;
    }

    private Token integerToken(String c) throws IOException{
        Token integer = new Token();
        integer.setTokenType(TokenType.INTEGER);
        integer.setTokenLine(lineNumber);
        StringBuilder stringBuilder = new StringBuilder(c);
        String nextChar = readNextChar();

        while(RegEx.DigitRegex.matcher(nextChar).matches()){
            stringBuilder.append(nextChar);
            nextChar = readNextChar();
        }
        extraChar = nextChar;
        integer.setToken(stringBuilder.toString());
        return integer;
    }

    private Token operatorToken(String c) throws IOException{
        Token operator = new Token();
        operator.setTokenType(TokenType.OPERATOR);
        operator.setTokenLine(lineNumber);
        StringBuilder stringBuilder = new StringBuilder(c);
        String nextChar = readNextChar();

        while(RegEx.OperatorRegex.matcher(nextChar).matches()){
            stringBuilder.append(nextChar);
            nextChar = readNextChar();
        }
        extraChar = nextChar;
        operator.setToken(stringBuilder.toString());
        return operator;
    }

    private Token stringToken(String c) throws IOException{
        Token string  = new Token();
        string.setTokenType(TokenType.STRING);
        string.setTokenLine(lineNumber);
        StringBuilder stringBuilder = new StringBuilder("");
        String nextChar = readNextChar();

        while(RegEx.StringRegex.matcher(nextChar).matches()){
            if (nextChar == "\'") {
                break;
            }
            stringBuilder.append(nextChar);
            nextChar = readNextChar();
        }
        // stringBuilder.append(nextChar);
        string.setToken(stringBuilder.toString());
        nextChar = readNextChar();
        extraChar= nextChar;
        return string;
    }

    private Token spaceToken(String c) throws IOException{
        Token space = new Token();
        space.setTokenType(TokenType.DELETE);
        space.setTokenLine(lineNumber);
        StringBuilder stringBuilder = new StringBuilder(c);
        String nextChar = readNextChar();

        while(RegEx.SpacesRegex.matcher(nextChar).matches()){
            stringBuilder.append(nextChar);
            nextChar = readNextChar();
        }
        extraChar = nextChar;
        space.setToken(stringBuilder.toString());
        return space;
    }

    private Token commentToken(String c) throws IOException{
        Token comment = new Token();
        comment.setTokenType(TokenType.DELETE);
        comment.setTokenLine(lineNumber);
        StringBuilder stringBuilder = new StringBuilder(c);
        String nextChar = readNextChar();

        while(RegEx.CommentRegex.matcher(nextChar).matches()){
            if (nextChar == "\n") break;
            stringBuilder.append(nextChar);
            nextChar = readNextChar();
        }
        comment.setToken(stringBuilder.toString());
        return comment;
    }

    private Token punctionToken(String c) throws IOException {
        Token punction = new Token();
        punction.setTokenLine(lineNumber);
        StringBuilder stringBuilder = new StringBuilder();
        String nextChar = c;

        while(RegEx.PunctionRegex.matcher(nextChar).matches()){
            if (nextChar.equals("(")) punction.setTokenType(TokenType.L_PAREN);
            else if (nextChar.equals(")")) punction.setTokenType(TokenType.R_PAREN);
            else if (nextChar.equals(";")) punction.setTokenType(TokenType.SEMICOLON);
            else if (nextChar.equals(",")) punction.setTokenType(TokenType.COMMA);
            stringBuilder.append(nextChar);
            nextChar = readNextChar();
        }
        extraChar = nextChar;
        punction.setToken(stringBuilder.toString());
        return punction;
    }


}


