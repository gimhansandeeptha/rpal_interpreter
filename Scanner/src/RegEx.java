import java.util.regex.Pattern;

public class RegEx {
    private static final String Letter = "a-zA-Z";
    private static final String Digit = "\\d";
    private static final String Operator_symbol= "+-*<>&.@/:=~|$!#%^_[]{}\\\"'?";
    private static final String LeftSqBracket = "[";
    private static final String RightSqBracket = "]"; 
    private static final String Comment = "\"();,\\\\s\\t";  


    public static final Pattern LetterRegex = Pattern.compile(LeftSqBracket+Letter+RightSqBracket);
    public static final Pattern DigitRegex = Pattern.compile(Digit);
    public static final Pattern Identifier = Pattern.compile(LeftSqBracket+Letter+Digit+RightSqBracket);
    public static final Pattern Spaces = Pattern.compile(LeftSqBracket+"\\s\\t\\n"+RightSqBracket);
    public static final Pattern CommentRegex = Pattern.compile(LeftSqBracket+Comment+Letter+Digit+Operator_symbol+RightSqBracket);
    public static final Pattern Punction = Pattern.compile(LeftSqBracket+"();,"+RightSqBracket);
    public static final Pattern Operator = Pattern.compile(Operator_symbol);
}
