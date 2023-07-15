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

    public AST buildAST(){
        startParse();
        return new AST(stack.pop());

    }

    public void startParse(){
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




    private void procE(){
        if (isCurrentToken(TokenType.RESERVED, "let")){// E => let D in E => let
            readNT();
            procD();

            if (!isCurrentToken(TokenType.RESERVED, "in")){
                throw new ParseException("expected 'in'");
            }
            readNT();
            procE();
            buildASTNode(ASTNodeType.LET, 2);
        }

        else if (isCurrentToken(tokenType.RESERVED, "fn")){ //fn Vb+ .E => lambda
            
            int treesToPop = 0;

            readNT();

            while (isCurrentToken(TokenType.IDENTIFIER) || isCurrentTokenType(TokenType.L_PAREN)){
                procVB();
                treesToPop++;
            }

            if (treesToPop == 0){
                throw new Parse
            }
        }
    }
}