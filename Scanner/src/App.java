public class App {
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner("test.txt");
        Token nextToken = scanner.readNextToken();
        while(nextToken != null){
            System.out.printf("%s : %s\n",nextToken.getToken(),nextToken.getTokenType().toString());
            System.out.println("debug");
            nextToken = scanner.readNextToken();
        }
        
    }
}
