import java.util.Stack;

public class CSEMachine{

  private Stack<ASTNode> valueStack;
  private Delta rootDelta;

  public CSEMachine(AST ast){
    if(!ast.isStandardized())
      throw new RuntimeException("AST has NOT been standardized!");
    rootDelta = ast.createDeltas();
    rootDelta.setlinkedEnvironments(new Environment());
    valueStack = new Stack<ASTNode>();
  }

  public void evalProgram(){
    processControlStack(rootDelta, rootDelta.getlinkedEnvironments());
  }

  private void processControlStack(Delta currentDelta, Environment currentEnv){
    
    Stack<ASTNode> controlStack = new Stack<ASTNode>();
    controlStack.addAll(currentDelta.getBody());
    
    while(!controlStack.isEmpty())
      processNodeCurrent(currentDelta, currentEnv, controlStack);
  }

  private void processNodeCurrent(Delta currentDelta, Environment currentEnv, Stack<ASTNode> currentControlStack){
    ASTNode node = currentControlStack.pop();
    if(applyBinaryOp(node))
      return;
    else if(applyUnaryOp(node))
      return;
    else{
      switch(node.getType()){
        case IDENTIFIER:
          handleIdentifiers(node, currentEnv);
          break;
        case NIL:
        case TAU:
          createTuple(node);
          break;
        case BETA:
          handleBeta((Beta)node, currentControlStack);
          break;
        case GAMMA:
          applyGamma(currentDelta, node, currentEnv, currentControlStack);
          break;
        case DELTA:
          ((Delta)node).setlinkedEnvironments(currentEnv); //Rule #2
          valueStack.push(node);
          break;
        default:

          valueStack.push(node);
          break;
      }
    }
  }

  
  private boolean applyBinaryOp(ASTNode rator){//Rule #6
    switch(rator.getType()){
      case PLUS:
      case MINUS:
      case MUL:
      case DEV:
      case EXP:
      case LS:
      case LE:
      case GR:
      case GE:
        binaryArithmeticOp(rator.getType());
        return true;
      case EQ:
      case NE:
        binaryLogicalEqNe(rator.getType());
        return true;
      case OR:
      case BOOLAND:
        binaryLogicalOrAnd(rator.getType());
        return true;
      case AUG:
        augTuples();
        return true;
      default:
        return false;
    }
  }

  private void binaryArithmeticOp(ASTNodeType type){
    ASTNode rand1 = valueStack.pop();
    ASTNode rand2 = valueStack.pop();
    if(rand1.getType()!=ASTNodeType.INTEGER || rand2.getType()!=ASTNodeType.INTEGER)
      EvaluationError.printErrorMessage(rand1.getSourceLine(), "Expected two integers; was given \""+rand1.getValue()+"\", \""+rand2.getValue()+"\"");

    ASTNode result = new ASTNode();
    result.setType(ASTNodeType.INTEGER);

    switch(type){
      case PLUS:
        result.setValue(Integer.toString(Integer.parseInt(rand1.getValue())+Integer.parseInt(rand2.getValue())));
        break;
      case MINUS:
        result.setValue(Integer.toString(Integer.parseInt(rand1.getValue())-Integer.parseInt(rand2.getValue())));
        break;
      case MUL:
        result.setValue(Integer.toString(Integer.parseInt(rand1.getValue())*Integer.parseInt(rand2.getValue())));
        break;
      case DEV:
        result.setValue(Integer.toString(Integer.parseInt(rand1.getValue())/Integer.parseInt(rand2.getValue())));
        break;
      case EXP:
        result.setValue(Integer.toString((int)Math.pow(Integer.parseInt(rand1.getValue()), Integer.parseInt(rand2.getValue()))));
        break;
      case LS:
        if(Integer.parseInt(rand1.getValue())<Integer.parseInt(rand2.getValue()))
          pushTrueNode();
        else
          pushFalseNode();
        return;
      case LE:
        if(Integer.parseInt(rand1.getValue())<=Integer.parseInt(rand2.getValue()))
          pushTrueNode();
        else
          pushFalseNode();
        return;
      case GR:
        if(Integer.parseInt(rand1.getValue())>Integer.parseInt(rand2.getValue()))
          pushTrueNode();
        else
          pushFalseNode();
        return;
      case GE:
        if(Integer.parseInt(rand1.getValue())>=Integer.parseInt(rand2.getValue()))
          pushTrueNode();
        else
          pushFalseNode();
        return;
      default:
        break;
    }
    valueStack.push(result);
  }

  private void binaryLogicalEqNe(ASTNodeType type){
    ASTNode rand1 = valueStack.pop();
    ASTNode rand2 = valueStack.pop();

    if(rand1.getType()==ASTNodeType.TRUE || rand1.getType()==ASTNodeType.FALSE){
      if(rand2.getType()!=ASTNodeType.TRUE && rand2.getType()!=ASTNodeType.FALSE)
        EvaluationError.printErrorMessage(rand1.getSourceLine(), "Cannot compare dissimilar types; was given \""+rand1.getValue()+"\", \""+rand2.getValue()+"\"");
      compareTruth(rand1, rand2, type);
      return;
    }

    if(rand1.getType()!=rand2.getType())
      EvaluationError.printErrorMessage(rand1.getSourceLine(), "Cannot compare dissimilar types; was given \""+rand1.getValue()+"\", \""+rand2.getValue()+"\"");

    if(rand1.getType()==ASTNodeType.STRING)
      compareStr(rand1, rand2, type);
    else if(rand1.getType()==ASTNodeType.INTEGER)
      compareInt(rand1, rand2, type);
    else
      EvaluationError.printErrorMessage(rand1.getSourceLine(), "Don't know how to " + type + " \""+rand1.getValue()+"\", \""+rand2.getValue()+"\"");

  }

  private void compareTruth(ASTNode rand1, ASTNode rand2, ASTNodeType type){
    if(rand1.getType()==rand2.getType())
      if(type==ASTNodeType.EQ)
        pushTrueNode();
      else
        pushFalseNode();
    else
      if(type==ASTNodeType.EQ)
        pushFalseNode();
      else
        pushTrueNode();
  }

  private void compareStr(ASTNode rand1, ASTNode rand2, ASTNodeType type){
    if(rand1.getValue().equals(rand2.getValue()))
      if(type==ASTNodeType.EQ)
        pushTrueNode();
      else
        pushFalseNode();
    else
      if(type==ASTNodeType.EQ)
        pushFalseNode();
      else
        pushTrueNode();
  }

  private void compareInt(ASTNode rand1, ASTNode rand2, ASTNodeType type){
    if(Integer.parseInt(rand1.getValue())==Integer.parseInt(rand2.getValue()))
      if(type==ASTNodeType.EQ)
        pushTrueNode();
      else
        pushFalseNode();
    else
      if(type==ASTNodeType.EQ)
        pushFalseNode();
      else
        pushTrueNode();
  }

  private void binaryLogicalOrAnd(ASTNodeType type){
    ASTNode rand1 = valueStack.pop();
    ASTNode rand2 = valueStack.pop();

    if((rand1.getType()==ASTNodeType.TRUE || rand1.getType()==ASTNodeType.FALSE) &&
        (rand2.getType()==ASTNodeType.TRUE || rand2.getType()==ASTNodeType.FALSE)){
      orAndTruthValues(rand1, rand2, type);
      return;
    }

    EvaluationError.printErrorMessage(rand1.getSourceLine(), "Don't know how to " + type + " \""+rand1.getValue()+"\", \""+rand2.getValue()+"\"");
  }

  private void orAndTruthValues(ASTNode rand1, ASTNode rand2, ASTNodeType type){
    if(type==ASTNodeType.OR){
      if(rand1.getType()==ASTNodeType.TRUE || rand2.getType()==ASTNodeType.TRUE)
        pushTrueNode();
      else
        pushFalseNode();
    }
    else{
      if(rand1.getType()==ASTNodeType.TRUE && rand2.getType()==ASTNodeType.TRUE)
        pushTrueNode();
      else
        pushFalseNode();
    }
  }

  private void augTuples(){
    ASTNode rand1 = valueStack.pop();
    ASTNode rand2 = valueStack.pop();

    if(rand1.getType()!=ASTNodeType.TUPLE)
      EvaluationError.printErrorMessage(rand1.getSourceLine(), "Cannot augment a non-tuple \""+rand1.getValue()+"\"");

    ASTNode childNode = rand1.getChild();
    if(childNode==null)
      rand1.setChild(rand2);
    else{
      while(childNode.getSibling()!=null)
        childNode = childNode.getSibling();
      childNode.setSibling(rand2);
    }
    rand2.setSibling(null);

    valueStack.push(rand1);
  }

  
  private boolean applyUnaryOp(ASTNode rator){//Rele #7
    switch(rator.getType()){
      case NOT:
        not();
        return true;
      case NEG:
        neg();
        return true;
      default:
        return false;
    }
  }

  private void not(){
    ASTNode rand = valueStack.pop();
    if(rand.getType()!=ASTNodeType.TRUE && rand.getType()!=ASTNodeType.FALSE)
      EvaluationError.printErrorMessage(rand.getSourceLine(), "Expecting a truthvalue; was given \""+rand.getValue()+"\"");

    if(rand.getType()==ASTNodeType.TRUE)
      pushFalseNode();
    else
      pushTrueNode();
  }

  private void neg(){
    ASTNode rand = valueStack.pop();
    if(rand.getType()!=ASTNodeType.INTEGER)
      EvaluationError.printErrorMessage(rand.getSourceLine(), "Expecting a truthvalue; was given \""+rand.getValue()+"\"");

    ASTNode result = new ASTNode();
    result.setType(ASTNodeType.INTEGER);
    result.setValue(Integer.toString(-1*Integer.parseInt(rand.getValue())));
    valueStack.push(result);
  }

  
  private void applyGamma(Delta currentDelta, ASTNode node, Environment currentEnv, Stack<ASTNode> currentControlStack){//Rule #3
    ASTNode rator = valueStack.pop();
    ASTNode rand = valueStack.pop();

    if(rator.getType()==ASTNodeType.DELTA){
      Delta nextDelta = (Delta) rator;
      

      Environment newEnv = new Environment();
      newEnv.set_Parent(nextDelta.getlinkedEnvironments());
      
      
      if(nextDelta.getboundVariables().size()==1){//Rule #11
        newEnv.add_Mapping(nextDelta.getboundVariables().get(0), rand);
      }
      
      else{//Rule #11
        if(rand.getType()!=ASTNodeType.TUPLE)
          EvaluationError.printErrorMessage(rand.getSourceLine(), "Expected a tuple; was given \""+rand.getValue()+"\"");
        
        for(int i = 0; i < nextDelta.getboundVariables().size(); i++){
          newEnv.add_Mapping(nextDelta.getboundVariables().get(i), getNthTupleElement((Tuple)rand, i+1));
        }
      }
      
      processControlStack(nextDelta, newEnv);
      return;
    }
    else if(rator.getType()==ASTNodeType.Y){//Rule #12
      
      if(rand.getType()!=ASTNodeType.DELTA)
        EvaluationError.printErrorMessage(rand.getSourceLine(), "Expected a Delta; was given \""+rand.getValue()+"\"");
      
      Eta etaNode = new Eta();
      etaNode.setDelta((Delta)rand);
      valueStack.push(etaNode);
      return;
    }
    else if(rator.getType()==ASTNodeType.ETA){//Rule #13
      
      valueStack.push(rand);
      valueStack.push(rator);
      valueStack.push(((Eta)rator).get_Delta());
      
      currentControlStack.push(node);
      currentControlStack.push(node);
      return;
    }
    else if(rator.getType()==ASTNodeType.TUPLE){
      tupleSelection((Tuple)rator, rand);
      return;
    }
    else if(evaluateReserved(rator, rand, currentControlStack))
      return;
    else
      EvaluationError.printErrorMessage(rator.getSourceLine(), "Don't know how to evaluate \""+rator.getValue()+"\"");
  }

  private boolean evaluateReserved(ASTNode rator, ASTNode rand, Stack<ASTNode> currentControlStack){
    switch(rator.getValue()){
      case "Isinteger":
        checkTypeAndPushTrueOrFalse(rand, ASTNodeType.INTEGER);
        return true;
      case "Isstring":
        checkTypeAndPushTrueOrFalse(rand, ASTNodeType.STRING);
        return true;
      case "Isdummy":
        checkTypeAndPushTrueOrFalse(rand, ASTNodeType.DUMMY);
        return true;
      case "Isfunction":
        checkTypeAndPushTrueOrFalse(rand, ASTNodeType.DELTA);
        return true;
      case "Istuple":
        checkTypeAndPushTrueOrFalse(rand, ASTNodeType.TUPLE);
        return true;
      case "Istruthvalue":
        if(rand.getType()==ASTNodeType.TRUE||rand.getType()==ASTNodeType.FALSE)
          pushTrueNode();
        else
          pushFalseNode();
        return true;
      case "Stem":
        stem(rand);
        return true;
      case "Stern":
        stern(rand);
        return true;
      case "Conc":
      case "conc": 
        conc(rand, currentControlStack);
        return true;
      case "Print":
      case "print": 
        printNode(rand);
        pushDummyNode();
        return true;
      case "ItoS":
        itos(rand);
        return true;
      case "Order":
        order(rand);
        return true;
      case "Null":
        isTupleNull(rand);
        return true;
      default:
        return false;
    }
  }

  private void checkTypeAndPushTrueOrFalse(ASTNode rand, ASTNodeType type){
    if(rand.getType()==type)
      pushTrueNode();
    else
      pushFalseNode();
  }

  private void pushTrueNode(){
    ASTNode trueNode = new ASTNode();
    trueNode.setType(ASTNodeType.TRUE);
    trueNode.setValue("true");
    valueStack.push(trueNode);
  }
  
  private void pushFalseNode(){
    ASTNode falseNode = new ASTNode();
    falseNode.setType(ASTNodeType.FALSE);
    falseNode.setValue("false");
    valueStack.push(falseNode);
  }

  private void pushDummyNode(){
    ASTNode falseNode = new ASTNode();
    falseNode.setType(ASTNodeType.DUMMY);
    valueStack.push(falseNode);
  }

  private void stem(ASTNode rand){
    if(rand.getType()!=ASTNodeType.STRING)
      EvaluationError.printErrorMessage(rand.getSourceLine(), "Expected a string; was given \""+rand.getValue()+"\"");
    
    if(rand.getValue().isEmpty())
      rand.setValue("");
    else
      rand.setValue(rand.getValue().substring(0,1));
    
    valueStack.push(rand);
  }

  private void stern(ASTNode rand){
    if(rand.getType()!=ASTNodeType.STRING)
      EvaluationError.printErrorMessage(rand.getSourceLine(), "Expected a string; was given \""+rand.getValue()+"\"");
    
    if(rand.getValue().isEmpty() || rand.getValue().length()==1)
      rand.setValue("");
    else
      rand.setValue(rand.getValue().substring(1));
    
    valueStack.push(rand);
  }

  private void conc(ASTNode rand1, Stack<ASTNode> currentControlStack){
    currentControlStack.pop();
    ASTNode rand2 = valueStack.pop();
    if(rand1.getType()!=ASTNodeType.STRING || rand2.getType()!=ASTNodeType.STRING)
      EvaluationError.printErrorMessage(rand1.getSourceLine(), "Expected two strings; was given \""+rand1.getValue()+"\", \""+rand2.getValue()+"\"");

    ASTNode result = new ASTNode();
    result.setType(ASTNodeType.STRING);
    result.setValue(rand1.getValue()+rand2.getValue());
    
    valueStack.push(result);
  }

  private void itos(ASTNode rand){
    if(rand.getType()!=ASTNodeType.INTEGER)
      EvaluationError.printErrorMessage(rand.getSourceLine(), "Expected an integer; was given \""+rand.getValue()+"\"");
    
    rand.setType(ASTNodeType.STRING);
    valueStack.push(rand);
  }

  private void order(ASTNode rand){
    if(rand.getType()!=ASTNodeType.TUPLE)
      EvaluationError.printErrorMessage(rand.getSourceLine(), "Expected a tuple; was given \""+rand.getValue()+"\"");

    ASTNode result = new ASTNode();
    result.setType(ASTNodeType.INTEGER);
    result.setValue(Integer.toString(getNumChildren(rand)));
    
    valueStack.push(result);
  }

  private void isTupleNull(ASTNode rand){
    if(rand.getType()!=ASTNodeType.TUPLE)
      EvaluationError.printErrorMessage(rand.getSourceLine(), "Expected a tuple; was given \""+rand.getValue()+"\"");

    if(getNumChildren(rand)==0)
      pushTrueNode();
    else
      pushFalseNode();
  }

  
  private void tupleSelection(Tuple rator, ASTNode rand){//Rule #10
    if(rand.getType()!=ASTNodeType.INTEGER)
      EvaluationError.printErrorMessage(rand.getSourceLine(), "Non-integer tuple selection with \""+rand.getValue()+"\"");

    ASTNode result = getNthTupleElement(rator, Integer.parseInt(rand.getValue()));
    if(result==null)
      EvaluationError.printErrorMessage(rand.getSourceLine(), "Tuple selection index "+rand.getValue()+" out of bounds");

    valueStack.push(result);
  }

  /*
   *return n th element of the tuple
   */
  private ASTNode getNthTupleElement(Tuple tupleNode, int n){
    ASTNode childNode = tupleNode.getChild();
    for(int i=1;i<n;++i){
      if(childNode==null)
        break;
      childNode = childNode.getSibling();
    }
    return childNode;
  }

  private void handleIdentifiers(ASTNode node, Environment currentEnv){//Rule #1
    if(currentEnv.lookup(node.getValue())!=null) 
      valueStack.push(currentEnv.lookup(node.getValue()));
    else if(isReserved(node.getValue()))
      valueStack.push(node);
    else
      EvaluationError.printErrorMessage(node.getSourceLine(), "Undeclared identifier \""+node.getValue()+"\"");
  }

  
  private void createTuple(ASTNode node){//Rule #8
    int numChildren = getNumChildren(node);
    Tuple tupleNode = new Tuple();
    if(numChildren==0){
      valueStack.push(tupleNode);
      return;
    }

    ASTNode childNode = null, tempNode = null;
    for(int i=0;i<numChildren;++i){
      if(childNode==null)
        childNode = valueStack.pop();
      else if(tempNode==null){
        tempNode = valueStack.pop();
        childNode.setSibling(tempNode);
      }
      else{
        tempNode.setSibling(valueStack.pop());
        tempNode = tempNode.getSibling();
      }
    }
    tempNode.setSibling(null);
    tupleNode.setChild(childNode);
    valueStack.push(tupleNode);
  }

  
  private void handleBeta(Beta node, Stack<ASTNode> currentControlStack){//Rule #9
    ASTNode conditionResultNode = valueStack.pop();

    if(conditionResultNode.getType()!=ASTNodeType.TRUE && conditionResultNode.getType()!=ASTNodeType.FALSE)
      EvaluationError.printErrorMessage(conditionResultNode.getSourceLine(), "Expecting a truthvalue; found \""+conditionResultNode.getValue()+"\"");

    if(conditionResultNode.getType()==ASTNodeType.TRUE)
      currentControlStack.addAll(node.get_Then_Body());
    else
      currentControlStack.addAll(node.get_Else_Body());
  }

  private int getNumChildren(ASTNode node){
    int numChildren = 0;
    ASTNode childNode = node.getChild();
    while(childNode!=null){
      numChildren++;
      childNode = childNode.getSibling();
    }
    return numChildren;
  }
  
  private void printNode(ASTNode rand){
    String evaluationResult = rand.getValue();
    evaluationResult = evaluationResult.replace("\\t", "\t");
    evaluationResult = evaluationResult.replace("\\n", "\n");
    System.out.print(evaluationResult);
  }


  private boolean isReserved(String value){
    switch(value){
      case "Isinteger":
      case "Isstring":
      case "Istuple":
      case "Isdummy":
      case "Istruthvalue":
      case "Isfunction":
      case "ItoS":
      case "Order":
      case "Conc":
      case "conc":
      case "Stern":
      case "Stem":
      case "Null":
      case "Print":
      case "print":
      case "neg":
        return true;
    }
    return false;
  }

}