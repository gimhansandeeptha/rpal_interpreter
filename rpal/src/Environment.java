import java.util.HashMap;
import java.util.Map;

public class Environment{
  private Environment parent;
  private Map<String, ASTNode> mapNameValue;
  
  public Environment(){
    mapNameValue = new HashMap<String, ASTNode>();
  }

  public Environment get_Parent(){
    return parent;
  }

  public void set_Parent(Environment parent){
    this.parent = parent;
  }
  
  /*
   * Find the binding of the given key
   * return ASTNode that corresponds to the key passed in as an argument
   * return null if no mapping was found
   */
  public ASTNode lookup(String key){
    ASTNode retValue = null;
    Map<String, ASTNode> map = mapNameValue;
    
    retValue = map.get(key);
    
    if(retValue!=null)
      return retValue.accept(new NodeCopier());
    
    if(parent!=null)
      return parent.lookup(key);
    else
      return null;
  }
  
  public void add_Mapping(String key, ASTNode value){
    mapNameValue.put(key, value);
  }
}