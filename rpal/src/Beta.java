import java.util.Stack;


public class Beta extends ASTNode{
  private Stack<ASTNode> thenBody;
  private Stack<ASTNode> elseBody;
  
  public Beta(){
    setType(ASTNodeType.BETA);
    thenBody = new Stack<ASTNode>();
    elseBody = new Stack<ASTNode>();
  }
  
  public Beta accept(NodeCopier nodeCopier){
    return nodeCopier.copy(this);
  }

  public Stack<ASTNode> getThenBody(){
    return thenBody;
  }

  public Stack<ASTNode> getElseBody(){
    return elseBody;
  }

  public void setThenBody(Stack<ASTNode> thenBody){
    this.thenBody = thenBody;
  }

  public void setElseBody(Stack<ASTNode> elseBody){
    this.elseBody = elseBody;
  }
  
}
