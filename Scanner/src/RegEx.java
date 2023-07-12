import java.util.regex.Pattern;

public class RegEx {
    private static final String Letter = "a-zA-Z";
    private static final String Digit = "\\d";
    private static final String Operator_symbol= "+-*<>&.@/:=~|$!#%^_[]{}\\\"'?";
    private static final String LeftSqBracket = "[";
    private static final String RightSqBracket = "]"; 
    private static final String Comment = "\"();,\\\\s\\t"; 
    private static final String Punction = "();,";


    public static final Pattern LetterRegex = Pattern.compile(LeftSqBracket+Letter+RightSqBracket);
    public static final Pattern DigitRegex = Pattern.compile(Digit);
    public static final Pattern IdentifierRegex = Pattern.compile(LeftSqBracket+Letter+Digit+RightSqBracket);
    public static final Pattern SpacesRegex = Pattern.compile(LeftSqBracket+"\\s\\t\\n"+RightSqBracket);
    public static final Pattern CommentRegex = Pattern.compile(LeftSqBracket+Comment+Letter+Digit+Operator_symbol+RightSqBracket);
    public static final Pattern PunctionRegex = Pattern.compile(LeftSqBracket+"();,"+RightSqBracket);
    public static final Pattern OperatorRegex = Pattern.compile(Operator_symbol);
    public static final Pattern StringRegex = Pattern.compile(LeftSqBracket+"\\s\\t\\n\\\\"+Punction+Letter+Digit+Operator_symbol+RightSqBracket);
    
}
