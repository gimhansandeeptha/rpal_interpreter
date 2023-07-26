import java.util.Stack;

/*
 * Evaluates the conditionals.
 */
public class Beta extends ASTNode{
  private Stack<ASTNode> then_Body;
  private Stack<ASTNode> else_Body;
  
  public Beta(){
    setType(ASTNodeType.BETA);
    then_Body = new Stack<ASTNode>();
    else_Body = new Stack<ASTNode>();
  }
  
  public Beta accept(NodeCopier nodeCopier){
    return nodeCopier.copyBeta(this);
  }

  public Stack<ASTNode> get_Then_Body(){
    return then_Body;
  }

  public Stack<ASTNode> get_Else_Body(){
    return else_Body;
  }

  public void set_Then_Body(Stack<ASTNode> then_Body){
    this.then_Body = then_Body;
  }

  public void set_Else_Body(Stack<ASTNode> else_Body){
    this.else_Body = else_Body;
  }
  
}
