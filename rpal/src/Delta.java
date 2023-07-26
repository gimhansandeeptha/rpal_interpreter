import java.util.ArrayList;
import java.util.List;
import java.util.Stack;


public class Delta extends ASTNode{
  private List<String> boundVariables;
  private Environment linkedEnvironments; 
  private Stack<ASTNode> body;
  private int index;
  
  public Delta(){
    setType(ASTNodeType.DELTA);
    boundVariables = new ArrayList<String>();
  }
  
  public Delta accept(NodeCopier nodeCopier){
    return nodeCopier.copyDelta(this);
  }
  
  
  @Override
  public String getValue(){
    return "[lambda closure: "+boundVariables.get(0)+": "+index+"]";
  }

  public List<String> getboundVariables(){
    return boundVariables;
  }
  
  public void addboundVariables(String boundVar){
    boundVariables.add(boundVar);
  }
  
  public void setboundVariables(List<String> boundVariables){
    this.boundVariables = boundVariables;
  }
  
  public Stack<ASTNode> getBody(){
    return body;
  }
  
  public void setBody(Stack<ASTNode> body){
    this.body = body;
  }
  
  public int getIndex(){
    return index;
  }

  public void setIndex(int index){
    this.index = index;
  }

  public Environment getlinkedEnvironments(){
    return linkedEnvironments;
  }

  public void setlinkedEnvironments(Environment linkedEnvironments){
    this.linkedEnvironments = linkedEnvironments;
  }
}
