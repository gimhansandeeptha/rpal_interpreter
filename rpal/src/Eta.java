//Represents the fixed-point resulting from the application
public class Eta extends ASTNode{
  private Delta delta;
  
  public Eta(){
    setType(ASTNodeType.ETA);
  }
  

  @Override
  public String getValue(){
    return "[eta closure: "+delta.getboundVariables().get(0)+": "+delta.getIndex()+"]";
  }
  
  public Eta accept(NodeCopier nodeCopier){
    return nodeCopier.copyEta(this);
  }

  public Delta get_Delta(){
    return delta;
  }

  public void setDelta(Delta delta){
    this.delta = delta;
  }
  
}