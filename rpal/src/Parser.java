import java.io.IOException;
import java.util.Stack;

/*
 * Recursive decent parcer for RPAL grammar
 * Get inputs from Scanner and build AST
 */
public class Parser {
    private Scanner s;
    private Token currentToken;
    Stack<ASTNode> stack;

    public Parser(Scanner s){
        this.s = s;
        stack = new Stack<ASTNode>();
      }
      
    public AST buildAST() throws IOException{
        startParse();
        return new AST(stack.pop());
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


    /*
     * Create a N-ary AST node
     * n_children => number of children of the new node
     */
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
        if (isCurrentToken(TokenType.KEYWORD, "let")){    //E -> ’let’ D ’in’ E
            readNT();
            procD();

            if (!isCurrentToken(TokenType.KEYWORD, "in")){
                
                System.out.println("Parse Exception1");
            }
            readNT();
            procE();
            buildASTNode(ASTNodeType.LET, 2);
        }

        else if (isCurrentToken(TokenType.KEYWORD, "fn")){ //E -> ’fn’ Vb+ ’.’ E
            int treesToPop = 0;

            readNT();

            while (isCurrentTokenType(TokenType.IDENTIFIER) || isCurrentTokenType(TokenType.L_PAREN)){
                procVB();
                treesToPop++;
            }

            if (treesToPop == 0){
            
               System.out.println("Parse Exception2"); 
            }

            if (!isCurrentToken(TokenType.OPERATOR, ".")){
                
                System.out.println("Parse Exception3");
            }
            readNT();
            procE();
            
            buildASTNode(ASTNodeType.LAMBDA, treesToPop+1);
        }
        
        else{  
            procEW(); //E -> Ew;
        }

    }

    private void procEW() throws IOException{
        procT();  // Ew -> T;
        if(isCurrentToken(TokenType.KEYWORD, "where")){ //Ew -> T ’where’ Dr
            readNT();
            procDR();
            buildASTNode(ASTNodeType.WHERE, 2);
        }
    }

    private void procT() throws IOException{
        procTA(); //T -> Ta ;
        int treesToPop = 0;
        while(isCurrentToken(TokenType.COMMA, ",")){ //T -> Ta ( ’,’ Ta )+
            readNT();
            procTA();
            treesToPop = treesToPop+1;
        }
        if(treesToPop > 0){ 
            buildASTNode(ASTNodeType.TAU,treesToPop+1);
        }
    }

    

    public void procTA() throws IOException{
        procTC(); //Ta -> Tc ;
        while(isCurrentToken(TokenType.KEYWORD, "aug")){ //Ta -> Ta ’aug’ Tc
            readNT();
            procTC();
            buildASTNode(ASTNodeType.AUG,2);
        }
    }
    
    public void procTC() throws IOException{
        procB(); //Tc -> B ;
        if (isCurrentToken(TokenType.OPERATOR, "->")){ //Tc -> B ’->’ Tc ’|’ Tc
            readNT();
            procTC();
            if(!isCurrentToken(TokenType.OPERATOR, "|")){
                
                System.out.println("Parse Exception4");
            }
            readNT();
            procTC();
            buildASTNode(ASTNodeType.CONDITIONAL, 3);
            
        }
    }

    private void procB() throws IOException{
        
        procBT(); //B -> Bt ;
        
        while (isCurrentToken(TokenType.KEYWORD, "or")){ //B ->B’or’ Bt
            readNT();
            procBT();
            buildASTNode(ASTNodeType.OR, 2);
        }
    }
    
    private void procBT() throws IOException{

        procBS();  //Bt -> Bs ;

        while (isCurrentToken(TokenType.OPERATOR, "&")){ //Bt -> Bt ’&’ Bs

            readNT();
            procBS();
            
            buildASTNode(ASTNodeType.BOOLAND, 2);
        }
    }

    private void procBS() throws IOException{
        if(isCurrentToken(TokenType.KEYWORD,"not")){ //Bs -> ’not’ Bp 
            readNT();
            procBP();
            buildASTNode(ASTNodeType.NOT,1);
        }
        else procBP();  // Bs -> Bp ;
    }
    
    private void procBP() throws IOException{
        procA();  //Bp -> A ;
        if(isCurrentToken(TokenType.KEYWORD,"gr") || isCurrentToken(TokenType.OPERATOR,">")){//Bp -> A (’gr’ | ’>’ ) A
            readNT();
            procA();
            buildASTNode(ASTNodeType.GR, 2);
        }
        else if(isCurrentToken(TokenType.KEYWORD,"ge") || isCurrentToken(TokenType.OPERATOR,">=")){//Bp -> A (’ge’ | ’>=’) A
            readNT();
            procA();
            buildASTNode(ASTNodeType.GE, 2);
        }
        else if(isCurrentToken(TokenType.KEYWORD,"ls") || isCurrentToken(TokenType.OPERATOR,"<")){//Bp -> A (’ls’ | ’<’ ) A
            readNT();
            procA();
            buildASTNode(ASTNodeType.LS, 2);
        }
        else if(isCurrentToken(TokenType.KEYWORD,"le") || isCurrentToken(TokenType.OPERATOR,"<=")){//Bp -> A (’le’ | ’<=’) A
            readNT();
            procA();
            buildASTNode(ASTNodeType.LE, 2);
        }
        else if(isCurrentToken(TokenType.KEYWORD,"eq")){//Bp -> A ’eq’ A
            readNT();
            procA();
            buildASTNode(ASTNodeType.EQ, 2);
        }
        else if(isCurrentToken(TokenType.KEYWORD,"ne")){//Bp -> A ’ne’ A
            readNT();
            procA();
            buildASTNode(ASTNodeType.NE, 2);
        }
    }


    private void procA() throws IOException{

        if (isCurrentToken(TokenType.OPERATOR, "+")){ //A ->A’+’ At
            readNT();
            procAT();
        }

        else if (isCurrentToken(TokenType.OPERATOR, "-")){//A -> A ’-’ At
            readNT();
            procAT();
            buildASTNode(ASTNodeType.NEG, 1);
        }

        else{
            procAT(); //A -> At ;
        }
        
        boolean plus = true;
        
        while (isCurrentToken(TokenType.OPERATOR, "+") || isCurrentToken(TokenType.OPERATOR, "-")){
            
            if (currentToken.getToken().equals("+")){ //A -> ’+’ At
                plus = true;
            }

            else if (currentToken.getToken().equals("-")){//A -> ’-’ At
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
        
        procAF(); //At -> Af ;

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

            if (mult){ //At -> At ’*’ Af
                buildASTNode(ASTNodeType.MUL, 2);
            }
            else { //At -> At ’/’ Af
                buildASTNode(ASTNodeType.DEV, 2);
            }
        }
    }

    private void procAF() throws IOException{
        procAP(); //Af -> Ap ;
        if (isCurrentToken(TokenType.OPERATOR,"**")){ //Af -> Ap ’**’ Af
            readNT();
            procAF();
            buildASTNode(ASTNodeType.EXP,2);
        }
    }


    private void procAP() throws IOException{
        procR(); // Ap -> R ;
        while(isCurrentToken(TokenType.OPERATOR,"@")){ //Ap -> Ap ’@’ ’<IDENTIFIER>’ R
            readNT();
            if(!isCurrentTokenType(TokenType.IDENTIFIER)){
                
                System.out.println("Parse Exception5");
            }
            readNT();
            procR();
            buildASTNode(ASTNodeType.AT,3);
        }
    }

    private void procR() throws IOException{
        procRN(); //R -> Rn ;
        readNT();
        while(isCurrentTokenType(TokenType.INTEGER) //R ->RRn
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
        if(isCurrentTokenType(TokenType.IDENTIFIER) //Rn -> ’<IDENTIFIER>’
            || isCurrentTokenType(TokenType.INTEGER) //-> ’<INTEGER>’
            || isCurrentTokenType(TokenType.STRING)){}//-> ’<STRING>’
        else if(isCurrentToken(TokenType.KEYWORD, "true")){//-> ’true’
            createTerminalASTNode(ASTNodeType.TRUE, "true");
        }
        else if(isCurrentToken(TokenType.KEYWORD, "false")){//-> ’false’
            createTerminalASTNode(ASTNodeType.FALSE, "false");
        }
        else if(isCurrentToken(TokenType.KEYWORD, "nil")){ //-> ’nil’
            createTerminalASTNode(ASTNodeType.NIL, "nil");
    }
        else if(isCurrentTokenType(TokenType.L_PAREN)){ //-> ’(’ E ’)’
            readNT();
            procE();
            if(! isCurrentTokenType(TokenType.R_PAREN)){
               
                System.out.println("Parse Exception6");
            }
        }
        else if (isCurrentToken(TokenType.KEYWORD,"nil")){
            createTerminalASTNode(ASTNodeType.NIL, "nil");
        }
        else if(isCurrentToken(TokenType.KEYWORD, "dummy")){//-> ’dummy’
            createTerminalASTNode(ASTNodeType.DUMMY, "dummy");
        }
    }

    

    
    private void procD() throws IOException{
        procDA(); //D -> Da ;
        if (isCurrentToken(TokenType.KEYWORD,"within" )){//D -> Da ’within’ D
            readNT();
            procD();
            buildASTNode(ASTNodeType.WITHIN,2);
        }
        
    }

    private void procDA() throws IOException{
        procDR();//Da -> Dr ;
        int treesToPop = 0;
        while(isCurrentToken(TokenType.KEYWORD, "and")){//Da -> Dr ( ’and’ Dr )+
            readNT();
            procDR();
            treesToPop++;
        }
        if (treesToPop>0){
            buildASTNode(ASTNodeType.AND, treesToPop+1);
        }
    }

    private void procDR() throws IOException{

        if (isCurrentToken(TokenType.KEYWORD, "rec")){ //Dr -> ’rec’ Db

            readNT();
            procDB();
            
            buildASTNode(ASTNodeType.REC, 1);
        }
        else{
            procDB();//Dr -> Db ;
        }
    }



    private void procDB() throws IOException{

        if (isCurrentTokenType(TokenType.L_PAREN)){//Db -> ’(’ D ’)’ ;
            procD();
            readNT();
        
            if (!isCurrentTokenType(TokenType.R_PAREN)){
                
                System.out.println("Parse Exception7");
            }
        
            readNT();
        }
        
        
        else if (isCurrentTokenType(TokenType.IDENTIFIER)){//Db -> ’<IDENTIFIER>’ Vb+ ’=’ E
            
            readNT();
            if (isCurrentToken(TokenType.OPERATOR, ",")){
                readNT();
                procVL();

                if (!isCurrentToken(TokenType.OPERATOR,"=")){
                    
                    System.out.println("Parse Exception8");
                }
                buildASTNode(ASTNodeType.COMMA, 2);
                readNT();
                procE();
                buildASTNode(ASTNodeType.EQUAL, 2);
            }
            else{ //Db -> Vl ’=’ E
                if (isCurrentToken(TokenType.OPERATOR, "=")){
                    readNT();
                    procE();
                    buildASTNode(ASTNodeType.EQUAL,2);
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
        if (isCurrentTokenType(TokenType.IDENTIFIER)){//Vb -> ’<IDENTIFIER>’
            readNT();
        }
        else if(isCurrentTokenType(TokenType.L_PAREN)){ //Vb -> ’(’ ’)’
            readNT();
            if (isCurrentTokenType(TokenType.R_PAREN)){
                createTerminalASTNode(ASTNodeType.PARAN, "");
                readNT();
            }
            else{ //Vb -> ’(’ Vl ’)’
                procVL();
                if(!isCurrentTokenType(TokenType.R_PAREN)){
                    
                    System.out.println("Parse Exception11");
                }
                readNT();
            }
        }
    }

    private void procVL() throws IOException{//
        if(!isCurrentTokenType(TokenType.IDENTIFIER)){
            
            System.out.println("Parse Exception12");
        }
        else{
            readNT();
            int treesToPop = 0;
            while( isCurrentToken(TokenType.COMMA, ",")){//Vl -> ’<IDENTIFIER>’ list ’,’
                readNT();
                if(!isCurrentTokenType(TokenType.IDENTIFIER)){
                    
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

    

    public void printAST(ASTNode ast){
        
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