package Ast;
// import node copier from cse machine

public class ASTNode{
    private ASTNodeType type;
    private String value;
    private ASTNode child;
    private ASTNode sibling;
    private int sourceLine;

    public ASTNode(){}

    public String getName(){
        return type.name();
    }

    public ASTNodeType getType(){
        return type;
    }
    
    public void setType(ASTNodeType nodeType){
        this.type = nodeType;
    }

    public ASTNode getChild(){
        return child;
    }

    public void setChild(ASTNode node){
        this.child = node;
    }

    public ASTNode getSibling(){
        return sibling;
    }

    public void setSibling(ASTNode node){
        this.sibling = node;
    }

    public String getValue(){
        return value;
    }

    public void setValue(String value){
        this.value = value;
    }

    //  add accept method

    public int getSourceLine(){
        return sourceLine;
    }

    public void setSourceLine(int lineNumber){
        this.sourceLine = lineNumber;
    }

}