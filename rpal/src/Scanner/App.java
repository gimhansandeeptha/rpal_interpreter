package Scanner;
public class App {
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner("test.txt");
        Token nextToken = scanner.readNextToken();
        while(nextToken != null){
            System.out.printf("%s : %s : %d\n",nextToken.getToken(),nextToken.getTokenType().toString(),nextToken.getTokenLine());
            nextToken = scanner.readNextToken();
        }
        
    }
}
