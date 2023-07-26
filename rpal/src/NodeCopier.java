import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/*
 * Make copies of nodes on value stack
 */
public class NodeCopier{
  
  public ASTNode copyASTNode(ASTNode astNode){
    ASTNode copy = new ASTNode();
    if(astNode.getChild()!=null)
      copy.setChild(astNode.getChild().accept(this));
    if(astNode.getSibling()!=null)
      copy.setSibling(astNode.getSibling().accept(this));
    copy.setType(astNode.getType());
    copy.setValue(astNode.getValue());
    copy.setSourceLine(astNode.getSourceLine());
    return copy;
  }
  
  public Beta copyBeta(Beta beta){
    Beta copy = new Beta();
    if(beta.getChild()!=null)
      copy.setChild(beta.getChild().accept(this));
    if(beta.getSibling()!=null)
      copy.setSibling(beta.getSibling().accept(this));
    copy.setType(beta.getType());
    copy.setValue(beta.getValue());
    copy.setSourceLine(beta.getSourceLine());
    
    Stack<ASTNode> thenBodyCopy = new Stack<ASTNode>();
    for(ASTNode thenBodyElement: beta.get_Then_Body()){
      thenBodyCopy.add(thenBodyElement.accept(this));
    }
    copy.set_Then_Body(thenBodyCopy);
    
    Stack<ASTNode> elseBodyCopy = new Stack<ASTNode>();
    for(ASTNode elseBodyElement: beta.get_Else_Body()){
      elseBodyCopy.add(elseBodyElement.accept(this));
    }
    copy.set_Else_Body(elseBodyCopy);
    
    return copy;
  }
  
  public Eta copyEta(Eta eta){
    Eta copy = new Eta();
    if(eta.getChild()!=null)
      copy.setChild(eta.getChild().accept(this));
    if(eta.getSibling()!=null)
      copy.setSibling(eta.getSibling().accept(this));
    copy.setType(eta.getType());
    copy.setValue(eta.getValue());
    copy.setSourceLine(eta.getSourceLine());
    
    copy.setDelta(eta.get_Delta().accept(this));
    
    return copy;
  }
  
  public Delta copyDelta(Delta delta){
    Delta copy = new Delta();
    if(delta.getChild()!=null)
      copy.setChild(delta.getChild().accept(this));
    if(delta.getSibling()!=null)
      copy.setSibling(delta.getSibling().accept(this));
    copy.setType(delta.getType());
    copy.setValue(delta.getValue());
    copy.setIndex(delta.getIndex());
    copy.setSourceLine(delta.getSourceLine());
    
    Stack<ASTNode> bodyCopy = new Stack<ASTNode>();
    for(ASTNode bodyElement: delta.getBody()){
      bodyCopy.add(bodyElement.accept(this));
    }
    copy.setBody(bodyCopy);
    
    List<String> boundVarsCopy = new ArrayList<String>();
    boundVarsCopy.addAll(delta.getboundVariables());
    copy.setboundVariables(boundVarsCopy);
    
    copy.setlinkedEnvironments(delta.getlinkedEnvironments());
    
    return copy;
  }
  
  public Tuple copyTuple(Tuple tuple){
    Tuple copy = new Tuple();
    if(tuple.getChild()!=null)
      copy.setChild(tuple.getChild().accept(this));
    if(tuple.getSibling()!=null)
      copy.setSibling(tuple.getSibling().accept(this));
    copy.setType(tuple.getType());
    copy.setValue(tuple.getValue());
    copy.setSourceLine(tuple.getSourceLine());
    return copy;
  }
}
