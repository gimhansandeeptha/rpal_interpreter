import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

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

    private Token buildToken(String c){
        return null;
    }


}


