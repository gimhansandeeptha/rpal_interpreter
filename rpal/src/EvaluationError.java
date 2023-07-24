public class EvaluationError{
  
  public static void printError(int sourceLineNumber, String message){
    System.out.println(P.fileName+":"+sourceLineNumber+": "+message);
    System.exit(1);
  }

}
