package Parser;

import java.io.IOException;
import java.util.Stack;
import Scanner.Scanner;
import Scanner.Token;
import Scanner.TokenType;
import Ast.ASTNodeType;
import Ast.AST;
import Ast.ASTNode;


public class Parser {
    private Scanner s;
    private Token currentToken;
    Stack<ASTNode> stack;

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
            n_children -= n_children;            
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
        if (isCurrentToken(TokenType.RESERVED, "let")){// E => let D in E => let
            readNT();
            procD();

            if (!isCurrentToken(TokenType.RESERVED, "in")){
                // throw new ParseException("E: expected 'in'");
            }
            readNT();
            procE();
            buildASTNode(ASTNodeType.LET, 2);
        }

        else if (isCurrentToken(TokenType.RESERVED, "fn")){ //fn Vb+ .E => lambda
            int treesToPop = 0;

            readNT();

            while (isCurrentTokenType(TokenType.IDENTIFIER) || isCurrentTokenType(TokenType.L_PAREN)){
                procVB();
                treesToPop++;
            }

            if (treesToPop == 0){
                //throw new ParseException("E: At least one 'Vb' expected");
            }

            if (!isCurrentToken(TokenType.OPERATOR, ".")){
                //throw new ParseException("E: expected '.'");
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
        if(isCurrentToken(TokenType.RESERVED, "where")){
            readNT();
            procDR();
            buildASTNode(ASTNodeType.WHERE, 2);
        }
    }

    private void procT() throws IOException{
        procTA();
        int treesToPop = 0;
        while(isCurrentToken(TokenType.OPERATOR, ",")){
            readNT();
            procTA();
            treesToPop += treesToPop;
        }
        if(treesToPop > 0){ 
            buildASTNode(ASTNodeType.TAU,treesToPop+1);
        }
    }

    

    public void procTA() throws IOException{
        procTA();
        while(isCurrentToken(TokenType.RESERVED, "aug")){
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
            }
            readNT();
            procTC();
            buildASTNode(ASTNodeType.CONDITIONAL, 3);
            
        }
    }

    private void procB() throws IOException{
        
        procBT();
        
        while (isCurrentToken(TokenType.RESERVED, "or")){
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
        if(isCurrentToken(TokenType.RESERVED,"not")){
            readNT();
            procBP();
            buildASTNode(ASTNodeType.NOT,1);
        }
        else procBP();
    }
    
    private void procBP() throws IOException{
        procA();
        if(isCurrentToken(TokenType.RESERVED,"gr") || isCurrentToken(TokenType.OPERATOR,">")){
            readNT();
            procA();
            buildASTNode(ASTNodeType.GR, 2);
        }
        else if(isCurrentToken(TokenType.RESERVED,"ge") || isCurrentToken(TokenType.OPERATOR,">=")){
            readNT();
            procA();
            buildASTNode(ASTNodeType.GE, 2);
        }
        else if(isCurrentToken(TokenType.RESERVED,"ls") || isCurrentToken(TokenType.OPERATOR,"<")){
            readNT();
            procA();
            buildASTNode(ASTNodeType.LS, 2);
        }
        else if(isCurrentToken(TokenType.RESERVED,"le") || isCurrentToken(TokenType.OPERATOR,"<=")){
            readNT();
            procA();
            buildASTNode(ASTNodeType.LE, 2);
        }
        else if(isCurrentToken(TokenType.RESERVED,"eq")){
            readNT();
            procA();
            buildASTNode(ASTNodeType.EQ, 2);
        }
        else if(isCurrentToken(TokenType.RESERVED,"ne")){
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
        while(isCurrentToken(TokenType.OPERATOR,"@")){
            readNT();
            if(!isCurrentTokenType(TokenType.IDENTIFIER)){
                // throw new ParseException();
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
                || isCurrentToken(TokenType.RESERVED, "true")
                || isCurrentToken(TokenType.RESERVED, "false")
                || isCurrentToken(TokenType.RESERVED, "nil")
                || isCurrentToken(TokenType.RESERVED, "dummy")
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
        else if(isCurrentToken(TokenType.RESERVED, "true")){
            createTerminalASTNode(ASTNodeType.TRUE, "true");
        }
        else if(isCurrentToken(TokenType.RESERVED, "false")){
            createTerminalASTNode(ASTNodeType.TRUE, "false");
        }
        else if(isCurrentTokenType(TokenType.L_PAREN)){
            readNT();
            procE();
            if(! isCurrentTokenType(TokenType.R_PAREN)){
                // throw new ParseException("RN: ')' expected");
            }
        }
        else if(isCurrentToken(TokenType.RESERVED, "dummy")){
            createTerminalASTNode(ASTNodeType.DUMMY, "dummy");
        }
    }

    

    
    private void procD() throws IOException{
        procDA();
        if (isCurrentToken(TokenType.RESERVED,"within" )){
            readNT();
            procD();
            buildASTNode(ASTNodeType.WITHIN,2);
        }
        
    }

    private void procDA() throws IOException{
        procDR();
        int treesToPop = 0;
        while(isCurrentToken(TokenType.RESERVED, "and")){
            readNT();
            procDR();
            treesToPop++;
        }
        if (treesToPop>0){
            buildASTNode(ASTNodeType.AND, treesToPop+1);
        }
    }

    private void procDR() throws IOException{

        if (isCurrentToken(TokenType.RESERVED, "rec")){

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
                    }
                    if (!isCurrentToken(TokenType.OPERATOR, "=")){
                        // throw new ParseException("DB: expexted =");
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
                }
                readNT();
            }
        }
    }

    private void procVL() throws IOException{
        if(!isCurrentTokenType(TokenType.IDENTIFIER)){
            // throw new ParseException("VL: Identifier Expected");
        }
        else{
            readNT();
            int treesToPop = 0;
            while( isCurrentToken(TokenType.OPERATOR, ",")){
                readNT();
                if(!isCurrentTokenType(TokenType.IDENTIFIER)){
                    // throw new ParseException("VL: Identifier Expected");
                }
                readNT();
                treesToPop+=treesToPop;   
            }
            if(treesToPop>0){
            buildASTNode(ASTNodeType.COMMA,treesToPop+1);
        }
        }
    }
}