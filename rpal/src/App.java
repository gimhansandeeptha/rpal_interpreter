public class App {
    public static void main(String[] args) throws Exception {
        // Scanner scanner = new Scanner("D:\\Gimhan Sandeeptha\\Gimhan\\Semester 04\\Programming Languages\\PL Group Project - 12\\rpal_interpreter\\rpal\\test.txt");


        // Token nextToken = scanner.readNextToken();
        // while(nextToken != null){
        //     System.out.printf("%s : %s : %d\n",nextToken.getToken(),nextToken.getTokenType().toString(),nextToken.getTokenLine());
        //     nextToken = scanner.readNextToken();
        // }


        // Parser check
        Parser parser = new Parser();
        parser.buildAST();
        parser.printAST();

        
    }
}