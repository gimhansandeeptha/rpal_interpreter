import java.io.IOException;
import java.util.Stack;

public class Parser {
    private Scanner s;
    private Token currentToken;
    Stack<ASTNode> stack;

    public ASTNode buildAST() throws IOException{
        s = new Scanner("D:\\Gimhan Sandeeptha\\Gimhan\\Semester 04\\Programming Languages\\PL Group Project - 12\\rpal_interpreter\\rpal\\test.txt");
        stack = new Stack<>();
        startParse();
        return stack.pop();
        // return new AST(stack.pop());

    }

    public void startParse() throws IOException{
        readNT();
        procE();
        if(currentToken != null){
            System.out.println("Exception");
        }
    }

    
    private void readNT() throws IOException{
        
        do{
            currentToken = s.readNextToken(); //read the next token
        }
        while(isCurrentTokenType(TokenType.DELETE));//ignore the token with type DELETE
        

        if (currentToken != null){

            if (currentToken.getTokenType() == TokenType.IDENTIFIER){
                createTerminalASTNode(ASTNodeType.IDENTIFIER, currentToken.getToken());
            }

            else if (currentToken.getTokenType() == TokenType.INTEGER){
                createTerminalASTNode(ASTNodeType.INTEGER, currentToken.getToken());
            }

            else if (currentToken.getTokenType() == TokenType.STRING){
                createTerminalASTNode(ASTNodeType.STRING, currentToken.getToken());
            }
        }
    }

    private boolean isCurrentToken(TokenType type, String value){

        if (currentToken == null){
            return false;
        }

        if (currentToken.getTokenType() != type || !currentToken.getToken().equals(value)){
            return false;
        }
        return true;
    }

    private boolean isCurrentTokenType(TokenType type){

        if (currentToken == null){
            return false;
        }

        if (currentToken.getTokenType() == type){
            return true;
        }
        return false;
    }


    
    private void buildASTNode(ASTNodeType nodeType, int n_children){
        ASTNode node = new ASTNode();
        node.setType(nodeType);
        
        while(n_children > 0){
            ASTNode new_child = stack.pop();
            if(node.getChild() != null){
                new_child.setSibling(node.getChild());
            }
            node.setChild(new_child);
            node.setSourceLine(new_child.getSourceLine());
            n_children = n_children-1;            
        }
        stack.push(node);
    }

    private void createTerminalASTNode(ASTNodeType type, String value){
        ASTNode node = new ASTNode();
        node.setType(type);
        node.setValue(value);
        node.setSourceLine(currentToken.getTokenLine());
        stack.push(node);
    }



    private void procE() throws IOException{
        if (isCurrentToken(TokenType.KEYWORD, "let")){// E => let D in E => let
            readNT();
            procD();

            if (!isCurrentToken(TokenType.KEYWORD, "in")){
                // throw new ParseException("E: expected 'in'");
                System.out.println("Parse Exception1");
            }
            readNT();
            procE();
            buildASTNode(ASTNodeType.LET, 2);
        }

        else if (isCurrentToken(TokenType.KEYWORD, "fn")){ //fn Vb+ .E => lambda
            int treesToPop = 0;

            readNT();

            while (isCurrentTokenType(TokenType.IDENTIFIER) || isCurrentTokenType(TokenType.L_PAREN)){
                procVB();
                treesToPop++;
            }

            if (treesToPop == 0){
                //throw new ParseException("E: At least one 'Vb' expected");
               System.out.println("Parse Exception2"); 
            }

            if (!isCurrentToken(TokenType.OPERATOR, ".")){
                //throw new ParseException("E: expected '.'");
                System.out.println("Parse Exception3");
            }
            readNT();
            procE();
            
            buildASTNode(ASTNodeType.LAMBDA, treesToPop+1);
        }
        
        else{  
            procEW(); //E => Ew
        }

    }

    private void procEW() throws IOException{
        procT();
        if(isCurrentToken(TokenType.KEYWORD, "where")){
            readNT();
            procDR();
            buildASTNode(ASTNodeType.WHERE, 2);
        }
    }

    private void procT() throws IOException{
        procTA();
        int treesToPop = 0;
        while(isCurrentToken(TokenType.COMMA, ",")){
            readNT();
            procTA();
            treesToPop = treesToPop+1;
        }
        if(treesToPop > 0){ 
            buildASTNode(ASTNodeType.TAU,treesToPop+1);
        }
    }

    

    public void procTA() throws IOException{
        procTC();
        while(isCurrentToken(TokenType.KEYWORD, "aug")){
            readNT();
            procTC();
            buildASTNode(ASTNodeType.AUG,2);
        }
    }
    
    public void procTC() throws IOException{
        procB();
        if (isCurrentToken(TokenType.OPERATOR, "->")){
            readNT();
            procTC();
            if(!isCurrentToken(TokenType.OPERATOR, "|")){
                // throw new ParseException("Tc:  expected");
                System.out.println("Parse Exception4");
            }
            readNT();
            procTC();
            buildASTNode(ASTNodeType.CONDITIONAL, 3);
            
        }
    }

    private void procB() throws IOException{
        
        procBT();
        
        while (isCurrentToken(TokenType.KEYWORD, "or")){
            readNT();
            procBT();
            buildASTNode(ASTNodeType.OR, 2);
        }
    }
    
    private void procBT() throws IOException{

        procBS();

        while (isCurrentToken(TokenType.OPERATOR, "&")){

            readNT();
            procBS();
            
            buildASTNode(ASTNodeType.BOOLAND, 2);
        }
    }

    private void procBS() throws IOException{
        if(isCurrentToken(TokenType.KEYWORD,"not")){
            readNT();
            procBP();
            buildASTNode(ASTNodeType.NOT,1);
        }
        else procBP();
    }
    
    private void procBP() throws IOException{
        procA();
        if(isCurrentToken(TokenType.KEYWORD,"gr") || isCurrentToken(TokenType.OPERATOR,">")){
            readNT();
            procA();
            buildASTNode(ASTNodeType.GR, 2);
        }
        else if(isCurrentToken(TokenType.KEYWORD,"ge") || isCurrentToken(TokenType.OPERATOR,">=")){
            readNT();
            procA();
            buildASTNode(ASTNodeType.GE, 2);
        }
        else if(isCurrentToken(TokenType.KEYWORD,"ls") || isCurrentToken(TokenType.OPERATOR,"<")){
            readNT();
            procA();
            buildASTNode(ASTNodeType.LS, 2);
        }
        else if(isCurrentToken(TokenType.KEYWORD,"le") || isCurrentToken(TokenType.OPERATOR,"<=")){
            readNT();
            procA();
            buildASTNode(ASTNodeType.LE, 2);
        }
        else if(isCurrentToken(TokenType.KEYWORD,"eq")){
            readNT();
            procA();
            buildASTNode(ASTNodeType.EQ, 2);
        }
        else if(isCurrentToken(TokenType.KEYWORD,"ne")){
            readNT();
            procA();
            buildASTNode(ASTNodeType.NE, 2);
        }
    }


    private void procA() throws IOException{

        if (isCurrentToken(TokenType.OPERATOR, "+")){
            readNT();
            procAT();
        }

        else if (isCurrentToken(TokenType.OPERATOR, "-")){
            readNT();
            procAT();
            buildASTNode(ASTNodeType.NEG, 1);
        }

        else{
            procAT();
        }
        
        boolean plus = true;
        
        while (isCurrentToken(TokenType.OPERATOR, "+") || isCurrentToken(TokenType.OPERATOR, "-")){
            
            if (currentToken.getToken().equals("+")){
                plus = true;
            }

            else if (currentToken.getToken().equals("-")){
                plus = false;
            }

            readNT();
            procAT();
            
            if (plus){
                buildASTNode(ASTNodeType.PLUS, 2);
            }

            else{
                buildASTNode(ASTNodeType.MINUS, 2);
            }
        }   
    }

    private void procAT() throws IOException{
        
        procAF();

        boolean mult = true;

        while (isCurrentToken(TokenType.OPERATOR, "*") || isCurrentToken(TokenType.OPERATOR, "/")){

            if (currentToken.getToken().equals("*")){
                mult = true;
            }

            if (currentToken.getToken().equals("/")){
                mult = false;
            }

            readNT();
            procAF();

            if (mult){
                buildASTNode(ASTNodeType.MUL, 2);
            }
            else {
                buildASTNode(ASTNodeType.DEV, 2);
            }
        }
    }

    private void procAF() throws IOException{
        procAP();
        if (isCurrentToken(TokenType.OPERATOR,"**")){
            readNT();
            procAF();
            buildASTNode(ASTNodeType.EXP,2);
        }
    }


    private void procAP() throws IOException{
        procR();
        while(isCurrentToken(TokenType.OPERATOR,"@")){
            readNT();
            if(!isCurrentTokenType(TokenType.IDENTIFIER)){
                // throw new ParseException();
                System.out.println("Parse Exception5");
            }
            readNT();
            procR();
            buildASTNode(ASTNodeType.AT,3);
        }
    }

    private void procR() throws IOException{
        procRN();
        readNT();
        while(isCurrentTokenType(TokenType.INTEGER)
                || isCurrentTokenType(TokenType.STRING)
                || isCurrentTokenType(TokenType.IDENTIFIER)
                || isCurrentToken(TokenType.KEYWORD, "true")
                || isCurrentToken(TokenType.KEYWORD, "false")
                || isCurrentToken(TokenType.KEYWORD, "nil")
                || isCurrentToken(TokenType.KEYWORD, "dummy")
                ||isCurrentTokenType(TokenType.L_PAREN)){
                    procRN();
                    buildASTNode(ASTNodeType.GAMMA,2);
                    readNT();        
        }
    }
    
    private void procRN() throws IOException{
        if(isCurrentTokenType(TokenType.IDENTIFIER)
            || isCurrentTokenType(TokenType.INTEGER)
            || isCurrentTokenType(TokenType.STRING)){}
        else if(isCurrentToken(TokenType.KEYWORD, "true")){
            createTerminalASTNode(ASTNodeType.TRUE, "true");
        }
        else if(isCurrentToken(TokenType.KEYWORD, "false")){
            createTerminalASTNode(ASTNodeType.TRUE, "false");
        }
        else if(isCurrentTokenType(TokenType.L_PAREN)){
            readNT();
            procE();
            if(! isCurrentTokenType(TokenType.R_PAREN)){
                // throw new ParseException("RN: ')' expected");
                System.out.println("Parse Exception6");
            }
        }
        else if(isCurrentToken(TokenType.KEYWORD, "dummy")){
            createTerminalASTNode(ASTNodeType.DUMMY, "dummy");
        }
    }

    

    
    private void procD() throws IOException{
        procDA();
        if (isCurrentToken(TokenType.KEYWORD,"within" )){
            readNT();
            procD();
            buildASTNode(ASTNodeType.WITHIN,2);
        }
        
    }

    private void procDA() throws IOException{
        procDR();
        int treesToPop = 0;
        while(isCurrentToken(TokenType.KEYWORD, "and")){
            readNT();
            procDR();
            treesToPop++;
        }
        if (treesToPop>0){
            buildASTNode(ASTNodeType.AND, treesToPop+1);
        }
    }

    private void procDR() throws IOException{

        if (isCurrentToken(TokenType.KEYWORD, "rec")){

            readNT();
            procDB();
            
            buildASTNode(ASTNodeType.REC, 1);
        }
        else{
            procDB();
        }
    }



    private void procDB() throws IOException{

        if (isCurrentTokenType(TokenType.L_PAREN)){
            procD();
            readNT();
        
            if (!isCurrentTokenType(TokenType.R_PAREN)){
                //throw new ParseException("DB: expected ')'");
                System.out.println("Parse Exception7");
            }
        
            readNT();
        }
        
        
        else if (isCurrentTokenType(TokenType.IDENTIFIER)){
            
            readNT();
            if (isCurrentToken(TokenType.OPERATOR, ",")){
                readNT();
                procVL();

                if (!isCurrentToken(TokenType.OPERATOR,"=")){
                    //throw new ParseException("DB expeted '='")
                    System.out.println("Parse Exception8");
                }
                buildASTNode(ASTNodeType.COMMA, 2);
                readNT();
                procE();
                buildASTNode(ASTNodeType.EQUAL, 2);
            }
            else{
                if (isCurrentToken(TokenType.OPERATOR, "=")){
                    readNT();
                    procE();
                    buildASTNode(ASTNodeType.EQ,2);
                }
                else {
                    int treesToPop = 0;
                    
                    while (isCurrentTokenType(TokenType.IDENTIFIER) || isCurrentTokenType(TokenType.L_PAREN)){
                        procVB();
                        treesToPop++;
                    }
                    if (treesToPop == 0){
                        //throw new ParseException("E: expected at least one Vb");
                        System.out.println("Parse Exception9");
                    }
                    if (!isCurrentToken(TokenType.OPERATOR, "=")){
                        // throw new ParseException("DB: expexted =");
                        System.out.println("Parse Exception10");
                    }
                    readNT();
                    procE();
                    buildASTNode(ASTNodeType.FCNFORM, treesToPop+2);
                }
            }
        }
    }
    


    private void procVB() throws IOException{
        if (isCurrentTokenType(TokenType.IDENTIFIER)){
            readNT();
        }
        else if(isCurrentTokenType(TokenType.L_PAREN)){
            readNT();
            if (isCurrentTokenType(TokenType.R_PAREN)){
                createTerminalASTNode(ASTNodeType.PARAN, "");
                readNT();
            }
            else{
                procVL();
                if(!isCurrentTokenType(TokenType.R_PAREN)){
                    // throw new ParseException("VB: ')' expected");
                    System.out.println("Parse Exception11");
                }
                readNT();
            }
        }
    }

    private void procVL() throws IOException{
        if(!isCurrentTokenType(TokenType.IDENTIFIER)){
            // throw new ParseException("VL: Identifier Expected");
            System.out.println("Parse Exception12");
        }
        else{
            readNT();
            int treesToPop = 0;
            while( isCurrentToken(TokenType.COMMA, ",")){
                readNT();
                if(!isCurrentTokenType(TokenType.IDENTIFIER)){
                    // throw new ParseException("VL: Identifier Expected");
                    System.out.println("Parse Exception13");
                }
                readNT();
                treesToPop=treesToPop+1;   
            }
            if(treesToPop>0){
            buildASTNode(ASTNodeType.COMMA,treesToPop+1);
        }
        }
    }

    // public void printAST(){
    //     printAST(stack.pop());
    // }

    public void printAST(ASTNode ast){
        // ASTNode ast  = stack.pop();
        if (ast.getValue() == null) System.out.println(ast.getName());
        else System.out.println(ast.getValue());
        
        if (ast.getChild() != null ) {
            printAST(ast.getChild());
        }
        if (ast.getSibling() != null ) {
            printAST(ast.getSibling());
        }

    }
}