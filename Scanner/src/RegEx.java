import java.util.regex.Pattern;

public class RegEx {
    // private static final String Letter = "a-zA-Z";
    // private static final String Digit = "\\d";
    // private static final String Operator_symbol= "\\+\\-\\*<>&.@/:=~|$!#%^_\\[\\]{}\\\"'?";
    // private static final String LeftSqBracket = "[";
    // private static final String RightSqBracket = "]"; 
    // private static final String Comment = "();,\\\\s\\t"; 
    // private static final String Punction = "();,";
    

    // public static final Pattern LetterRegex = Pattern.compile(LeftSqBracket+Letter+RightSqBracket);
    // public static final Pattern DigitRegex = Pattern.compile(Digit);
    // public static final Pattern IdentifierRegex = Pattern.compile(LeftSqBracket+Letter+Digit+"_"+RightSqBracket);
    // public static final Pattern SpacesRegex = Pattern.compile(LeftSqBracket+"\\s\\t\\n"+RightSqBracket);
    // public static final Pattern CommentRegex = Pattern.compile(LeftSqBracket+Comment+Letter+Digit+Operator_symbol+RightSqBracket);
    // public static final Pattern PunctionRegex = Pattern.compile(LeftSqBracket+"();,"+RightSqBracket);
    // public static final Pattern OperatorRegex = Pattern.compile(Operator_symbol);
    // public static final Pattern StringRegex = Pattern.compile(LeftSqBracket+"\\s\\t\\n\\\\"+Punction+Letter+Digit+Operator_symbol+RightSqBracket);
    


    private static final String letterRegexString = "a-zA-Z";
    private static final String digitRegexString = "\\d";
    private static final String spaceRegexString = "[\\s\\t\\n]";
    private static final String punctuationRegexString = "();,";
    private static final String opSymbolRegexString = "+-/~:=|!#%_{}\"*<>.&$^\\[\\]?@";
    private static final String opSymbolToEscapeString = "([*<>.&$^?])";
    
    public static final Pattern LetterRegex = Pattern.compile("["+letterRegexString+"]");
    
    public static final Pattern IdentifierRegex = Pattern.compile("["+letterRegexString+digitRegexString+"_]");
  
    public static final Pattern DigitRegex = Pattern.compile(digitRegexString);
  
    public static final Pattern PunctionRegex = Pattern.compile("["+punctuationRegexString+"]");
  
    public static final String opSymbolRegex = "[" + escapeMetaChars(opSymbolRegexString, opSymbolToEscapeString) + "]";
    public static final Pattern OperatorRegex = Pattern.compile(opSymbolRegex);
    
    public static final Pattern StringRegex = Pattern.compile("[ \\t\\n\\\\"+punctuationRegexString+letterRegexString+digitRegexString+escapeMetaChars(opSymbolRegexString, opSymbolToEscapeString) +"]");
    
    public static final Pattern SpacesRegex = Pattern.compile(spaceRegexString);
    
    public static final Pattern CommentRegex = Pattern.compile("[ \\t\\'\\\\ \\r"+punctuationRegexString+letterRegexString+digitRegexString+escapeMetaChars(opSymbolRegexString, opSymbolToEscapeString)+"]"); //the \\r is for Windows LF; not really required since we're targeting *nix systems
    
    private static String escapeMetaChars(String inputString, String charsToEscape){
      return inputString.replaceAll(charsToEscape,"\\\\\\\\$1");
    }
}
